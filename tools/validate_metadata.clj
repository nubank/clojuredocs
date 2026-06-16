(ns tools.validate-metadata
  "Validate OKF + RDF YAML frontmatter across docs/ against docs/metadata-schema.edn.

  Run from the repo root:

      bb tools/validate_metadata.clj

  Exits non-zero if any document has an error. Warnings do not fail the run.
  See docs/rfcs/okf-metadata-rfc.md for the convention this enforces."
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [cheshire.core :as json]))

(def schema (edn/read-string (slurp "docs/metadata-schema.edn")))

;; Hints built from the schema so error messages list the actual allowed values.
(def types-hint    (str/join ", " (sort (:types schema))))
(def maturity-hint (str/join "/"  (sort (:review-maturity schema))))

(defn md-files []
  (->> (file-seq (io/file "docs"))
       (filter #(.isFile %))
       (filter #(str/ends-with? (.getName %) ".md"))
       (sort-by #(.getPath %))))

(defn relpath [f] (str/replace (.getPath f) #"^\./" ""))

(defn extract-frontmatter
  "The YAML text between the opening `---` line and the next `---` line, or nil."
  [content]
  (when (str/starts-with? content "---\n")
    (let [body (subs content 4)
          idx  (str/index-of body "\n---")]
      (when idx (subs body 0 idx)))))

(defn raw-value
  "The verbatim value text for key `k` from the raw frontmatter, unquoted.
  We validate dates against this, not the parsed value: clj-yaml/SnakeYAML is
  lenient and silently rolls 2026-13-45 over into a real Date, so the parsed
  value can't catch a bad date — the authored text can."
  [fm k]
  (when-let [m (re-find (re-pattern (str "(?m)^" (name k) ":[ \\t]*(.+?)[ \\t]*$")) fm)]
    (str/replace (second m) #"^['\"]|['\"]$" "")))

(defn date-ok?
  "True if the leading YYYY-MM-DD of `s` is a real calendar date (rejects
  2026-13-45, requires zero-padding)."
  [s]
  (boolean
   (and (string? s)
        (try (java.time.LocalDate/parse (subs s 0 (min 10 (count s))))
             true
             (catch Exception _ false)))))

(defn validate-concept [path content]
  (let [errs (atom []) warns (atom [])
        err! #(swap! errs conj %) warn! #(swap! warns conj %)
        fm (extract-frontmatter content)]
    (if-not fm
      (err! "no YAML frontmatter — add a `---` … `---` block at the very top of the file, with at least `type:` (see CLAUDE.md)")
      (let [data (try (yaml/parse-string fm)
                      (catch Exception e
                        (err! (str "frontmatter is not valid YAML: " (.getMessage e)
                                   " — check indentation and that values with `:` or `#` are quoted"))
                        nil))]
        (when data
          (let [t (:type data)]
            (cond
              (or (nil? t) (str/blank? (str t)))
              (err! (str "missing required key `type` — add `type:` set to one of: " types-hint
                         " (extend docs/metadata-schema.edn to add a new one)"))
              (not (contains? (:types schema) (str t)))
              (warn! (str "type \"" t "\" is not in the taxonomy (" types-hint
                          ") — fix the value, or add it to :types in docs/metadata-schema.edn if intentional"))))
          (when-let [rm (:review_maturity data)]
            (when-not (contains? (:review-maturity schema) (str rm))
              (err! (str "invalid review_maturity \"" rm "\" — use one of " maturity-hint
                         " (L0 AI-generated → L4 human-endorsed; see CLAUDE.md)"))))
          (doseq [k (:date-keys schema)]
            (when (contains? data k)
              (let [raw (raw-value fm k)]
                (when-not (date-ok? raw)
                  (err! (str (name k) ": " (pr-str (or raw (get data k)))
                             " is not a valid date — use a zero-padded ISO date `YYYY-MM-DD`, e.g. 2026-06-16"
                             " (pad single-digit months/days: 2026-6-6 → 2026-06-06)"))))))
          (when (and (contains? data :tags) (not (sequential? (:tags data))))
            (warn! "`tags` must be a YAML list in square brackets, e.g. tags: [data-model, edn-schema]"))
          (doseq [k (keys data)]
            (when-not (contains? (:known-keys schema) k)
              (warn! (str "unknown key `" (name k) "` — check the spelling against the field list in CLAUDE.md,"
                          " or add it to :known-keys in docs/metadata-schema.edn")))))))
    {:path path :errors @errs :warnings @warns}))

(defn validate-bundle-root [content]
  (let [errs (atom []) err! #(swap! errs conj %)
        fm (extract-frontmatter content)]
    (if-not fm
      (err! "bundle root index.md must declare okf_version in frontmatter")
      (let [data (yaml/parse-string fm)
            extra (disj (set (keys data)) :okf_version)]
        (when-not (= (str (:okf_version data)) (:okf-version schema))
          (err! (str "okf_version must be \"" (:okf-version schema) "\"")))
        (when (seq extra)
          (err! (str "index.md frontmatter may contain only okf_version; found also "
                     (str/join ", " (map name extra)))))))
    {:path (:bundle-root schema) :errors @errs :warnings []}))

(defn validate-context
  "Every known key (minus :context-exempt) must have a JSON-LD @context mapping."
  []
  (let [ctx (-> (:context-file schema) slurp json/parse-string (get "@context"))
        expected (remove (:context-exempt schema) (:known-keys schema))
        missing (remove #(contains? ctx (name %)) expected)]
    (map #(str "docs/context.jsonld is missing a JSON-LD mapping for `" (name %)
               "` — add it under @context (e.g. \"" (name %) "\": \"dcterms:" (name %) "\")") missing)))

(defn run []
  (let [files     (md-files)
        reserved? #(contains? (:reserved-files schema) (.getName %))
        grouped   (group-by reserved? files)
        concepts  (get grouped false [])
        reserved  (get grouped true [])
        results   (mapv #(validate-concept (relpath %) (slurp %)) concepts)
        ;; only docs/index.md gets bundle-root treatment; other reserved files are noted
        root-res  (when-let [root (first (filter #(= (relpath %) (:bundle-root schema)) reserved))]
                    (validate-bundle-root (slurp root)))
        all       (cond-> results root-res (conj root-res))
        ctx-warns (validate-context)]
    (doseq [{:keys [path errors warnings]} (sort-by :path all)]
      (println (if (seq errors) "FAIL" "PASS") path)
      (doseq [e errors]   (println "    ERROR:" e))
      (doseq [w warnings] (println "    warn: " w)))
    (doseq [w ctx-warns] (println "FAIL docs/context.jsonld\n    ERROR:" w))
    (let [err-files (count (filter #(seq (:errors %)) all))
          n-warn    (reduce + (map #(count (:warnings %)) all))
          n-ctx     (count ctx-warns)]
      (println)
      (printf "%d documents checked · %d with errors · %d warnings · %d context gaps\n"
              (count all) err-files n-warn n-ctx)
      (if (or (pos? err-files) (pos? n-ctx))
        (do (println "FAILED") 1)
        (do (println "OK") 0)))))

;; Run when invoked as a script (bb tools/validate_metadata.clj), not when required.
(when (= *file* (System/getProperty "babashka.file"))
  (System/exit (run)))
