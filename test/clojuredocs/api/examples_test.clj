(ns clojuredocs.api.examples-test
  "Regression tests for example editor dedup (issue #65).

  `add-editor` must treat [:login :account-source] as identity and refresh
  :avatar-url in place instead of appending a near-duplicate when the avatar
  changes between edits."
  (:require [clojure.test :refer [deftest is testing]]
            [clojuredocs.api.examples :as examples]))

(def ^:private alice-v1
  {:login "alice" :account-source "github"
   :avatar-url "https://example.com/alice-v1.png"})

(def ^:private alice-v2
  {:login "alice" :account-source "github"
   :avatar-url "https://example.com/alice-v2.png"})

(def ^:private bob
  {:login "bob" :account-source "github"
   :avatar-url "https://example.com/bob.png"})

(deftest add-editor-appends-new-user
  (testing "a first-time editor is appended"
    (is (= [alice-v1]
           ((examples/add-editor alice-v1) [])))
    (is (= [alice-v1 bob]
           ((examples/add-editor bob) [alice-v1])))))

(deftest add-editor-refreshes-avatar-without-duplicate
  (testing "same login+account-source updates avatar instead of duplicating"
    (is (= [alice-v2]
           ((examples/add-editor alice-v2) [alice-v1])))
    (is (= [bob alice-v2]
           ((examples/add-editor alice-v2) [bob alice-v1])))))

(deftest add-editor-ignores-true-duplicates
  (testing "identical user maps stay a single entry"
    (is (= [alice-v1]
           ((examples/add-editor alice-v1) [alice-v1])))))