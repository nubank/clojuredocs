(ns clojuredocs.search-type-test
  "Regression for issue #67 — static special forms must retain :type."
  (:require [clojure.test :refer [deftest testing is]]
            [clojuredocs.search :as search]
            [clojuredocs.search.static :as static]))

(deftest type-of-prefers-explicit-type
  (testing "explicit :type wins over macro/arglists/special-form keys"
    (is (= "special-form"
           (search/type-of {:type "special-form" :macro true :arglists '([])})))
    (is (= "macro" (search/type-of {:macro true})))
    (is (= "function" (search/type-of {:arglists '([x])})))
    (is (= "special-form" (search/type-of {:special-form true})))
    (is (= "var" (search/type-of {})))))

(deftest transform-var-meta-keeps-type
  (testing ":type survives select-keys in transform-var-meta"
    (is (= "special-form"
           (:type (search/transform-var-meta
                   {:ns 'clojure.core :name 'if :type "special-form"}))))))

(deftest static-special-forms-carry-type
  (testing "every static special form declares :type special-form"
    (is (seq static/special-forms))
    (doseq [sf static/special-forms]
      (is (= "special-form" (:type sf))
          (str "missing type on " (:name sf))))))
