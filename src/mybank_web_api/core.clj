  (ns mybank-web-api.core
    (:use [clojure pprint])
    (:require [io.pedestal.http :as http]
              [io.pedestal.test :as test-http]
              [mybank-web-api.server :as server])
    (:gen-class)) 

  (defn test-request
    [server verb url]
    (test-http/response-for (::http/service-fn @server) verb url)) 
  (defn test-post
    [server verb url body]
    (test-http/response-for (::http/service-fn @server) verb url :body body))
  
  (comment
    (server/start) 
    (server/stop-server) 
    (server/reset-server)

    (test-request server/server :get "/saldo/1")
    (test-request server/server :get "/saldo/2")
    (test-request server/server :get "/saldo/3")
    (test-request server/server :get "/saldo/4")

    (test-post server/server :post "/deposito/1" "2")
    (test-post server/server :post "/deposito/2" "863.99")
    (test-post server/server :post "/deposito/3" "100")
    
    (test-post server/server :post "/saque/1" "100")
   )
    
