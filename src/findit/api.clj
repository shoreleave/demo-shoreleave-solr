(ns findit.api)

(defn echo [echo-str]
  (do 
    (println "API echo from client")
    (str "You sent: " echo-str)))

