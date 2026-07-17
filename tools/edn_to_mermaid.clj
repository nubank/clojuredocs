#!/usr/bin/env bb
;; Generate a Mermaid erDiagram from docs/entity-attribute-model.edn.
;;
;; The EDN is the source of truth (REPL-verified present-state). This script
;; derives the entity boxes from :entities/:attrs and the edges from
;; :relationships, sanitizing every identifier BY CONSTRUCTION so the emitted
;; Mermaid is guaranteed to parse, then self-lints to prove it.
;;
;; Usage:
;;   bb tools/edn_to_mermaid.clj            # write docs/diagrams/entity-attribute-er.md
;;   bb tools/edn_to_mermaid.clj --check    # also POST to Kroki to confirm it renders
;;   bb tools/edn_to_mermaid.clj --stdout   # print the markdown, don't write
;;
;; Deterministic: dates come from the EDN's :generated, never wall-clock, so
;; re-runs are byte-identical and diffs reflect data changes only.

(ns edn-to-mermaid
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def repo-root
  (-> *file* io/file .getParentFile .getParentFile .getCanonicalPath))

(def edn-path "docs/entity-attribute-model.edn")
(def out-path "docs/diagrams/entity-attribute-er.md")

(def ident-re #"[A-Za-z_][A-Za-z0-9_]*")

;; :many-to-one means :from is the "many" side, :to is the "one" side.
;; Crow's-foot tokens read left-to-right as :from <token> :to.
(def cardinality->token
  {:many-to-one  "}o--||"
   :many-to-many "}o--o{"
   :one-to-one   "||--||"})

(defn assert-ident
  "Return s if it is a legal Mermaid identifier, else throw. This is the
   by-construction guarantee: nothing leaves a sanitizer without matching."
  [s ctx]
  (when-not (re-matches ident-re s)
    (throw (ex-info (str "Invalid Mermaid identifier " (pr-str s) " (" ctx ")") {:s s})))
  s)

(defn ent-name
  "Entity keyword -> UPPER_SNAKE Mermaid identifier. :example-history -> EXAMPLE_HISTORY."
  [kw]
  (-> (name kw) str/upper-case (str/replace #"[^A-Za-z0-9]" "_") (assert-ident (str kw))))

(defn ident
  "Attr/type keyword -> Mermaid identifier. :_id -> _id, :object-id -> object_id."
  [kw ctx]
  (let [s (str/replace (name kw) #"[^A-Za-z0-9_]" "_")
        s (if (re-matches #"[A-Za-z_].*" s) s (str "_" s))]
    (assert-ident s ctx)))

(defn comment-text
  "Sanitize a description into a single-line, bounded Mermaid comment.
   Strips characters that can break Mermaid's quoted-comment grammar — double
   quotes, braces, pipes, backslashes — and normalizes Unicode dashes/quotes to
   ASCII, so the result is safe by construction (no round-trip needed)."
  [s]
  (let [t (-> (or s "")
              (str/replace #"[‒-―]" "-")    ; figure/en/em-dash, horizontal bar -> hyphen
              (str/replace #"[‘’]" "'")     ; smart single quotes -> apostrophe
              (str/replace #"[“”]" "")      ; smart double quotes -> drop
              (str/replace #"[\"{}|\\\r\n]" " ")      ; mermaid-hostile chars -> space
              (str/replace #"\s+" " ")
              str/trim)]
    (if (> (count t) 68) (str (subs t 0 65) "...") t)))

(defn attr-line
  "One attribute row inside an entity block: `type name [PK|FK] \"comment\"`."
  [ent-kw [attr-kw {:keys [type schema description status]}]]
  (let [ref?  (= type :ref)
        tname (ident (if (and ref? schema) schema type) (str ent-kw attr-kw " type"))
        aname (ident attr-kw (str ent-kw " attr"))
        key   (cond (= attr-kw :_id) "PK" ref? "FK" :else nil)
        gap?  (= :gap (:state status))
        note  (cond-> (comment-text description) gap? (->> (str "[gap] ")))]
    (->> ["       " tname " " aname (when key (str " " key)) " \"" (comment-text note) "\""]
         (remove nil?) (apply str))))

(defn attr-sort
  "Stable, deterministic attribute order: _id first, then alphabetical."
  [[k _]] [(if (= k :_id) 0 1) (name k)])

(defn entity-block [[ent-kw {:keys [attrs]}]]
  (concat
   [(str "    " (ent-name ent-kw) " {")]
   (map #(attr-line ent-kw %) (sort-by attr-sort attrs))
   ["    }"]))

(defn rel-line [{:keys [from to cardinality label]}]
  (let [token (or (cardinality->token cardinality)
                  (throw (ex-info (str "Unknown cardinality " cardinality) {})))]
    (str "    " (ent-name from) " " token " " (ent-name to)
         " : \"" (comment-text label) "\"")))

;; relationships are flat lines; entity blocks follow. Keep them grouped for
;; readability: edges first (the shape of the graph), then the boxes.
(defn mermaid [{:keys [entities relationships]}]
  (let [ents (sort-by (comp name key) entities)
        rels (sort-by (juxt (comp name :from) (comp name :to) (comp str :via)) relationships)
        ent-set (set (keys entities))]
    (doseq [{:keys [from to]} rels]
      (when-not (and (ent-set from) (ent-set to))
        (throw (ex-info (str "Relationship references unknown entity: " from " -> " to) {}))))
    (str/join "\n"
              (concat ["erDiagram"]
                      (map rel-line rels)
                      [""]
                      (mapcat entity-block ents)))))

(defn lint!
  "Deterministic guarantee that the emitted Mermaid is well-formed:
   braces balance, every relationship token is from the known set, and the
   first line is the erDiagram header. Identifier legality is already enforced
   at construction by assert-ident; this re-checks the serialized text."
  [mermaid-str]
  (let [;; crow's-foot tokens contain { } too — exclude relationship lines from the brace check
        block-lines (->> (str/split-lines mermaid-str)
                         (remove #(re-find #"--" %)))
        bo (count (re-seq #"\{" (str/join "\n" block-lines)))
        bc (count (re-seq #"\}" (str/join "\n" block-lines)))]
    (when-not (= bo bc)
      (throw (ex-info (str "Unbalanced entity-block braces: " bo " open, " bc " close") {})))
    (when-not (str/starts-with? mermaid-str "erDiagram")
      (throw (ex-info "Mermaid output missing erDiagram header" {})))
    (let [bad-tokens (->> (str/split-lines mermaid-str)
                          (filter #(re-find #"--" %))
                          (remove (fn [l] (some #(str/includes? l %) (vals cardinality->token)))))]
      (when (seq bad-tokens)
        (throw (ex-info (str "Relationship line(s) with unknown cardinality token:\n"
                             (str/join "\n" bad-tokens)) {}))))
    ;; Braces may appear ONLY on entity delimiter lines (`NAME {` / `}`). A brace
    ;; that leaked into an attribute comment would break Mermaid's grammar.
    (let [stray (->> (str/split-lines mermaid-str)
                     (remove #(re-find #"--" %))
                     (remove #(re-matches #"\s*\S+ \{" %))
                     (remove #(re-matches #"\s*\}" %))
                     (filter #(re-find #"[{}]" %)))]
      (when (seq stray)
        (throw (ex-info (str "Brace inside attribute/comment line(s) — would break Mermaid:\n"
                             (str/join "\n" stray)) {}))))
    mermaid-str))

(defn legend-rows
  "Build the cardinality key from the tokens actually used, so the legend can
   never drift from the diagram."
  [{:keys [relationships]}]
  (let [used (->> relationships (map :cardinality) distinct sort)]
    (for [c used]
      (format "| `%s` | %s |" (cardinality->token c) (name c)))))

(defn frontmatter [{:keys [generated issue]}]
  (str/join "\n"
            ["type: Diagram"
             "title: \"ClojureDocs Entity-Attribute ER Diagram\""
             "description: \"Crow's-foot ER diagram of the ClojureDocs entity-attribute model, generated from the EDN source of truth.\""
             "tags: [entity-model, er-diagram, mermaid, issue-43]"
             (str "created: " generated)
             (str "modified: " generated)
             (str "source: " issue)
             "ai_assisted: \"Claude Opus 4.8 via Claude Code\""
             "tools: [babashka, EDN source of truth, tools/edn_to_mermaid.clj]"
             "agents_skills: []"
             "review_maturity: L1"
             "review_note: \"generated from the L3-verified EDN; diagram structure not separately reviewed\""]))

(defn document [model]
  (let [mm (lint! (mermaid model))]
    (str "---\n" (frontmatter model) "\n---\n\n"
         "# ClojureDocs Entity-Attribute ER Diagram\n\n"
         "*Auto-generated from [`docs/entity-attribute-model.edn`](../entity-attribute-model.edn) by "
         "[`tools/edn_to_mermaid.clj`](../../tools/edn_to_mermaid.clj). Do not edit by hand — "
         "regenerate with `bb tools/edn_to_mermaid.clj`.*\n\n"
         "## Sources\n\n"
         "- **Source of truth:** [`docs/entity-attribute-model.edn`](../entity-attribute-model.edn) — the data model (verification scope noted in its header).\n"
         "- **Narrative model:** [`docs/entity-attribute-model.md`](../entity-attribute-model.md) — entity descriptions and verification.\n"
         "- **Design rationale:** [`docs/rfcs/entity-model-rfc.md`](../rfcs/entity-model-rfc.md).\n"
         "- **Issue:** [nubank/clojuredocs#43](" (:issue model) ").\n"
         "- **Notation:** [Mermaid entity-relationship diagrams](https://mermaid.js.org/syntax/entityRelationshipDiagram.html).\n\n"
         "## Diagram\n\n"
         "```mermaid\n" mm "\n```\n\n"
         "## Key / Legend\n\n"
         "Relationships use Mermaid crow's-foot notation, read left-to-right as `FROM token TO`. "
         "The cardinality sits on the side it describes: `}o` = zero-or-more, `||` = exactly-one, "
         "`o{` = zero-or-more on the right.\n\n"
         "| Cardinality token | Meaning (`from`–`to`) |\n"
         "|---|---|\n"
         (str/join "\n" (legend-rows model)) "\n\n"
         "| Attribute marker | Meaning |\n"
         "|---|---|\n"
         "| `PK` | Primary key (`:_id` — MongoDB ObjectId). |\n"
         "| `FK` | Reference to another entity. Some are stored as embedded sub-documents that inline fields from the target (`:embedded-var`, `:embedded-user`); others as ObjectId join keys (e.g. `EXAMPLE_HISTORY.example_id`). |\n"
         "| `[gap]` prefix | Attribute described by the [2026 vision](../2026vison.md) but not yet present in code/data (`:status :gap`). |\n\n"
         "Each box lists the entity's present-state attributes (`:status :exists`); "
         "the `:_id` row is shown first, the rest alphabetically. JVM-heap and EDN-file "
         "entities are test-guarded; MongoDB entities are snapshot-derived and not yet "
         "test-guarded ([#66](https://github.com/nubank/clojuredocs/issues/66)). Embedded "
         "sub-schemas (`:embedded-var`, `:embedded-user`) are stored inline on the parent "
         "document and are not drawn as separate boxes — the `FK` edges point at the canonical "
         "[`VAR`](../glossary.md#v)/[`USER`](../entity-attribute-model.md) entities whose fields they embed.\n\n"
         "## Provenance & Review\n\n"
         "This file is generated; its version is the EDN's `:generated` date (" (:generated model)
         "), carried into the frontmatter. Errata are tracked centrally in "
         "[`errata.md`](../errata.md). Review notes: "
         "[`entity-attribute-er_research-review_run_1.md`](entity-attribute-er_research-review_run_1.md).\n")))

(defn -main [& args]
  (let [args (set args)
        model (edn/read-string (slurp (io/file repo-root edn-path)))
        doc (document model)]
    (when (args "--check")
      (require '[babashka.http-client :as http])
      (let [resp ((resolve 'babashka.http-client/post)
                  "https://kroki.io/mermaid/svg"
                  {:headers {"Content-Type" "text/plain"}
                   :body (lint! (mermaid model))
                   :throw false})]
        (if (= 200 (:status resp))
          (println "Kroki render check: OK (diagram parses)")
          (binding [*out* *err*]
            (println "Kroki render check FAILED:" (:status resp) (:body resp))
            (System/exit 1)))))
    (if (args "--stdout")
      (print doc)
      (let [f (io/file repo-root out-path)]
        (io/make-parents f)
        (spit f doc)
        (println "wrote" out-path
                 (str "(" (count (:entities model)) " entities, "
                      (count (:relationships model)) " edges)"))))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
