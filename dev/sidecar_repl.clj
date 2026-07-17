;; sidecar_repl.clj — REPL scratchpad for investigation
;; Not part of the application. Eval forms here to explore the codebase.
;; See decisions.md entry: 2026-06-09 — Sidecar REPL for entity model verification

(ns dev.sidecar-repl
  (:require [clojuredocs.search :as search]
            [clojuredocs.search.static :as static]
            [clojuredocs.search.compat :as compat]
            [somnium.congomongo :as mon]))

(comment
  ;; === Startup Flow Investigation ===
  
  ;; Q: What keys does the Library entity actually have?
  (keys search/clojure-lib)
  ;;=> (:library-url :version :source-base-url :gh-tag-url :namespaces :vars)
  
  (dissoc search/clojure-lib :vars :namespaces) ;; just the scalar attrs
  ;;=> {:library-url "https://github.com/clojure/clojure",
  ;;    :version "1.12.4",
  ;;    :source-base-url "https://github.com/clojure/clojure/blob/clojure-1.12.4",
  ;;    :gh-tag-url "https://github.com/clojure/clojure/tree/clojure-1.12.4"}
  
  ;; Q: What keys does a Var have after the gather pipeline?
  (keys (first (:vars search/clojure-lib)))
  ;;=> (:ns :name :doc :arglists :library-url :type :href)
  (search/lookup "clojure.core/map")
  ;;=> {:added "1.0",
  ;;    :ns "clojure.core",
  ;;    :name "map",
  ;;    :file "clojure/core.clj",
  ;;    :static true,
  ;;    :type "function",
  ;;    :column 1,
  ;;    :line 2744,
  ;;    :arglists ("f" "f coll" "f c1 c2" "f c1 c2 c3" "f c1 c2 c3 & colls"),
  ;;    :doc
  ;;    "Returns a lazy sequence consisting of the result of applying f to\n  the set of first items of each coll, followed by applying f to the\n  set of second items in each coll, until any one of the colls is\n  exhausted.  Any remaining items in other colls are ignored. Function\n  f should accept number-of-colls arguments. Returns a transducer when\n  no collection is provided.",
  ;;    :library-url "https://github.com/clojure/clojure",
  ;;    :href "/clojure.core/map"}
  
  ;; Q: Does :url actually appear on vars? :source-url? :usage-urls?
  (:url (search/lookup "clojure.core/map"))
  ;;=> nil
  (:source-url (search/lookup "clojure.core/map"))
  ;;=> nil
  (:usage-urls (search/lookup "clojure.core/map"))
  ;;=> nil
  
  ;; Q: What does a Namespace look like after gather?
  (first (:namespaces search/clojure-lib))
  ;;=> {:doc "Fundamental library of the Clojure language", :name "clojure.core"}
  (keys (first (:namespaces search/clojure-lib)))
  ;;=> (:doc :name)
  
  ;; Q: How many of each?
  (count (:vars search/clojure-lib))
  ;;=> 1572
  (count (:namespaces search/clojure-lib))
  ;;=> 38
  
  ;; Q: What does dialect compat data look like for a var?
  (compat/dialects-for "clojure.core" "map")
  ;;=> #{:bb :clj :cljs}
  (compat/dialects-for "clojure.core" "gen-class")
  ;;=> #{:clj}
  
  ;; === Namespace Key Coverage ===
  ;; Q: Do any namespaces have :added or :no-doc?
  (->> (:namespaces search/clojure-lib)
       (filter #(or (contains? % :added) (contains? % :no-doc)))
       (mapv #(select-keys % [:name :added :no-doc])))
  ;;=> [{:name "clojure.pprint", :added "1.2"}
  ;;    {:name "clojure.reflect", :added "1.3"}]
  ;; Only 2 of 38 have :added. None have :no-doc.
  ;; CSV claimed 4 attributes; only :name and :doc are universal.
  
  ;; === Var Key Universe ===
  ;; Q: What is the full set of keys across all 1,572 vars, with frequencies?
  (->> (:vars search/clojure-lib)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:ns 1572] [:name 1572] [:type 1572] [:arglists 1572]
  ;;    [:library-url 1572] [:href 1572] [:file 1482] [:column 1482]
  ;;    [:line 1482] [:doc 1293] [:added 907] [:static 319]
  ;;    [:macro 190] [:tag 122] [:dynamic 56] [:skip-wiki 31]
  ;;    [:deprecated 18] [:special-form 4] [:forms 4] [:url 1])
  ;; 20 distinct keys. 6 always present, 14 sparse.
  ;; :url appears on exactly 1 var (letfn) with nil value.
  ;; CSV-claimed :source-url and :usage-urls: 0 occurrences (errata #1).
  
  ;; Q: Which var has :url? What's its value?
  (->> (:vars search/clojure-lib)
       (filter #(contains? % :url))
       (mapv #(select-keys % [:ns :name :url])))
  ;;=> [{:ns "clojure.core", :name "letfn", :url nil}]
  
  ;; Q: What do the 4 special forms look like?
  (->> (:vars search/clojure-lib)
       (filter :special-form)
       (mapv #(select-keys % [:ns :name :url :forms :special-form])))
  ;;=> [{:ns "clojure.core", :name "let",
  ;;     :forms ("(let [bindings*] exprs*)"), :special-form true}
  ;;    {:ns "clojure.core", :name "fn",
  ;;     :forms ("(fn name? [params*] exprs*)" "(fn name? ([params*] exprs*) +)"),
  ;;     :special-form true}
  ;;    {:ns "clojure.core", :name "letfn", :url nil,
  ;;     :forms ("(letfn [fnspecs*] exprs*)"), :special-form true}
  ;;    {:ns "clojure.core", :name "loop",
  ;;     :forms ("(loop [bindings*] exprs*)"), :special-form true}]
  
  ;; === Cardinality: MongoDB Collection Counts ===
  {:examples             (mon/fetch-count :examples)
   :example-histories    (mon/fetch-count :example-histories)
   :notes                (mon/fetch-count :notes)
   :see-alsos            (mon/fetch-count :see-alsos)
   :users                (mon/fetch-count :users)
   :legacy-var-redirects (mon/fetch-count :legacy-var-redirects)}
;;   => {:examples             2671, 
;;       :example-histories    3358, 
;;       :notes                497,
;;       :see-alsos            2494, 
;;       :users                4902, 
;;       :legacy-var-redirects 1654}
  
  ;; === Cardinality: Active vs Deleted, User Sources ===
  {:deleted-examples   (mon/fetch-count :examples :where {:deleted-at {:$ne nil}})
   :active-examples    (mon/fetch-count :examples :where {:deleted-at nil})
   :vars-with-examples (->> (mon/fetch :examples :where {:deleted-at nil} :only [:var])
                            (map :var)
                            (map #(str (:ns %) "/" (:name %)))
                            distinct
                            count)
   :users-clojuredocs  (mon/fetch-count :users :where {:account-source "clojuredocs"})
   :users-github       (mon/fetch-count :users :where {:account-source "github"})}
  ;;=> {:deleted-examples 54, 
  ;;    :active-examples 2617,
  ;;    :vars-with-examples 997,
  ;;    :users-clojuredocs 1252, 
  ;;    :users-github 3650}
  ;; 37% of vars (575 of 1572) have zero examples.
  ;; 1252 legacy users + 3650 GitHub users = 4902 total.
  
  ;; === Update Frequency: Date Ranges ===
  (let [latest-ex   (first (mon/fetch :examples :sort {:created-at -1} :limit 1))
        oldest-ex   (first (mon/fetch :examples :sort {:created-at 1} :limit 1))
        latest-note (first (mon/fetch :notes :sort {:created-at -1} :limit 1))
        latest-sa   (first (mon/fetch :see-alsos :sort {:created-at -1} :limit 1))
        latest-hist (first (mon/fetch :example-histories :sort {:created-at -1} :limit 1))]
    {:examples          {:oldest (java.util.Date. (long (:created-at oldest-ex)))
                         :newest (java.util.Date. (long (:created-at latest-ex)))}
     :example-histories {:newest (java.util.Date. (long (:created-at latest-hist)))}
     :notes             {:newest (java.util.Date. (long (:created-at latest-note)))}
     :see-alsos         {:newest (java.util.Date. (long (:created-at latest-sa)))}})
  ;;=> {:examples {:oldest #inst "2010-07-03T16:32:31.000-00:00"
  ;;               :newest #inst "2025-09-05T20:10:01.400-00:00"}
  ;;    :example-histories {:newest #inst "2025-09-10T17:23:28.124-00:00"}
  ;;    :notes {:newest #inst "2025-09-09T04:19:52.552-00:00"}
  ;;    :see-alsos {:newest #inst "2025-09-06T18:45:52.247-00:00"}}
  ;; ~15 years of data. All timestamped collections last modified Sep 2025.
  
  ;; === Document Shape: Example ===
  ;; Q: What does an actual Example document look like?
  (let [ex (first (mon/fetch :examples :limit 1))]
    {:keys   (keys ex)
     :sample (-> ex
                 (dissoc :body)
                 (update :author #(select-keys % [:login :account-source])))})
  ;;=> {:keys (:updated-at :created-at :body :editors :author :var :_id)
  ;;    :sample {:updated-at 1711017078763, 
  ;;             :created-at 1280746350000,
  ;;             :editors [{:avatar-url "...", 
  ;;                        :account-source "github", 
  ;;                        :login "jafingerhut"} ...]
  ;;             :author {:login "gstamp", :account-source "clojuredocs"}
  ;;             :var {:ns "clojure.core", :name "sorted-map",
  ;;                   :library-url "https://github.com/clojure/clojure"}
  ;;             :_id #object[ObjectId ...]}}
  ;; Note: :editors contains duplicates (same user appears 3x). No dedup.
  ;; Note: :author has account-source "clojuredocs" (legacy), editors mix both.
  ;; Note: :var is embedded — denormalized copy of var identity.

  ;; === Why does letfn have :url but not the other special forms? ===
  ;; Q: Compare raw JVM metadata across the 4 special forms
  (let [special-forms ["let" "fn" "letfn" "loop"]]
    (mapv (fn [n]
            (let [m (meta (resolve (symbol "clojure.core" n)))]
              {:name n
               :has-url? (contains? m :url)
               :url (:url m)
               :special-form (:special-form m)
               :macro (:macro m)}))
          special-forms))
  ;;=> [{:name "let",    :has-url? false, :url nil, :special-form true, :macro true}
  ;;    {:name "fn",     :has-url? false, :url nil, :special-form true, :macro true}
  ;;    {:name "letfn",  :has-url? true,  :url nil, :special-form true, :macro true}
  ;;    {:name "loop",   :has-url? false, :url nil, :special-form true, :macro true}]
  ;; Only letfn has the :url KEY (with nil value). Others don't have the key at all.

  ;; Q: What metadata keys does letfn have that let doesn't?
  (clojure.set/difference
    (set (keys (meta #'clojure.core/letfn)))
    (set (keys (meta #'clojure.core/let))))
  ;;=> #{:url}
  ;; letfn is defined at line 6622 in core.clj, let at line 4523.
  ;; The :url key is an incomplete annotation in Clojure's source —
  ;; someone started to add a clojure.org docs link and left it nil.
  ;; It's vestigial: 1 var, nil value, no semantic meaning.
  ;; It passes through search/var-keys because :url is in the select-keys list.
  )

(comment
  ;; === MongoDB Key Universe: All Collections ===
  ;; Full key-frequency scan for every collection.
  ;; This is the canonical verification that the entity-attribute-model
  ;; is correct — it catches both fabrications and omissions.
  ;; Run: 2026-06-09

  ;; --- Examples (2,671 docs) ---
  (->> (mon/fetch :examples)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:var 2671] [:body 2671] [:created-at 2671] [:author 2671]
  ;;    [:_id 2671] [:updated-at 2671] [:editors 1708] [:deleted-at 54])
  ;; 6 universal keys + 2 sparse (editors: 64%, deleted-at: 2%)

  ;; --- Example Histories (3,358 docs) ---
  (->> (mon/fetch :example-histories)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:editor 3358] [:body 3358] [:created-at 3358]
  ;;    [:example-id 3358] [:_id 3358])
  ;; 5 keys, all universal. No sparse fields.

  ;; --- Notes (497 docs) ---
  (->> (mon/fetch :notes)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:updated-at 497] [:var 497] [:body 497]
  ;;    [:created-at 497] [:author 497] [:_id 497])
  ;; 6 keys, all universal. NO :deleted-at — hard delete only.

  ;; --- See-Alsos (2,494 docs) ---
  (->> (mon/fetch :see-alsos)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:created-at 2494] [:author 2494] [:to-var 2494]
  ;;    [:from-var 2494] [:_id 2494])
  ;; 5 keys, all universal. NO :deleted-at — hard delete only.

  ;; --- Users (4,902 docs) ---
  (->> (mon/fetch :users)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:login 4902] [:account-source 4902]
  ;;    [:avatar-url 4902] [:_id 4902])
  ;; 4 keys, all universal. NO :email, :created-at, or :reputation.
  ;; CSV fabricated several User attributes that don't exist.

  ;; --- Legacy Var Redirects (1,654 docs) ---
  (->> (mon/fetch :legacy-var-redirects)
       (mapcat keys)
       frequencies
       (sort-by val >))
  ;;=> ([:function-id 1654] [:library-url 1654] [:ns 1654]
  ;;    [:name 1654] [:_id 1654])
  ;; 5 keys, all universal. CSV omitted :library-url (errata #7).

  ;; === Document Shape: User ===
  ;; Q: Do github and clojuredocs users have the same shape?
  (let [gh-user (first (mon/fetch :users :where {:account-source "github"}))
        cd-user (first (mon/fetch :users :where {:account-source "clojuredocs"}))]
    {:github-keys      (set (keys gh-user))
     :clojuredocs-keys (set (keys cd-user))
     :same-shape?      (= (set (keys gh-user)) (set (keys cd-user)))})
  ;;=> {:github-keys #{:login :account-source :avatar-url :_id}
  ;;    :clojuredocs-keys #{:login :account-source :avatar-url :_id}
  ;;    :same-shape? true}
  ;; Both user types have identical key sets.

  ;; === Document Shape: LegacyVarRedirect ===
  (let [lvr (first (mon/fetch :legacy-var-redirects :limit 1))]
    {:keys   (keys lvr)
     :sample (dissoc lvr :_id)})
  ;;=> {:keys (:_id :function-id :library-url :ns :name)
  ;;    :sample {:function-id "clojure.core/send"
  ;;             :library-url "https://github.com/clojure/clojure"
  ;;             :ns "clojure.core"
  ;;             :name "send"}}
  ;; CSV had only 3 attributes; actual has 5 (missing _id and library-url).

  ;; === Deletion Semantics Summary ===
  ;; Examples: soft-delete via :deleted-at (54 of 2,671 = 2%)
  ;; Notes: hard-delete only (no :deleted-at field exists)
  ;; SeeAlsos: hard-delete only (no :deleted-at field exists)
  ;; ExampleHistories: append-only (no deletion mechanism)
  ;; Users: no deletion mechanism observed
  ;; LegacyVarRedirects: no deletion mechanism observed
  nil)

(comment
  ;; === Embedded Sub-Document Shape Consistency ===
  ;; Q: Are embedded :var sub-docs the same shape across all collections?
  ;; Run: 2026-06-09

  (let [ex-vars   (->> (mon/fetch :examples :only [:var])
                       (map #(set (keys (:var %)))))
        note-vars (->> (mon/fetch :notes :only [:var])
                       (map #(set (keys (:var %)))))
        sa-from   (->> (mon/fetch :see-alsos :only [:from-var])
                       (map #(set (keys (:from-var %)))))
        sa-to     (->> (mon/fetch :see-alsos :only [:to-var])
                       (map #(set (keys (:to-var %)))))]
    {:example-var-shapes    (frequencies ex-vars)
     :note-var-shapes       (frequencies note-vars)
     :see-also-from-shapes  (frequencies sa-from)
     :see-also-to-shapes    (frequencies sa-to)})
  ;;=> {:example-var-shapes   {#{:ns :name :library-url} 2671}
  ;;    :note-var-shapes      {#{:ns :name :library-url} 497}
  ;;    :see-also-from-shapes {#{:ns :name :library-url} 2494}
  ;;    :see-also-to-shapes   {#{:ns :name :library-url} 2494}}
  ;; 100% consistent. All 8,156 embedded var refs have exactly #{:ns :name :library-url}.

  ;; Q: Are embedded :author sub-docs the same shape across all collections?
  (let [ex-authors   (->> (mon/fetch :examples :only [:author])
                          (map #(set (keys (:author %)))))
        note-authors (->> (mon/fetch :notes :only [:author])
                          (map #(set (keys (:author %)))))
        sa-authors   (->> (mon/fetch :see-alsos :only [:author])
                          (map #(set (keys (:author %)))))]
    {:example-author-shapes  (frequencies ex-authors)
     :note-author-shapes     (frequencies note-authors)
     :see-also-author-shapes (frequencies sa-authors)})
  ;;=> {:example-author-shapes  {#{:account-source :avatar-url :login} 2671}
  ;;    :note-author-shapes     {#{:account-source :avatar-url :login} 497}
  ;;    :see-also-author-shapes {#{:account-source :avatar-url :login} 2494}}
  ;; 100% consistent. All 5,662 embedded author refs have #{:account-source :avatar-url :login}.

  ;; Q: Is ExampleHistory :editor an embedded user doc or a string?
  (let [editors (->> (mon/fetch :example-histories :only [:editor])
                     (map :editor))]
    {:type-sample (type (first editors))
     :shapes      (frequencies (map #(cond
                                       (map? %) (set (keys %))
                                       (string? %) :string
                                       :else (type %))
                                    editors))})
  ;;=> {:type-sample clojure.lang.PersistentArrayMap
  ;;    :shapes {#{:account-source :avatar-url :login} 3358}}
  ;; Embedded user doc, same shape. 100% consistent across all 3,358 records.

  ;; Q: What about :editors (plural) entries on Examples?
  (->> (mon/fetch :examples :only [:editors])
       (filter :editors)
       (mapcat :editors)
       (map #(set (keys %)))
       frequencies)
  ;;=> {#{:account-source :avatar-url :login} 2596}
  ;; Same shape. All 2,596 editor entries in :editors lists are identical.

  ;; === Embedded Sub-Document Summary ===
  ;; Two embedded doc types exist in the entire database:
  ;;   EmbeddedVar:  {:ns :name :library-url} — denormalized var identity
  ;;   EmbeddedUser: {:account-source :avatar-url :login} — denormalized user identity
  ;; Both are 100% uniform — no sparsity, no extra keys, no missing keys.
  ;; EmbeddedUser is User minus :_id.
  nil)
