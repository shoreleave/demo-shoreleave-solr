(ns findit.views.common
  (:require [clojure.core.memoize :as memo]
            [clojure.string :as cstr])
  (:use [findit.config :only (config)]))

;; ###Replacements
;; This will peform regex replacements on substrigns found in our HTML
(def replacements
  [[#"../uberstrap" ""]
   [#"../public" ""]])

(defn read-page [page-name]
  (let [page (slurp (str (config :template-dir) page-name))]
    (reduce (fn [page [rep sub]] (cstr/replace page rep sub)) page replacements)))

(def page (memo/memo-ttl read-page (config :page-cache-expire-ms)))

