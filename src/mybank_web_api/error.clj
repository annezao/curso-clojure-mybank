(ns mybank-web-api.error
  (:use [clojure pprint])
  (:gen-class))

(defn print-error
  [throwable-error, code, message]
  (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map throwable-error) ".")))
  {:status code :body message})