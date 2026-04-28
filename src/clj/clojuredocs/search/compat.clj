(ns clojuredocs.search.compat
  "Loads dialect-compat.edn at startup and provides lookup."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def dialect-compat
  (-> (io/resource "dialect-compat.edn")
      slurp
      edn/read-string))

(defn dialects-for
  "Returns the set of dialect keywords for a qualified var name,
   e.g. #{:clj :cljs :bb}, or nil if not found."
  [ns-str name-str]
  (get-in dialect-compat [:vars (str ns-str "/" name-str)]))
