(ns clojuredocs.pages.common-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojuredocs.pages.common :as common])
  (:import [java.time LocalDate]))

(deftest show-survey-banner?-test
  (testing "shown while the survey is open, through the last day"
    (is (common/show-survey-banner? (LocalDate/of 2026 11 16)))
    (is (common/show-survey-banner? (LocalDate/of 2026 11 30))))
  (testing "hidden once the survey has closed"
    (is (not (common/show-survey-banner? (LocalDate/of 2026 12 1))))
    (is (not (common/show-survey-banner? (LocalDate/of 2027 1 1))))))
