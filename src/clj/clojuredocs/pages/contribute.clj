(ns clojuredocs.pages.contribute
  (:require [clojuredocs.util :as util]
            [clojuredocs.data :as data]
            [clojuredocs.search :as search]
            [clojuredocs.pages.common :as common]
            [clojure.core.memoize :as memo]))

(def ^:private core-lib-url (:library-url search/clojure-lib))

;; --- Vars still needing an example ---

(defn- compute-needs-examples []
  (let [have-examples (data/vars-with-examples-for-library core-lib-url)]
    (->> (:vars search/clojure-lib)
         (remove search/impl-var?)
         (remove #(contains? have-examples (select-keys % [:ns :name])))
         (map #(select-keys % [:ns :name]))
         (sort-by (juxt :ns :name)))))

(def needs-examples
  (memo/ttl compute-needs-examples :ttl/threshold (* 1000 3600)))

(defn $needs-examples [vs]
  (when (seq vs)
    [:div.wanted
     [:span.wanted-label "Still need an example: "]
     (for [{:keys [ns name]} vs]
       [:li (util/$var-link ns name ns "/" name)])]))

(defn $content [needs-examples]
  [:div.row
   [:div.col-md-10.col-md-offset-1
    [:section
     [:h1 "Contribute to ClojureDocs"]
     [:p "We need your help to make ClojureDocs a great community resource. Here are a couple of ways you can contribute."]
     [:ul
      [:li
       [:h4 [:i.fa.fa-comment-o] "Give Feedback"]
       [:p "Please " [:a {:href "https://github.com/nubank/clojuredocs/issues"} "open a ticket"] " if you have an idea of how we can improve ClojureDocs."]]
      [:li
       [:h4 [:i.fa.fa-indent] "Add an Example"]
       [:p "Sharing your knowledge with fellow Clojurists is easy:"]
       [:p
        "First, take a look at the "
        [:a {:href "/examples-styleguide"} "examples style guide"]
        ", and then add an example for your favorite var (or pick one from the list)."]
       [:p "In addition to examples, you also have the ability to add 'see also' references between vars."]
       ($needs-examples needs-examples)]]]]])

(defn page-handler [{:keys [uri user]}]
  (common/$main
    {:body-class "contribute-page"
     :title "Contribute | ClojureDocs - Community-Powered Clojure Documentation and Examples"
     :user user
     :page-uri uri
     :content ($content (needs-examples))}))
