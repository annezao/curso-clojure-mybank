(ns mybank-web-api.server
  (:use [clojure pprint])
  (:require [io.pedestal.http :as http] 
            [mybank-web-api.routes :as routes]
            [mybank-web-api.error :as error]
            [mybank-web-api.interceptors :as app-interceptors]
            
            [io.pedestal.http.content-negotiation :as conneg])
  (:gen-class))
  
(defonce server (atom nil))

;; curl -i -H "Accept: application/xml" http://localhost:9999/contas
(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])
(def content-neg-intc (conneg/negotiate-content supported-types))

(def service-map-simple {
                         ::http/routes routes/routes
                         ::http/type   :jetty
                         ::http/port   8890
                         ::http/join?  false})

(def service-map (-> service-map-simple
                     (http/default-interceptors)
                     (update ::http/interceptors conj
                             ;; 3. checa content header
                             content-neg-intc
                             ;; 2. carrega contas ao contexto
                             app-interceptors/carrega-contas-interceptor)))

(defn create-server
  "Creates a HTTP Server using `service-map` definitions."
  []
  (http/create-server
   service-map))

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