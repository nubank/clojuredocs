(ns clojuredocs.entity-model-test
  "Tests that verify the entity-attribute-model.edn schema against the
   running system. These tests enforce the reliability ratchet:
   findings that were REPL-verified are now permanently guarded."
  (:require [clojure.test :refer [deftest testing is are]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojuredocs.search :as search]
            [clojuredocs.search.static :as static]
            [clojuredocs.search.compat :as compat]
            [clojuredocs.api.common :as common]))

;; --- Schema loading ---

(def schema
  (-> (io/file "docs/entity-attribute-model.edn")
      slurp
      edn/read-string))

;; --- Schema self-consistency ---

(deftest schema-parses-and-has-required-keys
  (is (map? schema))
  (is (= "1.1.0" (:schema-version schema)))
  (is (map? (:sub-schemas schema)))
  (is (map? (:entities schema))))

(deftest schema-entities-have-attrs
  (testing "every entity has an :attrs map"
    (doseq [[entity-key entity] (:entities schema)]
      (is (map? (:attrs entity))
          (str "entity " entity-key " missing :attrs")))))

(deftest schema-attrs-have-required-fields
  (testing "every attribute has :type, :required?, :description, and :status"
    (doseq [[entity-key entity] (:entities schema)
            [attr-key attr] (:attrs entity)]
      (is (contains? attr :type)
          (str entity-key "/" attr-key " missing :type"))
      (is (contains? attr :required?)
          (str entity-key "/" attr-key " missing :required?"))
      (is (string? (:description attr))
          (str entity-key "/" attr-key " missing :description"))
      (is (map? (:status attr))
          (str entity-key "/" attr-key " missing :status")))))

