(ns clojuredocs.core-test
  (:require [clojure.test :refer :all]
            [clojuredocs.core :as core]))

(deftest core-namespace-loads
  (testing "core namespace loads successfully"
    (is (find-ns 'clojuredocs.core))))
