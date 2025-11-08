(ns clojuredocs.css
  (:require [garden.stylesheet :refer [at-media]]
            [nsfw.css :as nc]))

(def monospace-font "Monaco, Menlo, Consolas, \"Courier New\", monospace")

(def blue "rgba(66, 139, 202, 1)")
(def light-blue "rgba(66, 139, 202, 0.8)")

(def gray-1 "#333")
(def gray-2 "#777")
(def gray-3 "#bbb")
(def gray-4 "#f8f8f8")

(def color-darkest (str "light-dark(" gray-1 ", " gray-4 ")"))
(def color-dark (str "light-dark(" gray-2 ", " gray-3 ")"))
(def color-light (str "light-dark(" gray-3 ", " gray-2 ")"))
(def color-lightest (str "light-dark(" gray-4 ", " gray-1 ")"))

(def color-green "#5a5")
(def color-red "#a55")
(def color-light-pink "#fcc")

(def intro-page
  [[:.xkcd {:display 'block
            :margin-left 'auto
            :margin-right 'auto}]

   [:.intro {:margin-bottom "30px"}]
   [:.intro-page
    [:section
     {:margin-bottom "40px"}
     (nc/at-bp {:margin-bottom "20px"})]
    [:.quick-lookup-wrapper
     {:background-color 'white
      :margin "-30px -25px"
      :padding "30px 25px"}
     (nc/at-bp {:margin 0
                :padding 0})
     [:input.query
      {:font-size "20px"
       :padding "10px 15px"}
      (nc/at-bp {:font-size "16px"})]]
    [:.code-example
     [:font-size "14px"
      :padding "20px"
      :border "solid transparent 1px"
      :background-color color-lightest]]
    [:.row
     {:margin-bottom "0px"}]
    [:.top-contribs
     [:.avatar
      {:width "3.63%"
       :margin-right "0.53%"
       :margin-bottom "0.53%"
       :height 'auto}
      (nc/at-bp :xs
                {:width "7.80%"
                 :height 'auto})]]
    [:.migrate-account
     {:text-align 'right
      :font-size "12px"}]
    [:.getting-started-resources
     {:margin-bottom "20px"}
     [:li {:list-style-type 'disc
           :margin-left "30px"}]]
    [:.syntaxify
     {:padding "15px"
      :overflow 'auto
      :overflow-y 'hidden
      :font-family monospace-font
      :font-size "15px"
      :line-height "1.3em"}
     (nc/at-bp :md {:font-size "12px"})
     (nc/at-bp :sm {:font-size "13px"})
     (nc/at-bp :xs {:font-size "12px"})
     [:table {:margin "0 auto"}]]
    [:ul
     [:h4
      [:i {:margin-right "12px"
           :margin-left "-30px"}]
      [:i.fa-map-marker {:padding "0 3px"}]]]
    [:.used-by
     [:ul {:text-align 'center}]
     [:li {:display 'inline-block
           :margin-right "30px"
           :margin-bottom "20px"}]
     [:img {:max-width "200px"
            :max-height "50px"}
      (nc/at-bp :md {:max-height "35px"})
      (nc/at-bp :sm {:max-height "50px"})]]
    [:.recently-updated
     {:margin-bottom "5px"
      :font-size "13px"
      :color color-dark
      :line-height "17px"
      :clear 'both}
     [:.clear {:clear 'both}]
     [:.avatar-link
      {:margin-right "10px"
       :float 'left
       :display 'inline-block
       :padding-top "2px"}]]]
   [:.not-finding {:text-align 'right
                   :font-size "12px"
                   :line-height "18px"
                   :opacity 0.8
                   :margin-top "5px"}]])

(def autocomplete
  [[:.ac-result-link:hover {:text-decoration 'none
                            :width "100%"}]

   [:.ac-result
    [:td {:padding "10px"}]
    [:.docstring {:color color-darkest}]]

   [:ul.ac-results
    {:margin-left 0
     :margin-bottom 0}
    [:li {:margin-left 0
          :list-style-type 'none
          :cursor 'pointer
          :padding "10px"
          :margin-top "5px"
          :margin-bottom "5px"
          :padding-bottom "15px"}
     [:&.highlighted {:background-color "rgba(0,0,0,0.02)"}]
     [:p {:margin-bottom 0}]
     [:i {:margin-left 0
          :color color-dark
          :width "15px"
          :text-align 'right}]]]
   [:.ac-entry {:position 'relative}
    [:.ac-type
     {:position 'absolute
      :right 0
      :top 0
      :font-size "10px"
      :text-transform 'uppercase
      :color color-light
      :line-height "12px"}]
    [:p {:margin-bottom "10px"}]
    [:.see-alsos {:font-size "14px"
                  :color color-light}
     [:.see-also-label {:margin-right "10px"}]
     [:ul {:display 'inline-block}
      [:li {:margin 0
            :padding 0
            :margin-right "10px"
            :display 'inline-block}]]]]
   [:table.ac-results {:width "100%"
                       :font-szie "15px"}
    [:td {:padding-bottom "20px"
          :vertical-align 'top}]
    [:.ac-result
     [:.name {:vertical-align 'top
              :margin-right "10px"
              :font-weight 'bold
              :white-space 'nowrap}]
     [:.docstring {:vertical-align 'top
                   :max-width "700px"}
      [:a {:display 'block
           :color color-darkest}]]]
    [:.ac-metadata
     {:font-weight 'normal
      :color color-dark
      :font-size "12px"
      :white-sapce 'nowrap}]]])

(def var-page
  [[:.var-header
    [:h1.var-name
     {:margin-bottom "5px"}]
    [:.var-meta
     {:margin-bottom "10px"
      :font-size "13px"
      :line-height "1.3em"
      :color color-dark
      :text-align 'right}
     (nc/at-bp :xs {:text-align 'left})
     [:h2 {:margin-bottom "5px"}]
     [:ul :li :.added
      {:margin-bottom "3px"}]
     [:li {:display 'inline-block}]]
    [:.arglists {:margin-left 0
                 :padding-left 0
                 :font-family monospace-font
                 :font-size "14px"}
     [:li {:margin 0
           :display 'inline-block
           :margin-right "10px"
           :margin-bottom "10px"
           :padding "0 5px"
           :background-color color-lightest}]]]

   [:.var-page :.example-page
    [:h1 :h2 {:margin-top 0}]
    [:pre {:border 'none}]
    [:section {:margin-bottom "2em"}]
    [:section.search {:margin-bottom "1em"}]
    [:.null-state {:margin-bottom "30px"}]
    [:.docstring
     [:pre {:background-color color-lightest
            :color color-dark
            :font-size "15px"
            :line-height "1.4em"
            :margin-bottom 0
            :padding "15px 10px"}
      (nc/at-bp :md {:font-size "12px"})
      (nc/at-bp :sm {:font-size "13px"})
      (nc/at-bp :xs {:font-size "12px"})]
     [:.copyright {:text-align 'right
                   :font-size "10px"
                   :color color-light}
      [:a {:color color-light}]]]
    [:.example {:margin-bottom "30px"}]]
   [:.example-meta :.note-meta
    {:font-size "10px"
     :margin-bottom 0
     :line-height "1.3em"
     :margin-right "5px"
     :color color-dark}
    [:.avatar {:width "25px"
               :height "25px"
               :opacity 0.8}
     [:&:hover {:opacity 1}]]]
   [:.note {:margin-bottom "30px"}]
   [:.note-meta {:margin-bottom "10px"}
    [:.avatar {:margin "0 2px"}]]
   (let [r [:&
            {:font-size "15px"}
            (nc/at-bp :md {:font-size "12.5px"})
            (nc/at-bp :sm {:font-size "11.5px"})]]
     [[:.var-example [:.syntaxify r]]
      [:.tabbed-editor
       [:.example-editor
        [:textarea.form-control r]
        [:.columns-guide r]]
       [:pre.syntaxify r]]])
   [:.var-example
    {:margin-bottom "30px"}
    [:&.highlighted
     {:border-top "solid #FFA500 4px"
      :border-bottom "solid #FFA500 4px"
      :padding-top "5px"
      :padding-bottom "5px"}]
    [:.example {:margin-bottom "30px"}]
    [:.syntaxify [:table {:margin 0}]]]
   [:.example-meta
    {:color color-light}
    [:.avatar {:margin-right "5px"}]
    [:.contributors :.created :.last-updated :.links
     {:display 'inline-block
      :margin-right "5px"
      :margin-bottom "5px"}]]
   [:.example-meta :.see-also
    :.delete-controls :.note-meta
    [:.loading {:width "9px"
                :margin-bottom "2px"}]
    [:.error-deleting
     {:padding-left "3px"
      :padding-right "3px"}]]
   [:.example-page
    [:.current-example
     {:border "solid rgba(255,243,109, 0.3) 5px"
      :padding "10px"
      :background-color 'transparent
      :margin-bottom "20px"}]
    [:.example-meta
     [:.created {:display 'block}]]]
   [:.example-code
    {:margin-bottom "15px"
     :background-color color-lightest
     :border-top "solid #ddd 5px"}]
   [:.ns-tree
    {:font-size "14px"}
    [:h3 {:font-size "12px"}]
    [:li {:margin-bottom 0}]
    ["li > ul" {:margin-left "10px"}]
    [:.current {:font-weight 'bold}]]
   [:.syntaxify [:* {:font-family monospace-font}]]
   [:.add-see-also
    [:.add-see-also-content
     {:border "solid #eee 1px"
      :padding "20px"}]
    [:form {:margin-bottom "10px"}]
    [:input {:border-radius 0}]
    [:.error-message {:padding "5px"}
     [:i {:margin-right "5px"}]]
    [:.ac-results {:color color-dark}]]
   [:.note-body
    {:background-color color-lightest
     :padding "10px"
     :word-wrap 'normal}
    [:p {:margin-bottom "10px"}]]
   [:.add-example
    [:.add-example-content
     {:padding "10px"
      :background-color color-lightest}]]
   [:.add-note
    [:.instructions
     {:color color-dark
      :font-size "12px"}]
    [:textarea {:height "200px"}]]
   [:.examples-styleguide-content
    [:.syntaxify {:margin-bottom "20px"
                  :background-color color-darkest}]
    [:ul {:margin-bottom "30px"}]
    [:li {:list-style-type 'disc
          :margin-left "25px"}]]])

(def quickref
  [[:.toc {:margin-bottom "20px"}
    [:h5 {:margin 0
          :margin-bottom "10px"
          :font-size "12px"
          :margin-right 0
          :margin-top 0}]
    [:h6 {:margin-top 0}]
    [:ul {:margin-bottom "5px"}]
    [:.toc-sphere
     [:h5 {:margin-bottom "5px"}]
     [:ul {:padding 0
           :margin-bottom "15px"}]
     [:li {:margin-left "10px"
           :font-size "12px"
           :margin-bottom 0
           :line-height "1.5em"}]]]
   [:.quickref-mobile-toc
    [:h5 {:color 'white
          :margin-bottom "5px"
          :margin-top 0}]
    [:.categories {:font-size "12px"
                   :line-height "16px"}]]
   [:.sticky {:position 'fixed
              :overflow 'auto
              :bottom 0}]
   [:.sphere
    [:h2 :h3 :h4
     {:display 'inline-block
      :margin 0
      :margin-bottom "10px"}]
    [:h4 {:font-size "14px"
          :font-weight 500}]
    [:.sphere-header
     {:border-bottom "solid orange 3px"
      :margin-bottom "10px"}]
    [:.category-header
     {:border-bottom "solid #ccc 2px"
      :margin-bottom "10px"}]
    [:.category
     {:margin-bottom "40px"}]
    [:.header-reference
     {:color color-light
      :font-weight 'normal
      :float 'right
      :margin-right 0}]
    [:.quickref-header
     [:.header-reference
      {:color color-dark
       :font-size "14px"}]]
    [:.examples-count
     {:color color-light}]
    [:.dl-row
     {:clear 'both
      :padding "3px 0"}
     [:&:hover {:background-color color-light}]]
    [:dl {:font-size "13px"}]
    [:dt {:width "130px"
          :text-align 'right
          :margin-right "10px"
          :font-family monospace-font
          :font-weight 'normal}]
    [:dd
     nc/ellipsis-text
     {:width "550px"
      :margin-left "150px"}
     (nc/at-bp :md {:width "430px"})
     (nc/at-bp :sm {:width "380px"})]
    [:dt {:font-weight 'normal}]
    (nc/at-bp :xs
              [:.dl-row {:padding 0}]
              [:dt {:display 'block
                    :text-align 'left
                    :padding-top "10px"
                    :clear 'both}]
              [:dd {:display 'block
                    :border-bottom "solid #ddd 1px"
                    :padding-bottom "10px"
                    :text-align 'left
                    :width "100%"
                    :margin-left 0}])]
   [:.quickref-other
    {:display 'inline-block}
    [:li {:display 'inline-block}]]])

(def example-editor
  [[:.example-editor
    {:background-color 'white
     :margin-bottom "10px"}
    [:&.disabled {:background-color color-light}]
    [:form {:margin-bottom "20px"}]
    [:textarea {:font-family monospace-font
                :margin-bottom 0
                :padding "10px"
                :font-size "13px"}]
    [:.add-example-controls
     {:margin-bottom "10px"}]
    [:.add-example-content
     {:padding "30px"
      :background-color color-lightest}]]
   [:p.example-instructions
    {:margin-bottom "10px"
     :font-size "12px"
     :line-height "20px"
     :color color-dark}]])

(def styleguide-page
  [(let [color color-lightest
         size 40]
     [:.checker-bg
      {:background-color color-lightest
       :background-image (str "linear-gradient(45deg,"
                              color
                              " 25%,transparent 25%,transparent 75%,"
                              color " 75%," color "),"
                              "linear-gradient(45deg,"
                              color " 25%, transparent 25%, transparent 75%,"
                              color " 75%, " color ")")
       :background-size (str size "px " size "px")
       :background-position (str "0 0, "
                                 (/ size 2) "px"
                                 " "
                                 (/ size 2) "px")
       :padding "20px"}])
   [:.styleguide-page
    [:.styleguide-section
     {:margin-bottom "50px"}
     ["& > h2" {:border-bottom "solid #ddd 1px"
                :padding-bottom "5px"}]]
    [:.example
     {:margin-bottom "30px"}]
    [:.buttons-ex
     [:button
      {:margin-right "10px"}]]
    [:.contextual-bgs
     ["& > div"
      {:display 'inline-block
       :padding "10px 20px"
       :margin-right "10px"}]]
    [:.headers-ex
     [:h1 :h2 :h3 :h4 :h5 :h6
      {:line-height "50px"
       :margin 0}]]]])

(def jobs
  [[:.job-preview
    [:.job-location :.comp :.remote-ok :.comp-equity :.comp-cash
     {:color color-dark}]]
   [:.jobs-page
    [:.job-preview-wrapper
     :.list-jobs-header
     {:border-bottom "solid #eee 1px"}]
    [:.job-preview-wrapper
     {:padding "20px 0"}]
    [:.job-preview
     [:.comp
      {:margin-top "3px"
       :color color-light
       :font-size "13px"}]]
    [:.job-info
     [:.section-header
      {:border-bottom "solid #eee 1px"
       :color color-dark
       :font-size "20px"}]
     [:ul {:margin-bottom "20px"}]
     [:h1 {:font-size "20px"
           :font-weight 'bold}]]
    [:.btn {:border-radius 0}]
    [:.job-description :.company-description
     [:ul {:margin-left "20px"}]
     [:li {:margin-bottom "0.5em"
           :list-style-type 'disc}]]
    [:.apply-now
     {:border-top "solid #ccc 1px"
      :border-bottom "solid #ccc 1px"
      :padding "15px 0"
      :text-align 'center}
     [:.btn {:width "75%"
             :border-radius 0}]]]])

(def app
  [nc/flex-defaults
   [":root" {:color-scheme "light dark"}]
   [:html :body {:-webkit-font-smothing 'antialiased
                 :height "100%"
                 :font-family "\"Helvetica Neue\", Helvetica, sans-serif"
                 :color color-darkest
                 :background-color color-lightest}]
   [:body {:font-size "16px"
           :line-height "1.5em"
           :transition "all 0.2s ease-in"
           :height "100%"}
    [:&.search-active {:background-color color-darkest}]]
   [:img {:max-width "100%"}]
   [:h1 :h2 :h3 :h4 :h5 :h6 {:font-weight 'normal}]
   [:h1 {:font-size "28px"
         :line-height "36px"
         :margin-bottom "20px"}
    [:&:first-of-type {:margin-top 0}]]
   (at-media {:max-width "767px"}
             [:h1 {:font-size "20px"
                   :line-height "30px"}])

   [:h2 {:font-size "24px"
         :line-height "34px"
         :margin-bottom "20px"}]
   [:h5 {:font-size "15px"
         :font-weight 500
         :text-transform 'uppercase
         :letter-spacing "1px"
         :color color-dark
         :margin-bottom "10px"}]
   [:p {:font-size "17px"
        :margin-bottom "1.4em"
        :line-height "1.6em"}]
   [:li {:margin-bottom "0.5em"
         :list-style-type 'none}]
   [:pre {:border-radius "0px"
          :border 'none
          :word-wrap 'normal}]
   [:ul {:padding 0}]
   [:section {:margin-bottom "40px"}]
   [:code {:font-size "14px"}]
   ["a > code" {:color blue}]
   [:.badge {:background-color #_"rgba(0,0,0,0.05)" 'transparent
             :color "rgba(0,0,0,0.2)"
             :border-radius "3px"
             :padding "1px 5px"
             :font-weight 500
             :margin-left "5px"
             :font-size "14px"
             :line-height "22px"}]

   ;; check
   [:.sticky-wrapper {:min-height "100px"
                      :height ["auto !important" "100%"]}]
   [:footer :.sticky-push {:height "200px"}]
   [:footer
    {:text-align 'center
     :font-size "12px"
     :padding-top "130px"}
    [:.divider {:font-size "16px"}]
    [:.ctas {:line-height "30px"}
     [:iframe {:margin-bottom "-6px"
               :margin-left "10px"}]
     [:.gh-starred-count {:width "95px"}]]
    [:.left {:text-align 'right}]
    [:.right {:text-align 'left}]

    (nc/at-bp :xs
              [:.left :.right {:text-align 'center}])]

   [:.avatar
    {:display 'inline-block
     :border-radius "3px"
     :width "48px"
     :height "48px"}]
   [:body.user-page
    [:.user-avatar
     [:.avatar {:width "200px"
                :height "200px"}]]]
   [:.form-group
    [:.loading
     {:margin-top "9px"
      :margin-bottom "10px"}]
    [:.error-message
     {:margin-top "5px"
      :margin-right "10px"
      :padding "10px"}
     [:i {:margin-right "8px"}]]]

   [:header.navbar
    {:border-radius 0
     :margin-bottom "20px"}
    (nc/at-bp :xs {:margin-bottom "10px"})
    [".nav > li > a" {:padding "15px 12px"}]
    [:i {:margin-right "5px"}]
    (nc/at-bp :xs [:.navbar-brand
                   {:float 'none
                    :display 'inline-block
                    :padding-left 0}])
    [:.btn.mobile-menu {:border 'none
                        :margin-top "5px"
                        :font-size "18px"
                        :display 'none}
     [:i {:margin 0}]]
    [:.user-area {:font-size "14px"
                  :color color-dark}]
    [:.navbar-nav {:padding-right 0
                   :margin 0}
     [:li {:margin-bottom 0
           :font-size "14px"}]]
    [:.nav-search-widget
     [:form
      {:margin-right "10px"}]]
    [:.gh-starred-count
     {:margin-top "15px"
      :line-height "20px"
      :margin-left "10px"}]
    [:.clojure-version
     {:font-weight 300
      :font-size "12px"
      :font-family monospace-font
      :color color-light
      :line-height "21px"}
     [:a {:line-height "20px"
          :color color-light
          :font-weight 300}]]]
   [:.mobile-nav-bar {:display 'none}]
   [:.navbar-nav
    [:i {:margin-right "5px"}]
    [:.avatar {:width "22px"
               :height "22px"
               :margin-right "5px"}]]
   [:.mobile-nav-menu
    {:display 'none
     :overflow-y 'scroll
     :overflow-x 'hidden
     :-webkit-overflow-scrolling 'touch
     :height "100%"}]
   [:.desktop-side-nav
    {:overflow-y 'scroll
     :max-height "100%"}
    [:.badge
     {:float 'right}]]
   [:.mobile-nav-menu
    [:.badge
     {:font-size "16px"
      :color "rgba(255,255,255,0.5)"
      :font-weight 300
      :float 'right
      :line-height "22px"
      :margin 0
      :padding 0}]]
   (nc/at-bp :xs
             [:header.navbar
              [:.btn.mobile-menu
               {:display 'block
                :margin-right "-11px"}]
              [:.nav-search-widget {:display 'none}]
              [:.navbar-nav {:display 'none}]]
             [:.mobile-push-wrapper
              (merge
               {:right 0
                :position 'relative
                :padding-top "60px"
                :transform "translate3d(0,0,0)"}
               (nc/transition "all 0.2s ease"))]
             [:.desktop-nav-bar :.desktop-side-nav {:display 'none}]
             [:.mobile-nav-bar
              (nc/transition "all 0.2s ease")
              {:background-color 'white
               :position 'fixed
               :top 0
               :width "100%"
               :z-index 1000
               :display 'block}
              [:header {:margin-bottom 0}]]
             [:.mobile-nav-menu
              (nc/transition "all 0.2s ease")
              {:transform "translate3d(200px,0,0)"}
              {:display 'block
               :position 'fixed
               :width "200px"
               :z-index 10000
               :background-color light-blue
               :color 'white
               :font-weight 300
               :top 0
               :bottom 0
               :right 0
               :margin 0
               :overflow-y 'scroll}
              [:h4 {:padding "5px 10px"
                    :border-bottom "solid rgba(255,255,255,0.3) 1px"
                    :margin 0
                    :color "rgba(255,255,255,0.8)"
                    :font-size "14px"
                    :letter-spacing "1px"
                    :font-weight 500}
               [:i {:margin-right "5px"}]]
              [:li {:margin 0}
               [:a {:color 'white}
                [:&:hover {:background-color blue}]]]
              [:.navbar-nav {:margin 0}]]
             [:.mobile-push-wrapper.mobile-push
              (nc/transition "all 0.2s ease")
              {:transform "translate3d(-200px,0,0)"
               :right 0}]
             [:.mobile-nav-menu.mobile-push
              (nc/transition "all 0.2s ease")
              {:transform "translate3d(0,0,0)"}
              [:header.navbar
               (nc/transition "all 0.2s ease")
               {:right "200px"}]]
             [:.mobile-nav-bar.mobile-push
              (nc/transition "all 0.2s ease")
              {:transform "translate3d(-200px,0,0)"}]
             [:.page-toc {:display 'none}]
             [:body.search-active {:background-color 'transparent}]
             [:.clojure-version {:font-weight 300
                                 :color "rgba(255,255,255,0.7)"
                                 :font-family monospace-font
                                 :margin-left "5px"
                                 :font-size "10px"}])



   [:.null-state {:background-color color-lightest
                  :text-align 'center
                  :padding "20px"
                  :color color-dark}
    [:code {:color color-dark}]
    [:a {:color "rgba(66, 139, 202, 0.7)"}]]

   intro-page

   [:.see-also
    {:margin-bottom "20px"}
    [:a {:color color-dark}
     [:.name {:color light-blue}
      [:&:hover {:color blue}]]]
    [:p {:color color-dark
         :margin-bottom 0
         :font-size "15px"}]
    [:.var-title {:margin-bottom "3px"}]
    [:.meta {:font-size "10px"
             :color color-light
             :text-transform 'uppercase}
     [:a {:color color-light}]]
    [:.delete-confirmation [:a {:color light-blue}]]]

   [:input.loading
    {:background-image "url('/img/loading.gif')"
     :background-repeat 'no-repeat
     :background-position "right 8px center"}]

   [:form.search
    [:input.query
     {:border-radius 0
      :min-width "200px"
      :height "100%"
      :font-size "14px"
      :padding "6px 12px"}]
    [:input.placeholder {:visibility 'hidden}]]

   [:.syntaxify {:padding 0
                 :background-color 'transparent
                 :word-wrap 'normal}]

   autocomplete

   var-page

   quickref

   example-editor

   styleguide-page

   [:.example-body
    {:padding "10px"
     :font-size "14px"
     :background-color color-lightest
     :margin-bottom "10px"}]

   [:pre.raw-example
    {:background-color color-lightest
     :margin-bottom "0px"
     :padding "15px"
     :font-size "14px"}]
   [:.recent-pages
    {:font-size "14px"
     :margin-bottom "30px"}
    [:li {:margin-bottom 0}]
    (nc/at-bp :xs
              [:& {:margin-bottom "10px"}]
              [:h3 {:display 'inline-block
                    :margin-right "20px"}]
              [:ul {:display 'inline-block}]
              [:li {:display 'inline-block
                    :margin-right "10px"
                    :background-color color-lightest
                    :padding "0 5px"}])]
   [:.var-page-nav {:margin-bottom "20px"}]
   [:.ns-page
    [:h1 {:margin-bottom "20px"}]
    [:pre.doc {:margin-bottom "20px"}]
    [:section {:margin-bottom "50px"}]]
   [:.var-group
    {:clear 'both}
    [:h4 {:margin-top "20px"
          :margin-bottom "5px"
          :border-bottom "solid #ccc 2px"}]
    [:.dl-row {:clear 'both
               :padding "3px 0"}
     [:&:hover {:background-color color-light}]]
    [:dl {:font-size "13px"}]
    [:dt {:min-width "150px"
          :text-align 'right
          :margin-right "10px"
          :font-family monospace-font
          :font-weight 'normal}]
    [:dd
     nc/ellipsis-text
     {:width "580px"}
     (nc/at-bp :md {:width "460px"})
     (nc/at-bp :sm {:width "400px"})]
    (nc/at-bp :xs
              [:.dl-row {:padding 0}]
              [:dt {:display 'block
                    :text-align 'left
                    :padding-top "10px"}]
              [:dd {:display 'block
                    :border-bottom "solid #ddd 1px"
                    :width "100%"
                    :padding-bottom "10px"}])
    [:.no-doc {:color color-light}]
    [:.ns-table
     {:width "100%"}
     [:.name {:text-align 'right}]
     [".name > span" {:padding-right "20px"
                      :font-family monospace-font
                      :font-size "12px"
                      :line-height "24px"
                      :vertical-align 'middle}]
     [:.doc
      nc/ellipsis-text
      {:line-height "24px"
       :font-size "14px"}]
     [:.heading {:margin-top "20px"
                 :margin-bottom "5px"
                 :border-bottom "solid #ccc 2px"}]]]
   [:.error-page
    [:h1 {:text-align 'center
          :font-size "20px"
          :color color-darkest}]
    [:a.four-oh-four
     {:max-width "300px"
      :max-height "100%"
      :display 'block
      :margin "20px auto"}]]
   [:.muted {:color color-light}]
   [:.var-link
    {:color color-dark}
    [:.namespace {:color color-dark}]
    [:.name {:color blue}]]
   [:.staging-banner
    {:text-align 'center
     :background-color "rgb(201,48,44)"
     :color "rgba(255,255,255, 0.8)"
     :font-size "14px"
     :font-weight 400
     :padding "3px"}]
   [:.toggle-controls :.login-required-message
    {:text-align 'right
     :margin-bottom "4px"
     :font-size "12px"}]
   [:.toggle-link {:color light-blue}]
   [:.sidenav {:font-size "14px"
               :max-height "100%"}
    [:h5 {:font-size "14px"
          :margin-top 0}]
    ["& > ul" {:margin-bottom "20px"}]
    [:li {:margin-bottom 0}]
    ["li > ul" {:margin-left "10px"}]
    [:.current {:font-weight 'bold}]
    (nc/at-bp :xs {:display 'none})

    [:.library-nav {:max-height "100%"
                    :overflow-y 'scroll
                    :overflow-x 'hidden}]]
   [:.live-preview
    {:background-color color-lightest
     :margin-bottom "10px"
     :word-wrap 'normal
     :overflow-x 'scroll}
    [:.empty-live-preview
     {:text-align 'center
      :color color-light
      :font-size "14px"
      :padding "10px"}]]
   [:.ac-results-widget {:margin "0 -20px"
                         :padding "0 20px"}]
   [:.search-active
    [:ac-results-widget
     {:background-color 'white}
     [:ul.ac-results
      {:overflow 'auto}]
     ["ul.ac-results > li"
      [:&:first-child {:margin-top "20px"}]
      [:&:last-child {:margin-bottom "20px"}]]]]
   [:.core-library-page
    [:pre :.syntaxify
     {:background-color color-darkest
      :padding "20px"}]
    [:h2 {:border-bottom "solid #ddd 2px"}]
    [:h3 {:margin-top "30px"}]]
   [:.markdown
    [:ol :ul
     {:padding-left 0
      :margin-bottom "20px"}
     [:li {:margin-left "25px"
           :padding 0
           :margin-bottom 0}]]
    [:ul [:li {:list-style-type 'disc}]]
    [:ol [:li {:list-style-type 'decimal}]]
    [:.syntaxify [:pre {:margin-bottom "40px"}]]
    [:h1 :h2 :h3 :h4 :h5 {:margin-bottom "0.5em"}]]
   [:.sg-example
    {:margin-bottom "30px"
     :border "solid #eee 1px"
     :padding "3px"}
    [:.caption
     {:text-align 'center
      :background-color color-lightest
      :padding "5px"
      :color color-dark
      :font-size "11px"
      :letter-spacing "1px"}]]
   [:.field-schemas :table.schema
    [:td {:padding-right "10px"
          :vertical-align 'top}]]
   [:table.schema
    {:border-top "solid #ccc 3px"
     :width "100%"}
    [:td {:border 'none
          :padding "5px"}]
    [:.aside {:font-size "12px"}]]
   [:.test-result
    {:padding "3px"
     :color 'white}
    [:&.pass {:background-color color-green}]
    [:&.fail {:background-color color-red}]
    [:&.error {:background-color color-dark}]
    [:&.pending {:background-color color-dark}]]
   [:.add-example-page
    [:.syntaxify
     [:code {:font-size "12px"}]]
    [:textarea :pre {:font-size "12px"}]]
   [:.tabbed-editor
    [:.example-editor
     {:border-color color-light
      :border "solid #eee 1px"
      :border-top 'none}
     [:.columns-guide
      {:background-color 'transparent
       :color color-light
       :margin 0}]]
    [:.null-state
     {:background-color 'white
      :border 'none
      :border-top 'none}]
    [:textarea
     {:border 'none
      :border-radius 0
      :overflow 'auto
      :outline 'none
      :outline-width 0
      :resize 'none
      :box-shadow 'none}
     [:&:focus {:box-shadow 'none}]]
    [:.nav-tabs
     {:background-color color-light
      :border-bottom 'none}
     [:li {:margin-bottom 0
           :border-bottom 'none}
      ["& > li.active" {:border-bottom 'none}
       [:&:hover {:border-color color-light}]
       ["& > a:focus" {:border-color color-light}]]]
     ["li.active > a" "li.active>a:hover" "li.active>a:focus"
      {:border-color color-light
       :border-bottom 'none}
      [:&:hover {:border-bottom 'none
                 :margin-bottom 0}]]
     [:li
      [:a {:border-radius 0
           :border-bottom 'none
           :margin-bottom 0}
       [:&:hover {:border-bottom 'none
                  :margin-bottom 0}]]]]
    [:.live-preview
     {:min-height "227px"
      :border "solid #eee 1px"
      :border-top 'none
      :padding "10px"
      :background-color color-lightest}
     [:li {:margin-left "20px"
           :list-style-type 'disc
           :margin-bottom 0}]]]
   [:.sg-example-inspector
    [:.sg-inspector-state
     [:textarea
      {:width "100%"
       :border "solid #eee 1px"
       :padding "5px"}
      [:&.error {:background-color color-light-pink}]]]
    [:.sg-inspector-outlets
     [:.message
      {:margin-bottom "10px"}
      [:h6 {:padding "3px"
            :margin 0
            :color color-dark}]
      [:pre {:margin 0}]]]]
   [:.migrate-user-page
    [:.controls {:text-align 'center}
     [:.btn {:font-size "30px"
             :padding "5px 30px"}]]]
   [:.var-namespace-group
    [:h2 {:border-bottom "solid orange 2px"}]]
   [:.var-group
    [:h3 {:border-bottom "solid #ccc 1px"}]
    [:ul.var-list
     [:li {:display 'inline-block
           :margin-right "4px"
           :background-color color-light
           :padding "0 4px"}]]]
   [:.search-results-page
    [:.search-results-header
     {:border-bottom "solid #ddd 1px"
      :margin-bottom "20px"}
     [:h1 {:margin-bottom 0}]
     [:p {:color color-dark
          :margin-bottom "5px"}]]
    [:.search-results
     {:margin-bottom "30px"}]
    [:.search-result
     {:margin-bottom "30px"
      :line-height "24px"}
     [:h1 {:border-bottom "solid #ccc 2px"}]
     [:h2 {:margin-top "5px"
           :margin-bottom 0
           :font-size "24px"}]
     [:h3 {:margin-top 0
           :margin-bottom "5px"
           :font-size "16px"
           :font-weight 500
           :color color-dark}]
     [:p {:margin-bottom "5px"
          :font-size "16px"
          :line-height "24px"}]
     [:.examples-count {:font-size "13px"
                        :color color-dark}]
     [:.arglists
      {:margin-left 0
       :margin-bottom 0
       :padding-left 0
       :font-family monospace-font
       :font-size "14px"}
      [:li {:margin 0
            :display 'inline-block
            :margin-right "10px"
            :margin-bottom "10px"
            :padding "0 5px"
            :background-color color-lightest}]]
     [:.meta-info
      {:font-size "13px"
       :color color-dark
       :line-height "20px"}]
     [:.see-alsos
      [:.see-also
       {:display 'inline-block
        :margin-bottom 0
        :line-height "12px"}
       [:a {:color blue}]
       [:.ns {:color color-dark}]]]]
    [:.search-controls
     {:text-align 'center
      :color color-dark}]]

   jobs

   [:.header-banner
    {:background-color blue
     :color 'white
     :text-align 'center
     :padding "3px"
     :font-weight '300
     :font-size "13px"}
    [:a {:color 'white
         :text-decoration 'underline}]]])
