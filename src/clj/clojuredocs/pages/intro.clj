(ns clojuredocs.pages.intro
  (:require [clojure.string :as str]
            [clojuredocs.util :as util]
            [clojuredocs.data :as data]
            [clojuredocs.search :as search]
            [clojuredocs.pages.common :as common]
            [clojuredocs.pages.vars :as vars]
            [clojuredocs.pages.quickref :as quickref]
            [clojuredocs.pages.quickref.static :as quickref-static]
            [clojuredocs.syntax :as syntax]))

(def ^:private core-lib-url (:library-url search/clojure-lib))

(defmulti $render-recently-updated :type)

(defmethod $render-recently-updated :default [_] nil)

(defmethod $render-recently-updated :example
  [{:keys [var author created-at]}]
  [:div.recently-updated
   (util/$avatar author {:size 48})
   [:span.content
    (:login author)
    " authored an example for "
    (util/$var-link (:ns var) (:name var)
      (-> var :ns util/html-encode)
      "/"
      (-> var :name util/html-encode))
    " "
    (util/timeago created-at)
    " ago."
    [:div.clear]]])

(defmethod $render-recently-updated :see-also
  [{:keys [from-var to-var author created-at]}]
  [:div.recently-updated
   (util/$avatar author {:size 48})
   [:span.content
    (:login author)
    " added a see-also from "
    (util/$var-link (:ns from-var) (:name from-var)
      (-> from-var :ns util/html-encode)
      "/"
      (-> from-var :name util/html-encode))
    " to "
    (util/$var-link (:ns to-var) (:name to-var)
      (-> to-var :ns util/html-encode)
      "/"
      (-> to-var :name util/html-encode))
    " "
    (util/timeago created-at)
    " ago."
    [:div.clear]]])

(defmethod $render-recently-updated :note
  [{:keys [var author created-at]}]
  [:div.recently-updated
   (util/$avatar author {:size 48})
   [:span.content
    (:login author)
    " authored a note for "
    (util/$var-link (:ns var) (:name var)
      (-> var :ns util/html-encode)
      "/"
      (-> var :name util/html-encode))
    " "
    (util/timeago created-at)
    " ago."]
   [:div.clear]])

;; --- Example of the day ---
;; Rotates daily through a small pool of illustrative clojure.core vars,
;; featuring whichever already has a real submitted example.

(def ^:private eotd-candidates
  ["frequencies" "group-by" "map" "reduce" "filter" "partition-by" "juxt" "comp"])

