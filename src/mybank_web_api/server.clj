(ns mybank-web-api.server
  (:use [clojure pprint])
  (:require [io.pedestal.http :as http] 
            [mybank-web-api.routes :as routes]
            [mybank-web-api.error :as error])
  (:gen-class))
  
(defonce server (atom nil))

(defn create-server []
    (http/create-server
     {::http/routes routes/routes
      ::http/type   :jetty
      ::http/port   8890
      ::http/join?  false}))

(defn stop-server []
  (try
    (pprint "Trying to stop server...")
    (http/stop @server)
    (pprint "Server stopped.")
    (catch Throwable e
      (error/fire-in-the-hole e))))

(defn start []
  (try
    (pprint "Trying to starting server...")
    (reset! server (http/start (create-server)))
    (pprint "Server on.")
    (catch Throwable e
      (error/fire-in-the-hole e)
      (stop-server))))

(defn reset-server []
  (stop-server)
  (start))