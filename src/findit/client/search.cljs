(ns findit.client.search
  (:require [clojure.string :as cstr]
            [shoreleave.services.geo :as geo]
            [shoreleave.remote :as remote])
  (:use [jayq.core :only [$ inner]]))

(defn query->map [search-query]
  (let [[topic location] (map cstr/trim (cstr/split search-query #" near "))
        zip (when (re-find #"\d" location) location)] ;if we see an digit, we assume it's some kind of zip
    {:raw-query search-query :topic topic :location location :zip zip}))

(defn clean-location [search-map search-map-fn]
  (if-let [location (:location search-map)]
    (geo/normalize-location location #(search-map-fn (merge search-map %)))
    (search-map-fn search-map)))

(defn solrized-map [search-map]
  (assoc search-map
         :solr-query (:topic search-map)
         :solr-fq (if (:latlng-str search-map)
                      ;TODO: the distance below should use a hand applied haversine filter-query, to get all tutors willing to travel:
                      ;  - http://wiki.apache.org/solr/FunctionQuery#hsin.2C_ghhsin_-_Haversine_Formula
                      ; For now, just search within five miles
                    (str "{!geofilt pt=" (:latlng-str search-map) " sfield=item_location d=8}") ;Five miles is 8 km
                    "")))

(defn process-solr [data-map query-map render]
  (let [{:keys [docs numFound start]} (:response data-map)]
    (render {:results docs :num-found numFound :query query-map :highlight (:highlighting data-map)})))

;; Use this to test the rendering of results
(defn fake-solr [q r]
  (r [{:id "555" :itemname "Book" :description "you read it"}
      {:id "666" :itemname "TV" :description "It's big"}]))

(defn solr [solr-query-map render]
  (remote/jsonp "http://127.0.0.1:8983/solr/select"
                :content {:wt "json" :qt "item" :fl "*,score"
                          :q (:solr-query solr-query-map)
                          :fq (:solr-fq solr-query-map)}
                :on-success #(process-solr % solr-query-map render)
                :on-timeout #(-> ($ ".item-result") (inner (str "<h4>There was an error fetching the results - SOLR most likely isn't on.</h4>")))
                :callback-name "json.wrf"))

;; Top-level call
(defn search
  ([search-query render]
    (search search-query render solr))
  ([search-query render search-fn]
    (let [search-map (query->map search-query)]
      (clean-location search-map #(-> % solrized-map (search-fn render))))))

;;(solr "math" js/console.dir)

