#!/usr/bin/env bb
;; Generate CSV views of docs/entity-attribute-model.edn for import into a
;; spreadsheet (Google Sheets: File > Import > Upload, one file per tab).
;;
;; Two outputs:
;;   docs/diagrams/entity-attributes.csv    — one row per (entity, attribute)
;;   docs/diagrams/entity-relationships.csv — one row per relationship edge
;;
;; The EDN is the source of truth; these CSVs are derived views. Quoting is
;; handled by clojure.data.csv (commas, quotes, newlines in descriptions are
;; escaped correctly). Output is deterministic — rows are sorted, no timestamps —
;; so re-runs are byte-identical and diffs reflect data changes only.
;;
;; Usage:
;;   bb tools/edn_to_csv.clj           # write both CSVs
;;   bb tools/edn_to_csv.clj --stdout  # print the attributes CSV, write nothing

(ns edn-to-csv
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(def repo-root
  (-> *file* io/file .getParentFile .getParentFile .getCanonicalPath))

(def edn-path "docs/entity-attribute-model.edn")
(def attrs-out "docs/diagrams/entity-attributes.csv")
(def rels-out  "docs/diagrams/entity-relationships.csv")

(defn s
  "Coerce a value to a CSV cell string; nil -> empty."
  [v] (if (nil? v) "" (str v)))

(defn kw->s
  "Keyword -> string. Namespaced :example/var -> \"example/var\"; :var -> \"var\".
   Non-keywords (e.g. a string :vision value) pass through via str."
  [k] (cond
        (nil? k)     ""
        (keyword? k) (if (namespace k) (str (namespace k) "/" (name k)) (name k))
        :else        (str k)))

(defn attr-sort [[k _]] [(if (= k :_id) 0 1) (name k)])

(def attr-headers
  ["Entity" "Source" "Cardinality" "Attribute" "Type" "Refs"
   "Required" "Coverage" "Status" "Verified" "Description" "Evidence / Gap notes"])

(defn attr-rows [entities]
  (for [[ek e] (sort-by (comp name key) entities)
        [ak a] (sort-by attr-sort (:attrs e))
        :let [st (:status a)
              evidence (or (:evidence st)
                           (when (= :gap (:state st))
                             (str "vision: " (kw->s (:vision st))
                                  (when (:context st) (str " — " (:context st))))))]]
    [(kw->s ek)
     (s (:source e))
     (s (:cardinality e))
     (kw->s ak)
     (kw->s (:type a))
     (s (kw->s (or (:refs a) (:schema a))))
     (if (:required? a) "yes" "no")
     (s (:coverage a))
     (kw->s (:state st))
     (s (:verified st))
     (s (:description a))
     (s evidence)]))

(def rel-headers ["From" "To" "Via" "Cardinality" "Label"])

(defn rel-rows [relationships]
  (for [r (sort-by (juxt (comp name :from) (comp name :to) (comp str :via)) relationships)]
    [(kw->s (:from r)) (kw->s (:to r)) (kw->s (:via r)) (kw->s (:cardinality r)) (s (:label r))]))

(defn write-csv! [path headers rows]
  (let [f (io/file repo-root path)]
    (io/make-parents f)
    (with-open [w (io/writer f)]
      (csv/write-csv w (cons headers rows)))
    (count rows)))

(defn -main [& args]
  (let [args (set args)
        model (edn/read-string (slurp (io/file repo-root edn-path)))
        arows (attr-rows (:entities model))
        rrows (rel-rows (:relationships model))]
    (if (args "--stdout")
      (csv/write-csv *out* (cons attr-headers arows))
      (do
        (write-csv! attrs-out attr-headers arows)
        (write-csv! rels-out rel-headers rrows)
        (println "wrote" attrs-out (str "(" (count arows) " attribute rows)"))
        (println "wrote" rels-out (str "(" (count rrows) " relationship rows)"))))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
