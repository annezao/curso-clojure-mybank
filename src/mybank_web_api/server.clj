(ns mybank-web-api.server
  (:use [clojure pprint])
  (:require [io.pedestal.http :as http] 
            [mybank-web-api.routes :as routes]
            [mybank-web-api.error :as error]
            [mybank-web-api.interceptors :as app-interceptors]
            
            [io.pedestal.http.content-negotiation :as conneg]
            [io.pedestal.test :as test-http]
            [com.stuartsierra.component :as component])
  (:gen-class))
  
(defonce server (atom nil))

;; curl -i -H "Accept: application/xml" http://localhost:9999/contas
(def supported-types ["text/html" "application/edn" "application/json" "text/plain"])
(def content-neg-intc (conneg/negotiate-content supported-types))

(defn create-server
  "Creates a HTTP Server using `service-map` definitions."
  [service-map]
  (http/create-server
   service-map))

(defn stop-server []
  (try
    (pprint "Trying to stop server...")
    (http/stop @server)
    (pprint "Server stopped.")
    (catch Throwable e
      (error/fire-in-the-hole e))))

(defn start-server [service-map]
  (try
    (pprint "Trying to starting server...")
    (reset! server (http/start (create-server service-map)))
    (pprint "Server on.")
    (catch Throwable e
      (error/fire-in-the-hole e)
      (stop-server))))

(defn reset-server [service-map]
  (stop-server )
  (start-server service-map))

(defn test-request [verb url]
  (test-http/response-for (::http/service-fn @server) verb url))

(defn test-post
  [verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(defrecord Servidor [database]
  component/Lifecycle

  (start [this]
    (println "Start servidor")
    (let [service-map-base {::http/routes routes/routes
                            ::http/type   :jetty
                            ::http/port   8890
                            ::http/join?  false}
          service-map (-> service-map-base
                          (http/default-interceptors)
                          (update ::http/interceptors conj
                             ;; 3. checa content header
                                  content-neg-intc
                             ;; 2. carrega contas ao contexto
                                  (app-interceptors/carrega-contas-interceptor database)))]
      (start-server service-map)
      (assoc this :test-request test-request)))

  (stop [_]
    (stop-server)))

(defn new-servidor []
  (->Servidor {}))


