(ns mybank-web-api.server
  (:use [clojure pprint])
  (:require [io.pedestal.http :as http] 
            [mybank-web-api.routes :as routes])
  (:gen-class))
  
(defonce server (atom nil))

(defn create-server []
    (http/create-server
     {::http/routes routes/routes
      ::http/type   :jetty
      ::http/port   8890
      ::http/join?  false}))

(defn start []
  (try
    (pprint "Trying to starting server...")
    (reset! server (http/start (create-server)))
    (pprint "Server on.")
    (catch Throwable e
      (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map e) ".")))
      (pprint "Stopping server...")
      (http/stop @server)
      (pprint "Server stopped."))))

(defn stop-server []
  (pprint "Trying to stop server...")
  (http/stop @server)
  (pprint "Server stopped."))

(defn reset-server []
  (stop-server)
  (start))