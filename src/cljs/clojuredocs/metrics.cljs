(ns clojuredocs.metrics)

(defn track-event
  "Records a Matomo custom event (Behaviour > Events). `name` and `value`
   are optional but positional — Matomo reads trackEvent as
   [category action name value], so an absent `name` alongside a present
   `value` must keep its slot (else the value is recorded as the name).
   `value` should be numeric when given."
  ([category action] (track-event category action nil nil))
  ([category action name] (track-event category action name nil))
  ([category action name value]
   (let [args (cond-> ["trackEvent" category action]
                (or name value) (conj name)
                value           (conj value))]
     (.push js/_paq (clj->js args)))))

(defn track-search
  "Records a Matomo site-search event (Behaviour > Site Search) so
   query terms and result counts are visible, including zero-result queries."
  [query result-count]
  (.push js/_paq #js ["trackSiteSearch" query false result-count]))

(defn track-search-choose [query choice]
  (track-event "search" "select-result" (str query " -> " choice)))
