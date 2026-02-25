(ns clojuredocs.search-test
  (:require [clojure.test :refer :all]
            [clojuredocs.search :as search]))

(deftest library-for-test
  (testing "library-for selects the correct library metadata"
    (is (= search/tools-build-lib
           (search/library-for "clojure.tools.build.api")))
    (is (= search/tools-build-lib
           (search/library-for {:ns "clojure.tools.build.api"})))
    (is (= search/clojure-lib
           (search/library-for "clojure.core")))))
