(ns findit.client.main
  (:require [findit.client.controllers :as controller]
            [findit.client.view.listeners :as listeners]
            [shoreleave.common :as common]
            [shoreleave.browser.history :as history]
            [shoreleave.pubsubs.simple :as pbus]
            [shoreleave.pubsubs.protocols :as pubsub])
  (:require-macros  [shoreleave.client.remotes.macros :as srm]))

;; Initial setup
;; -------------
;; We're going to have to look at the hash-args and the query-string args...
(def query-args (common/query-args-map))
(def hash-args (common/hash-args-map))

(defn ^:export apiping []
  (srm/rpc (api/ping) [pong-response]
    (js/alert pong-response)))

;; If someone came to the search page with a search query already
;; in the URL, do the search now
(when-let [query (:q hash-args)]
  (set! (.-onload js/window) #(controller/process-search query)))

;; Integrate history/back-button to the search
(history/navigate-callback #(controller/process-search (subs (:token %) 4) false))

;;Setup the PubSub bindings
(def bus (pbus/bus))
(defn ^:export test-fn [] "This is from the test-fn")
(def ps-test-fn (pubsub/publishize test-fn bus))
(pubsub/subscribe bus ps-test-fn #(js/console.log (str "This is from the bus --FN--: " %)))


; this has a side-effect -> it publishes its result to the bus
(ps-test-fn)

; Build the "identity" worker
(defn echo-workerfn [data] data)
(def nw (swk/worker echo-workerfn))

; This works, but the pubsub is uniform across everything
;(add-watch nw :watcher #(js/console.log (last %4)))

(pubsub/publishize nw bus)
(pubsub/subscribe bus nw #(js/console.log (str "This is from the bus --worker--: " %)))
(nw "HELLO")
(nw "THREADS")

(def test-atom (atom {}))
(pubsub/publishize test-atom bus)
(pubsub/subscribe bus test-atom #(js/console.log (str "This is ALSO from the bus --atom--: " %)))
; This is next
;(pubsub/subscribe-> bus test-atom ps-test-fn #(js/console.log "this is the end"))

(swap! test-atom assoc :new-key 1)
(swap! test-atom assoc :another-key 2)

; You can do this, but it doesn't block and it's a terrible idea
;(js/console.log (apply str @nw))

;; ### Browser REPL
;; If you add `repl` as a query-string arg,
;; you can remotely interact with the site from the local REPL
;; Visit: `http://127.0.0.1:8080/search?repl=yes#q=sat`
(common/toggle-brepl query-args :repl)

