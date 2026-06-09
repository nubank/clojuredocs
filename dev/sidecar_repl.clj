;; sidecar_repl.clj — REPL scratchpad for investigation
;; Not part of the application. Eval forms here to explore the codebase.
;; See decisions.md entry: 2026-06-09 — Sidecar REPL for entity model verification

(ns dev.sidecar-repl
  (:require [clojuredocs.search :as search]
            [clojuredocs.search.static :as static]
            [clojuredocs.search.compat :as compat]))

(comment
  ;; === Startup Flow Investigation ===

  ;; Q: What keys does the Library entity actually have?
  (keys search/clojure-lib)
  ;;=> (:library-url :version :source-base-url :gh-tag-url :namespaces :vars)

  (dissoc search/clojure-lib :vars :namespaces) ;; just the scalar attrs
  ;;=> {:library-url "https://github.com/clojure/clojure",
  ;;    :version "1.12.4",
  ;;    :source-base-url "https://github.com/clojure/clojure/blob/clojure-1.12.4",
  ;;    :gh-tag-url "https://github.com/clojure/clojure/tree/clojure-1.12.4"}

  ;; Q: What keys does a Var have after the gather pipeline?
  (keys (first (:vars search/clojure-lib)))
  ;;=> (:ns :name :doc :arglists :library-url :type :href)
  (search/lookup "clojure.core/map")
  ;;=> {:added "1.0",
  ;;    :ns "clojure.core",
  ;;    :name "map",
  ;;    :file "clojure/core.clj",
  ;;    :static true,
  ;;    :type "function",
  ;;    :column 1,
  ;;    :line 2744,
  ;;    :arglists ("f" "f coll" "f c1 c2" "f c1 c2 c3" "f c1 c2 c3 & colls"),
  ;;    :doc
  ;;    "Returns a lazy sequence consisting of the result of applying f to\n  the set of first items of each coll, followed by applying f to the\n  set of second items in each coll, until any one of the colls is\n  exhausted.  Any remaining items in other colls are ignored. Function\n  f should accept number-of-colls arguments. Returns a transducer when\n  no collection is provided.",
  ;;    :library-url "https://github.com/clojure/clojure",
  ;;    :href "/clojure.core/map"}

  ;; Q: Does :url actually appear on vars? :source-url? :usage-urls?
  (:url (search/lookup "clojure.core/map"))
  ;;=> nil
  (:source-url (search/lookup "clojure.core/map"))
  ;;=> nil
  (:usage-urls (search/lookup "clojure.core/map"))
  ;;=> nil

  ;; Q: What does a Namespace look like after gather?
  (first (:namespaces search/clojure-lib))
  ;;=> {:doc "Fundamental library of the Clojure language", :name "clojure.core"}
  (keys (first (:namespaces search/clojure-lib)))
  ;;=> (:doc :name)

  ;; Q: How many of each?
  (count (:vars search/clojure-lib))
  ;;=> 1572
  (count (:namespaces search/clojure-lib))
  ;;=> 38

  ;; Q: What does dialect compat data look like for a var?
  (compat/dialects-for "clojure.core" "map")
  ;;=> #{:bb :clj :cljs}
  (compat/dialects-for "clojure.core" "gen-class")
  ;;=> #{:clj}
  )
