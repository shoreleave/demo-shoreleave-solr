(ns findit.config
  "The main application configuration file")

;; The configuration
;; -----------------
;; `config` is a top-level hash-map. It is a simple key-value config.
;;
;; All "magic numbers" are placed in this config
;;
;; ### Using the config
;; Getting items from the config is done as follows:
;;
;;      (config :my-config-item)
;;
;; Even though the config is a hash map (where normally `(:key my-map)`
;; is most often used, placing the config map first allows us to later
;; migrate config to function that resolves configuration requests.
(def config
  {:app-port 8889
   :template-dir "resources/full_pages/"
   :page-cache-expire-ms (* 10 1000)

   ;; Session and Cookies
   ;; -------------------
   :session-key "thisisabadkey"
   :session-max-age-seconds 1209600})