(defn example-of-the-day []
  (let [n (count eotd-candidates)
        day (quot (System/currentTimeMillis) 86400000)
        ordered (map #(nth eotd-candidates (mod (+ day %) n)) (range n))]
    (some (fn [nm]
            (last (data/find-examples-for
                    {:ns "clojure.core" :name nm :library-url core-lib-url})))
      ordered)))

(defn $example-of-the-day [example]
  (when example
    (let [{:keys [body var]} example
          {:keys [ns name]} var]
      [:div.example-of-the-day.var-example
       [:div.eotd-head
        [:span "Example of the day"]
        (util/$var-link ns name (str ns "/" name))]
       [:div.example-body (syntax/syntaxify body)]
       (vars/$dialect-badges ns name)])))

;; --- clojure.core, by purpose ---
;; A curated preview built from the same category data that backs
;; /quickref, capped per-category with a link out to the rest.

(def ^:private core-index-picks
  [["Simple Values" "Numbers" nil]
   ["Simple Values" "Strings / Characters" nil]
   ["Simple Values" "Regular Expressions" nil]
   ["Operations" "Flow Control" nil]
   ["Operations" "Concurrency" nil]
   ["Functions" "General" "Functions"]
   ["Functions" "Macros" nil]
   ["Collections" "Sequences" nil]
   ["Collections" "Maps" nil]
   ["Collections" "Vectors" nil]
   ["Collections" "Sets" nil]
   ["Code Structure" "Variables" nil]
   ["Code Structure" "Namespaces" nil]
   ["Environment" "IO" nil]])

(def ^:private core-index-cap 10)

(defn- category-syms [sphere-title category-title]
  (->> quickref-static/quickref-data
       (filter #(= sphere-title (:title %)))
       first
       :categories
       (filter #(= category-title (:title %)))
       first
       :groups
       (mapcat :syms)
       distinct))

(def core-index
  (vec
    (for [[sphere-title category-title display-title] core-index-picks
          :let [syms (category-syms sphere-title category-title)]]
      {:title (or display-title category-title)
       :anchor (quickref/title->id category-title)
       :total (count syms)
       :syms (take core-index-cap syms)})))

(defn $core-index-group [{:keys [title anchor total syms]}]
  [:div.category
   [:div.category-header.clearfix
    [:h3 title [:span.n total]]]
   [:dl.dl-horizontal
    (mapcat
      (fn [s]
        [[:div.dl-row
          [:dt (util/$var-link "clojure.core" (str s) (str s))]
          [:dd (->> (str "clojure.core/" s)
                    search/lookup-vars
                    :doc
                    (take 70)
                    (apply str))]]])
      syms)
    (when (> total (count syms))
      [:div.dl-row
       [:dt]
       [:dd [:a {:href (str "/quickref#" anchor)}
             (str "+" (- total (count syms)) " more")]]])]])

;; --- More namespaces ---
;; A small, hand-picked set of commonly used namespaces beyond clojure.core.

(def ^:private featured-namespaces
  [["clojure.string" ["split" "join" "replace" "trim" "blank?"]]
   ["clojure.set" ["union" "difference" "index" "rename-keys"]]
   ["clojure.walk" ["postwalk" "prewalk" "keywordize-keys"]]
   ["clojure.edn" ["read-string" "read"]]
   ["clojure.java.io" ["reader" "writer" "file" "resource"]]
   ["clojure.test" ["deftest" "is" "testing" "are"]]])

(defn- first-sentence [doc]
  (when doc
    (-> doc
        (str/replace #"\s+" " ")
        str/trim
        (str/split #"(?<=\.) ")
        first
        (vars/ellipsis 90))))

(defn- ns-doc [ns-name]
  (->> search/clojure-lib
       :namespaces
       (filter #(= ns-name (:name %)))
       first
       :doc
       first-sentence))

(defn $namespace-card [[ns-name var-names]]
  [:div.namespace-card
   [:h4 [:a {:href (str "/" ns-name)} ns-name]]
   (when-let [doc (ns-doc ns-name)]
     [:p.doc doc])
   [:p.vars
    (for [v var-names]
      (util/$var-link ns-name v v))]])

;; --- Contribute: vars still needing an example ---

(def ^:private needs-examples-candidates
  ["halt-when" "iteration" "partitionv" "partitionv-all" "splitv-at"
   "update-keys" "parse-uuid"])

(defn needs-examples []
  (->> needs-examples-candidates
       (filter #(zero? (data/count-examples-for
                          {:ns "clojure.core" :name % :library-url core-lib-url})))
       (take 4)))

(defn $needs-examples [vs]
  (when (seq vs)
    [:div.wanted
     [:span.wanted-label "Still need an example: "]
     (for [v vs]
       (util/$var-link "clojure.core" v v))]))

(defn $index [{:keys [recently-updateds example-of-the-day needs-examples]}]
  [:div
   [:div.row
    [:div.col-md-5
     [:section
      [:h1 "Community-powered documentation and examples for " [:a {:href "http://clojure.org"} "Clojure"]]]
     [:section
      [:div.search-widget
       [:form.search {:method :get :action "/search" :autocomplete "off"}
        [:input.form-control.placeholder
         {:type "text"
          :name "q"
          :placeholder "Looking for?"
          :autoFocus "autofocus"
          :autoComplete "off"}]
        [:ul.ac-results]]]]]
    [:div.col-md-7
     ($example-of-the-day example-of-the-day)]]
   [:div.row
    [:div.col-md-12
     [:section
      [:h5 "Recently Updated"]
      [:div.row
       (->> recently-updateds
            (map $render-recently-updated)
            (partition-all 3)
            (map (fn [rs]
                   [:div.col-sm-6 rs])))]]]]
   [:div.row
    [:div.col-md-12
     [:section.core-index-section
      [:div.sec-head.clearfix
       [:h5 [:code "clojure.core"] ", by purpose"]
       [:span.sec-head-link [:a {:href "/quickref"} "Full quick reference →"]]]
      [:div.sphere
       (map $core-index-group core-index)]]]]
   [:div.row
    [:div.col-md-12
     [:section.namespaces-section
      [:div.sec-head.clearfix
       [:h5 "More namespaces"]
       [:span.sec-head-link [:a {:href "/clojure.core"} "Browse all namespaces →"]]]
      [:div.namespace-cards
       (map $namespace-card featured-namespaces)]]]]
   [:div.row
    [:div.col-md-12
     [:section
      [:h5 "Contribute to ClojureDocs"]
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
        ($needs-examples needs-examples)]]]]]])


(defn recently-updated []
  (let [limit 6
        examples (->> (data/find-recent-examples limit)
                      (map #(assoc % :type :example)))
        see-alsos (->> (data/find-recent-see-alsos limit)
                       (map #(assoc % :type :see-also)))
        notes (->> (data/find-recent-notes limit)
                   (map #(assoc % :type :note)))]
    (->> (concat
           examples
           see-alsos
           notes)
         (sort-by :created-at)
         reverse
         (take limit))))

(defn page-handler [{:keys [user]}]
    (-> {:content ($index {:recently-updateds (recently-updated)
                           :example-of-the-day (example-of-the-day)
                           :needs-examples (needs-examples)})
      :body-class "intro-page"
      :user user
      :hide-search true
      :show-stars? true}
     common/$main))
