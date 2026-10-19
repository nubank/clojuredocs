(ns clojuredocs.github
  "Provides auth^2 from the v3 github api.

   This library assumes you're using a ring-compatible response format.

   The GitHub auth flow is relatively simple, and consists of three
   steps:

   1. Redirect users to github to request access permissions.
   2. Handle the redirect back from github and exchange the recieved
      temporary code for a long-lived access token by posting to the
      github api.
   3. Use the long-lived access token to query the user's protected
      information from the github api.

   See http://developer.github.com/v3/oauth#web-application-flow for
   more information."

  (:require [clj-http.client :as hc]
            [clojuredocs.util :as u]))

(def login-base-url "https://github.com/login/oauth")

(defn auth-redirect-url
  "Generates a github authorization URL."
  [{:keys [client-id redirect-uri scopes]}]
  (str login-base-url
       "/authorize?"
       "client_id=" (u/url-encode client-id) "&"
       "redirect_uri=" (u/url-encode redirect-uri) "&"
       "scope=" (->> scopes
                     (map name)
                     (interpose ",")
                     (apply str)
                     u/url-encode)))

(defn exchange-code [{:keys [client-id client-secret]} code]
  (when code
    (let [qp {:client_id client-id
              :client_secret client-secret
              :code code}
          res (hc/post (str login-base-url "/access_token")
                {:query-params qp
                 :headers {"Accept" "application/json"}})]
      (if (= 200 (:status res))
        (u/from-json (:body res))
        {:error "unknown"}))))

(defn user [token]
  (when token
    (let [res (hc/get "https://api.github.com/user"
                {:basic-auth ["token" token]})]
      (-> res :body u/from-json))))

(def ^:private repo-stats-ttl-ms (* 60 60 1000))

(defonce ^:private repo-stats-cache (atom {}))

(defn repo-stats
  "Returns {:stars n} for a public repo, e.g. \"nubank/clojuredocs\".
   Cached for an hour per repo; falls back to the last known value
   (or nil) if the GitHub API call fails."
  [repo]
  (let [{:keys [at stats]} (get @repo-stats-cache repo)]
    (if (and stats (< (- (System/currentTimeMillis) at) repo-stats-ttl-ms))
      stats
      (if-let [stats' (try
                         (let [res (hc/get (str "https://api.github.com/repos/" repo)
                                     {:headers {"Accept" "application/vnd.github+json"}})]
                           (when (= 200 (:status res))
                             {:stars (-> res :body u/from-json :stargazers_count)}))
                         (catch Exception _ nil))]
        (do (swap! repo-stats-cache assoc repo {:at (System/currentTimeMillis) :stats stats'})
            stats')
        stats))))
