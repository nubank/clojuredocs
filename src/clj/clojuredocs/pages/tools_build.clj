 (ns clojuredocs.pages.tools-build
  (:require [clojuredocs.pages.common :as common]
            [clojuredocs.search :as search]
            [clojure.string :as str]
            [clojuredocs.util :as util]
            [clojuredocs.pages.nss :as nss]))

(defn tools-build-library-handler [{:keys [user uri]}]
  (common/$main
    {:body-class "tools-build-library-page"
     :title "tools.build Library | ClojureDocs - Community-Powered Clojure Documentation and Examples"
     :user user
     :page-uri uri
     :mobile-nav [{:title "tools.build"
                   :links [[:a {:href "/tools-build"} "Overview"]
                           [:a {:href "/tools-build/vars"} "All The Vars"]]}
                  {:title "Namespaces"
                   :links (->> search/tools-build-lib
                               :namespaces
                               (map (fn [{:keys [name]}]
                                      [:a {:href (str "/" name)} name])))}]
     :content
     (let [sections (into [:div.col-sm-10]
                          (concat
                            [[:section.markdown (common/memo-markdown-file "src/md/tools-build-library.md")]]
                            (for [{:keys [name]} search/searchable-nss
                                  :when (.startsWith name "clojure.tools.build")
                                  :let [content (common/memo-markdown-file (str "src/md/namespaces/" name ".md"))]
                                  :when content]
                              [:section.markdown
                               [:h2 [:a {:href (str "/" name)} name]]
                               content])))]
       [:div.row
        [:div.col-sm-2
         [:div.sidenav {:data-sticky-offset "20"}
          [:h5 "tools.build"]
          [:ul
           [:li [:a {:href "/tools-build"} "Overview"]]
           [:li [:a {:href "/tools-build/vars"} "All Vars"]]]
          (common/$library-nav search/tools-build-lib)]]
        sections])}))

(defn tools-build-library-vars-handler [{:keys [user uri]}]
  (common/$main
    {:body-class "tools-build-library-page"
     :title "All Vars in tools.build | ClojureDocs - Community-Powered Clojure Documentation and Examples"
     :page-uri uri
     :user user
     :mobile-nav [{:title "tools.build"
                   :links [[:a {:href "/tools-build"} "Overview"]
                           [:a {:href "/tools-build/vars"} "All The Vars"]]}
                  {:title "Namespaces"
                   :links (->> search/tools-build-lib
                               :namespaces
                               (map (fn [{:keys [name]}]
                                      [:a {:href (str "/" name)} name])))}]
     :content
     (let [groups (->> search/tools-build-lib :vars (group-by :ns) (sort-by first))]
       [:div.row
        [:div.col-sm-2
         [:div.sidenav {:data-sticky-offset "20"}
          [:h5 "tools.build"]
          [:ul
           [:li [:a {:href "/tools-build"} "Overview"]]
           [:li [:a {:href "/tools-build/vars"} "All Vars"]]]
          (common/$library-nav search/tools-build-lib)]]
        (into [:div.col-sm-10
               [:h1 "All Vars in tools.build"]]
              (for [[ns vars] groups
                    :let [vars (sort-by #(-> % :name str/lower-case) vars)]
                    :when ns]
                (into [:div.var-namespace-group
                       [:h2 [:a {:href (str "/" ns)} ns]]]
                      (for [{:keys [heading vars]} (nss/group-vars vars)]
                        [:div.var-group
                         [:h3 heading]
                         [:ul.var-list
                          (for [{:keys [ns name]} vars]
                            [:li (util/$var-link ns name name)])]]))))])}))