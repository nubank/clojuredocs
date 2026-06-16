(ns clojuredocs.metadata-test
  "Enforces the OKF + RDF document-metadata convention under `lein test`.

  This is a thin wrapper: it shells out to the single source of truth,
  `tools/validate_metadata.clj` (run by babashka), and asserts a clean exit.
  Keeping one validator means the hook, CI, and this test enforce the same rules.
  Requires `bb` on PATH."
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.java.shell :as sh]))

(deftest docs-metadata-frontmatter
  (testing "every docs/**.md frontmatter conforms to docs/metadata-schema.edn"
    (let [result (try (sh/sh "bb" "tools/validate_metadata.clj")
                      (catch java.io.IOException _ ::no-bb))]
      (if (= result ::no-bb)
        (is false "babashka (bb) must be on PATH to run the doc-metadata validator")
        (let [{:keys [exit out err]} result]
          (when-not (zero? exit)
            (println out)
            (when (seq err) (println err)))
          (is (zero? exit)
              "doc-metadata validation failed — see tools/validate_metadata.clj output above"))))))
