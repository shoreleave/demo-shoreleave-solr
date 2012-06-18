(ns findit.server
  "The webserver (complete with remote support)"
  (:require [noir.server :as server]
            [noir.shoreleave.rpc :as rpc]
            [ring.middleware.gzip]
            [ring.middleware.file-info]
            [ring.middleware.anti-forgery]
            ;; Sometimes remote-ns needs the symbol require
            [findit.api])
  (:use [findit.config :only (config)]
        [ring.middleware.session.cookie :only  (cookie-store)]))

;; The Web Server
;; ---------------------
;; Findit serves every view file found in `src/findit/views`
(server/load-views "src/findit/views/")

;; Remote Namespaces
;; -----------------
(rpc/activate-remotes!)
(rpc/remote-ns 'findit.api :as "api")

;; Middleware
;;-----------
(server/add-middleware ring.middleware.gzip/wrap-gzip)
(server/add-middleware ring.middleware.file-info/wrap-file-info)
(server/add-middleware ring.middleware.anti-forgery/wrap-anti-forgery)

;; Here we just define a conveinent way to start up a server in dev mode,
;; as well as a `main` method for execution view `lein run`

(defn run-server
  "Start a dev-mode web server"
  []
  (server/start (config :app-port) {:mode :dev
                                       :ns 'findit
                                       :session-store (cookie-store {:key (config :session-secret)})
                                       :session-cookie-attrs {:max-age (config :session-max-age-seconds)
                                                              :http-only true}}))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" (config :app-port)))]
    (server/start port {:mode mode
                        :ns 'findit
                        :session-store (cookie-store {:key (config :session-secret)})
                        :session-cookie-attrs {:max-age (config :session-max-age-seconds)
                                               :http-only true}})))

