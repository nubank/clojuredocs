(ns clojuredocs.search-test
  "Regression tests for search formatting / exact-name hits (issue #87)."
  (:require [clojure.test :refer [deftest is testing]]
            [clojuredocs.search :as search]))

(deftest exact-name-matches-star-quote
  (testing "clojure.core/*' is findable by its literal name"
    (let [hits (search/exact-name-matches "*'")]
      (is (seq hits) "expected at least one exact hit for *'")
      (is (some #(= "*'" (:name %)) hits)))))

(deftest query-finds-star-quote
  (testing "searching *' returns clojure.core/*' (does not collapse to -')"
    (let [hits (vec (search/query "*'"))
          names (map :name hits)]
      (is (some #(= "*'" %) names)
          (str "expected *' in results, got: " (pr-str names)))
      (is (not-any? #(= "-'" %) names)
          "must not exclusively/incorrectly surface -' for a *' query"))))

(deftest query-bare-star-still-resolves-core-star
  (testing "searching bare * still resolves clojure.core/*"
    (let [hits (vec (search/query "*"))]
      (is (= 1 (count hits)))
      (is (= "*" (:name (first hits))))
      (is (= "clojure.core" (:ns (first hits)))))))

(deftest drop-leading-stars-only-strips-prefix
  (testing "only leading asterisks are stripped (not every * in the string)"
    (is (= "agent*" (search/drop-leading-stars "*agent*")))
    (is (= "'" (search/drop-leading-stars "*'")))
    (is (nil? (search/drop-leading-stars "*")))
    (is (= "map" (search/drop-leading-stars "map")))))
