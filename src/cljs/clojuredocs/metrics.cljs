(ns clojuredocs.metrics)

(defn track-event
  "Records a Matomo custom event (Behaviour > Events). `name` and
   `value` are optional; Matomo expects `value` to be numeric when given."
  ([category action] (track-event category action nil nil))
  ([category action name] (track-event category action name nil))
  ([category action name value]
   (.push js/_paq (clj->js (remove nil? ["trackEvent" category action name value])))))

(defn track-search
  "Records a Matomo site-search event (Behaviour > Site Search) so
   query terms and result counts are visible, including zero-result queries."
  [query result-count]
  (.push js/_paq #js ["trackSiteSearch" query false result-count]))

(defn track-search-choose [query choice]
  (track-event "search" "select-result" (str query " -> " choice)))