(deftest schema-status-states-are-valid
  (testing "every :status :state is one of the defined enum values"
    (let [valid-states #{:exists :nil :vestigial :absent :planned :gap}]
      (doseq [[entity-key entity] (:entities schema)
              [attr-key attr] (:attrs entity)]
        (is (contains? valid-states (get-in attr [:status :state]))
            (str entity-key "/" attr-key " has invalid status state: "
                 (get-in attr [:status :state])))))))

(deftest schema-sub-schema-refs-are-valid
  (testing "every :schema reference in attrs points to a defined sub-schema"
    (let [sub-schema-keys (set (keys (:sub-schemas schema)))]
      (doseq [[entity-key entity] (:entities schema)
              [attr-key attr] (:attrs entity)
              :when (:schema attr)]
        (is (contains? sub-schema-keys (:schema attr))
            (str entity-key "/" attr-key " references undefined sub-schema "
                 (:schema attr)))))))

;; --- Library entity ---

(deftest library-is-singleton
  (is (= 1 (get-in schema [:entities :library :cardinality])))
  (is (map? search/clojure-lib) "clojure-lib should be a map"))

(deftest library-has-all-schema-attrs
  (testing "every :exists attr in schema is present on clojure-lib"
    (let [lib-attrs (get-in schema [:entities :library :attrs])
          lib-keys  (set (keys search/clojure-lib))]
      (doseq [[attr-key attr] lib-attrs
              :when (= :exists (get-in attr [:status :state]))]
        (is (contains? lib-keys attr-key)
            (str "library missing key " attr-key))))))

(deftest library-scalar-values
  (testing "library has expected scalar types"
    (is (string? (:library-url search/clojure-lib)))
    (is (string? (:version search/clojure-lib)))
    (is (string? (:source-base-url search/clojure-lib)))
    (is (string? (:gh-tag-url search/clojure-lib)))
    (is (sequential? (:namespaces search/clojure-lib)))
    (is (sequential? (:vars search/clojure-lib)))))

;; --- Namespace entity ---

(deftest namespace-count-matches-schema
  (let [expected (get-in schema [:entities :namespace :cardinality])
        actual   (count (:namespaces search/clojure-lib))]
    (is (= expected actual)
        (str "expected " expected " namespaces, got " actual))))

(deftest namespace-universal-keys
  (testing "every namespace has :name"
    (doseq [ns-map (:namespaces search/clojure-lib)]
      (is (string? (:name ns-map))
          (str "namespace missing :name — " ns-map)))))

(deftest namespace-doc-coverage
  (testing ":doc is present on most namespaces but not all"
    (let [with-doc (->> (:namespaces search/clojure-lib)
                        (filter #(string? (:doc %)))
                        count)
          without-doc (->> (:namespaces search/clojure-lib)
                           (remove #(string? (:doc %)))
                           (map :name)
                           set)]
      (is (= 32 with-doc))
      (is (= #{"clojure.core.logic" "clojure.core.logic.fd"
               "clojure.core.logic.pldb" "clojure.core.protocols"
               "clojure.instant" "clojure.tools.build.api"}
             without-doc)))))

(deftest namespace-sparse-added-key
  (testing ":added is present on exactly 2 namespaces"
    (let [with-added (->> (:namespaces search/clojure-lib)
                          (filter #(contains? % :added)))]
      (is (= 2 (count with-added)))
      (is (= #{"clojure.pprint" "clojure.reflect"}
             (set (map :name with-added)))))))

;; --- Var entity ---

(deftest var-count-matches-schema
  (let [expected (get-in schema [:entities :var :cardinality])
        actual   (count (:vars search/clojure-lib))]
    (is (= expected actual)
        (str "expected " expected " vars, got " actual))))

(deftest var-universal-keys
  (testing "every var has the 6 required keys"
    (let [required-keys #{:ns :name :type :arglists :library-url :href}]
      (doseq [v (:vars search/clojure-lib)]
        (doseq [k required-keys]
          (is (contains? v k)
              (str "var " (:ns v) "/" (:name v) " missing " k)))))))

(deftest var-key-universe-matches-schema
  (testing "the set of keys across all vars matches the schema"
    (let [actual-keys   (->> (:vars search/clojure-lib)
                             (mapcat keys)
                             set)
          schema-keys   (set (keys (get-in schema [:entities :var :attrs])))]
      ;; Schema should cover every key that appears on vars
      (is (empty? (set/difference actual-keys schema-keys))
          (str "keys on vars not in schema: "
               (set/difference actual-keys schema-keys)))
      ;; Every :exists schema key should appear on at least one var
      (let [exists-keys (->> (get-in schema [:entities :var :attrs])
                             (filter (fn [[_ v]] (= :exists (get-in v [:status :state]))))
                             (map first)
                             set)]
        (is (empty? (set/difference exists-keys actual-keys))
            (str "schema :exists keys never seen on vars: "
                 (set/difference exists-keys actual-keys)))))))

(deftest var-sparse-key-coverage
  (testing "sparse key frequencies match schema coverage claims"
    (let [freqs (->> (:vars search/clojure-lib)
                     (mapcat keys)
                     frequencies)
          total (count (:vars search/clojure-lib))
          schema-attrs (get-in schema [:entities :var :attrs])]
      ;; Check a few key coverage values
      (are [attr-key expected-count]
          (= expected-count (get freqs attr-key 0))
        :file     1482
        :doc      1293
        :added    907
        :static   319
        :macro    190
        :tag      122
        :dynamic  56
        :deprecated 18
        :special-form 4
        :forms    4
        :url      1))))

(deftest var-letfn-url-is-vestigial
  (testing ":url exists only on letfn and is nil"
    (let [with-url (->> (:vars search/clojure-lib)
                        (filter #(contains? % :url)))]
      (is (= 1 (count with-url)))
      (is (= "letfn" (:name (first with-url))))
      (is (nil? (:url (first with-url)))))))

(deftest var-type-values
  (testing "var :type set is exactly the observed values — no phantom types"
    ;; Strict equality (not subset?) so the schema cannot claim a type that never
    ;; occurs. "special-form" is intentionally absent: the 15 static special forms
    ;; lose their :type in transform-var-meta and surface as "var" (issue #67).
    ;; See the :var/:type entry in entity-attribute-model.edn.
    (let [actual-types (->> (:vars search/clojure-lib)
                            (map :type)
                            set)]
      (is (= #{"function" "macro" "var"} actual-types)
          (str "var :type set drifted from documented values: " actual-types)))))

;; --- Embedded sub-schema consistency ---

(deftest embedded-var-shape-matches-common
  (testing "EmbeddedVar sub-schema keys match the live api/common.clj Var schema"
    ;; Compare against the actual schema in clojuredocs.api.common, not a hardcoded
    ;; literal, so a change to Var there forces an update here (reliability ratchet).
    (is (= (set (keys common/Var))
           (set (keys (get-in schema [:sub-schemas :embedded-var]))))
        (str "embedded-var drifted from api/common.clj Var: "
             (set (keys common/Var))))))

(deftest embedded-user-shape-matches-common
  (testing "EmbeddedUser sub-schema keys match the live api/common.clj User schema"
    (is (= (set (keys common/User))
           (set (keys (get-in schema [:sub-schemas :embedded-user]))))
        (str "embedded-user drifted from api/common.clj User: "
             (set (keys common/User))))))

;; --- Dialect compat entity ---

(deftest dialect-compat-loads
  (testing "dialect-compat.edn loads and has expected structure"
    (is (map? compat/dialect-compat))
    (is (map? (:vars compat/dialect-compat)))
    (is (map? (:versions compat/dialect-compat)))))

(deftest dialect-compat-lookup-works
  (testing "known vars return expected dialect sets"
    (is (= #{:bb :clj :cljs} (compat/dialects-for "clojure.core" "map")))
    (is (= #{:clj} (compat/dialects-for "clojure.core" "gen-class")))))

(deftest dialect-compat-values-are-sets
  (testing "every dialect value is a set of keywords"
    (doseq [[var-key dialects] (:vars compat/dialect-compat)]
      (is (set? dialects)
          (str var-key " dialects is not a set: " (type dialects)))
      (doseq [d dialects]
        (is (keyword? d)
            (str var-key " contains non-keyword dialect: " d))))))

;; --- Cross-entity consistency ---

(deftest static-namespaces-match-gathered
  (testing "static/clojure-namespaces list matches gathered namespace count"
    (is (= (count static/clojure-namespaces)
           (count (:namespaces search/clojure-lib))))))

(deftest search-var-keys-covers-schema-attrs
  (testing "search/var-keys includes all keys that appear on vars"
    ;; var-keys is the select-keys filter — it determines what keys vars can have.
    ;; The 6 keys added by gather-vars (:library-url :type :href :ns :name :arglists)
    ;; are either in var-keys or added post-transform.
    (let [var-keys-set  (set search/var-keys)
          ;; Keys added by gather-vars pipeline (not from var metadata)
          pipeline-keys #{:library-url :type :href}
          ;; Keys from var-keys that actually appear
          actual-keys   (->> (:vars search/clojure-lib)
                             (mapcat keys)
                             set)
          ;; Everything in actual should be either in var-keys or pipeline-added
          unexplained   (set/difference actual-keys
                                       (set/union var-keys-set pipeline-keys))]
      (is (empty? unexplained)
          (str "var keys not explained by var-keys or pipeline: " unexplained)))))
