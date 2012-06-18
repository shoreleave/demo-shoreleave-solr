(ns findit.client.controllers
  (:require [findit.client.view.render :as render]
            [findit.client.search :as search]
            [shoreleave.common :as common])
  (:use [jayq.core :only [$ attr]])
  (:require-macros [shoreleave.remotes.macros :as srm]))

(defn process-search 
  ([query]
   (process-search query true))
  ([query set-location?]
   (do
     (when set-location?
       (common/set-window-hash-args {:q query}))
     (-> ($ "#item-search-box") (attr "value" query))
     (render/set-search-header "")
     (render/set-item-results (str "<h4>Searching for " query "...</h4>"))
     (search/search query render/render-search-results))))

;; This is used to confirm the server is up from the
;; browser js console or browser-repl
;;
;; It also illustrates the difference between the "global" remote and
;; a namespaced remote
(defn ^:export ping-server []
  (srm/rpc (ping) [pong-response]
    (js/alert pong-response)))
(defn ^:export echo-api [client-echo-str]
  (srm/rpc (api/echo client-echo-str) [echo-response]
    (js/alert echo-response)))

  
