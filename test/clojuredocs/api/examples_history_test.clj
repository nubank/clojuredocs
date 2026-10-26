(ns clojuredocs.api.examples-history-test
  "Regression tests for example history body capture (issue #55).

  When an example is edited, history must store the *previous* body
  (the content being replaced), not the incoming request body."
  (:require [clojure.test :refer [deftest is testing]]
            [clojuredocs.api.examples :as examples]
            [somnium.congomongo :as mon]))

(def ^:private example-id "507f1f77bcf86cd799439011")

(def ^:private stored-example
  {:_id (org.bson.types.ObjectId. example-id)
   :body "original content"
   :var {:ns "clojure.core" :name "map"
         :library-url "https://github.com/clojure/clojure"}
   :author {:login "alice" :account-source "github"
            :avatar-url "http://example.com/alice.png"}
   :editors []
   :created-at 1000
   :updated-at 1000})

(def ^:private editor
  {:login "bob" :account-source "github"
   :avatar-url "http://example.com/bob.png"})

(deftest patch-example-history-stores-previous-body
  (testing "history entry body is the pre-edit content, not the new payload"
    (let [history (atom nil)]
      (with-redefs [mon/fetch-one (fn [& _] stored-example)
                    mon/update! (fn [& _] nil)
                    mon/insert! (fn [coll doc]
                                  (when (= coll :example-histories)
                                    (reset! history doc)))]
        (let [resp ((examples/patch-example-handler example-id)
                    {:edn-body {:body "revised content"} :user editor})]
          (is (= 200 (:status resp)))
          (is (= "revised content" (:body (:body resp)))
              "current example should hold the new body")
          (is (= "original content" (:body @history))
              "history must preserve the body being replaced")
          (is (= (:_id stored-example) (:example-id @history))))))))
