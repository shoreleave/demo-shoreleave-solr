(ns findit.views.welcome
  (:require [noir.response :as response] ;json, jsonp, xml
            [findit.views.common :as common])
  (:use [noir.core :only [defpage]]
        [noir.shoreleave.rpc :only [defremote]]))

;; ### Pages
(defpage [:get "/"] []
  (common/page "home.html"))

(defpage [:get "/search"] []
  (common/page "search.html"))

;; ### Remotes
(defremote ping []
  (do
    (println "Pinged by client!")
    "PONG - from the server"))

; You might define a way for your client to send emails...
;(defremote email [message-details]
;  (mailgun/send-email message-details))

