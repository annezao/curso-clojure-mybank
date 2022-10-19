(ns mybank-web-api.response
  (:use [clojure pprint])
  (:gen-class))

(defn response
  "Create a map representing the response of a HTTP Request, using `status`, `body` and the rest as headers."
  [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       
  (partial response 200))
(def created  
  (partial response 201))
(def accepted 
  (partial response 202))
(def bad-request 
  (partial response 400))
(def error    
  (partial response 500))