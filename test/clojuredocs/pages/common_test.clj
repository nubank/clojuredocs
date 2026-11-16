(ns clojuredocs.pages.common-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojuredocs.pages.common :as common])
  (:import [java.time LocalDate]))

(deftest show-survey-banner?-test
  (testing "shown while the survey is open"
    (is (common/show-survey-banner? (LocalDate/of 2026 11 16)))
    (is (common/show-survey-banner? (LocalDate/of 2026 11 30))))
  (testing "still shown past the advertised close, through Dec 5"
    (is (common/show-survey-banner? (LocalDate/of 2026 12 1)))
    (is (common/show-survey-banner? (LocalDate/of 2026 12 5))))
  (testing "hidden from Dec 6 onward"
    (is (not (common/show-survey-banner? (LocalDate/of 2026 12 6))))
    (is (not (common/show-survey-banner? (LocalDate/of 2027 1 1))))))
