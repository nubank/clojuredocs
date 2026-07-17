#!/usr/bin/env bb
;; Regenerate every derived view of docs/entity-attribute-model.edn in one step:
;;   - docs/diagrams/entity-attribute-er.md      (Mermaid ER diagram + legend)
;;   - docs/diagrams/entity-attributes.csv        (one row per entity/attribute)
;;   - docs/diagrams/entity-relationships.csv     (one row per relationship edge)
;;
;; Shells out to the individual generators so behavior is identical to running
;; them directly. Pure babashka — no JVM, no Leiningen/deps involvement.
;;
;; Usage:
;;   bb tools/gen_model_views.clj           # regenerate diagram + CSVs
;;   bb tools/gen_model_views.clj --check   # also round-trip the diagram through Kroki

(ns gen-model-views
  (:require [clojure.java.io :as io]
            [babashka.process :refer [shell]]))

(def tools-dir (-> *file* io/file .getCanonicalFile .getParentFile))

(defn script [name] (str (io/file tools-dir name)))

(defn -main [& args]
  (let [check? (boolean (some #{"--check"} args))]
    (println "Regenerating entity-model views from docs/entity-attribute-model.edn ...")
    (apply shell "bb" (script "edn_to_mermaid.clj") (when check? ["--check"]))
    (shell "bb" (script "edn_to_csv.clj"))
    (println "Done — diagram + CSVs regenerated.")))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
