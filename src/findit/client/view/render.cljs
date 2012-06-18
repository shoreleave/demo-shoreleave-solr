(ns findit.client.view.render
  (:require [enfocus.core :as ef]
            [clojure.string :as cstr])
  (:use [jayq.core :only [$ inner]])
  (:require-macros [enfocus.macros :as em]))

(def partial-path "partials/")
(def partial-url-for #(str partial-path %))

(em/deftemplate tutor-result
  (partial-url-for "search/item-result.html.partial") [tutor-data-map]
    ["#item-id12345"] (em/set-attr :value (:email tutor-data-map)
                                    :name (str "send-" (:id tutor-data-map) "-email")
                                    :id    (str "tutor-id" (:id tutor-data-map)))
    ["#item-link12345"] (em/set-attr :href (str "http://www.tutorspree.com/tutor/" (:id tutor-data-map)))
    ["h4"] (em/content (str (:firstname tutor-data-map) " "
                                    (:lastname tutor-data-map) " :: "
                                    (:id tutor-data-map)))
    ["img"] (em/set-attr :src (str "http://www.tutorspree.com/" (cstr/replace
                                                                  (if (empty? (:picture tutor-data-map)) "sm-tutor-pic/images.jpg" (:picture tutor-data-map))
                                                                  #"uploads" "sm-tutor-pic")))
    ["#item-details12345"] (em/set-attr :title (str (:firstname tutor-data-map) " "
                                                      (:lastname tutor-data-map) " :: "
                                                      (:id tutor-data-map))
                                          :id (str "tutor-details" (:id tutor-data-map))
                                          :data-content (reduce (fn [old [k v]] (str old " <br/> <strong>" k "</strong> "
                                                                                     (cond
                                                                                       (= "" v) "No" ; TODO: The use of `empty?` here causes the render to fail
                                                                                       (vector? v) (cstr/join ", " v)
                                                                                       :else v)))
                                                                ""
                                                                (select-keys tutor-data-map
                                                                             [:ranking :created :certified_tutor :teacher_exp :subject :subject_name])))
    [".bioinfo"] (em/content (reduce (fn [old [sub regex]] (cstr/replace old regex sub)) (:personal_statement tutor-data-map)
                                     {"'" #"\\'", "\"" #"\\" " " #"<br>|<b>|</b>|<br/>"}))
    [".item-details"] (em/html-content (apply str
                                              (map (fn [[attr v]] (str "<li class=\"span3\"><strong>"attr"</strong> " (if (vector? v) (cstr/join ", " v) v)))
                                                   (select-keys (assoc tutor-data-map :related_subjects (get-in tutor-data-map [:highlight :subject_name]))
                                                                [:email :phone :price :city :state :zip :related_subjects :college])))))

(em/defaction produce-search-results [results-map]
              ["#search-header h3"] (em/do->
                                      (em/html-content (str ;"<h5>query: " (get-in results-map [:query :raw-query]) "</h5> "
                                                            "Items found: " (:num-found results-map)
                                                       (when-let [loc (get-in results-map [:query :formatted])]
                                                         (str " - five miles around " loc))
                                                       (when-let [zip (get-in results-map [:query :zip])]
                                                         (str " <h5><a href=\"http://zipskinny.com/index.php?zip=" zip "\">more area info</a></h5>")))))
              ["#item-results"] (em/html-content "<div class=\"row item-result\"></div>")
              [".item-result"]  (em/clone-for [tutor-data-map (:results-highlight results-map)]
                                               (em/content (tutor-result tutor-data-map))))

(defn set-search-header [content-str]
  (-> ($ "#search-header h3") (inner content-str)))

(defn set-item-results [content-str]
  (-> ($ "#item-results") (inner (str "<div class=\"row item-result\">" content-str  "</div>"))))

(defn render-search-results [results-map]
  ;; The search results and the highlights are separate; join the highlight into into the matching tutor-data-map
  (let [res-map (assoc results-map :results-highlight (for [tutor-data-map (:results results-map)
                                                :let [highlight-map (assoc tutor-data-map :highlight
                                                                           (get-in results-map
                                                                                   [:highlight (keyword (tutor-data-map :id))]))]]
                                            highlight-map))]
    (if-not (-> (:results res-map) count zero?)
      (produce-search-results res-map)
      (set-item-results (str "<h4>No results for " (get-in res-map [:query :topic]) "</h4>")))
    (-> ($ "[rel*='popover']") .popover)))

