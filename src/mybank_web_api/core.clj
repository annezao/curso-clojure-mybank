  (ns mybank-web-api.core
    (:use [clojure pprint])
    (:require [io.pedestal.http :as http]
              [io.pedestal.http.route :as route]
              [io.pedestal.test :as test-http])
    (:gen-class)) 

  (defonce contas (atom {:1 {:saldo 100}
                         :2 {:saldo 200}
                         :3 {:saldo 300}}))

  (defn get-saldo [request]
    (try

      (let [id-conta (-> request :path-params :id keyword)]
        (if (id-conta @contas)
          {:status 200 :body {:saldo (id-conta @contas)}}
          (throw (Throwable. "Conta inválida!"))))
      (catch Throwable e
        (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map e) ".")))
        {:status 500 :body (:cause (Throwable->map e)) })))

  (defn make-deposit [request]
    (try
      
      (let [id-conta (-> request :path-params :id keyword)
            valor-deposito (-> request :body slurp parse-double)
            _ (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
        {:status 200 :body {:id-conta id-conta
                            :novo-saldo (id-conta @contas)}})
     (catch Throwable e 
        (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map e) ".")))
       {:status 500 :body "Conta inválida!"})))
  

  (defn make-withdraw [request]
    (try

      (let [id-conta (-> request :path-params :id keyword)
            valor-saque (-> request :body slurp parse-double)
            _ (swap! contas (fn [m] (update-in m [id-conta :saldo] #(- % valor-saque))))]
        {:status 200 :body {:id-conta id-conta
                            :novo-saldo (id-conta @contas)}})
      (catch Throwable e
        (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map e) ".")))
        {:status 500 :body "Conta inválida!"})))

  (def routes
    (route/expand-routes
      #{["/saldo/:id" :get get-saldo :route-name :saldo]
        ["/deposito/:id" :post make-deposit :route-name :deposito]
        ["/saque/:id" :post make-withdraw :route-name :saque]}))

  (defn create-server []
    (http/create-server
      {::http/routes routes
       ::http/type   :jetty
       ::http/port   8890
       ::http/join?  false}))

  (defonce server (atom nil))

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

  (defn test-request [server verb url]
    (test-http/response-for (::http/service-fn @server) verb url))
  (defn test-post [server verb url body]
    (test-http/response-for (::http/service-fn @server) verb url :body body))

  """
      ;; Exercicios Módulo 03 - Aula 01

      1 - Tratamento de conta inválida/inexistente no deposito. Retornar o status http de erro e mensagem no body.
      2 - Implementar funcionalidade saque
      3 - Criar reset do servidor (tenta stop e tenta start) e demonstrar no mesmo repl antes e depois do tratamento de erro no ex. 1
  """
  (defn stop-server []
    (pprint "Trying to stop server...")
    (http/stop @server)
    (pprint "Server stopped."))
  
  (defn reset-server []
    (stop-server)
    (start))
  
  (comment
    (start) 
    (stop-server) 
    (reset-server)

    (test-request server :get "/saldo/1")
    (test-request server :get "/saldo/2")
    (test-request server :get "/saldo/3")
    (test-request server :get "/saldo/4")

    (test-post server :post "/deposito/2" "863.99")
    (test-post server :post "/deposito/3" "100")
    
    (test-post server :post "/saque/1" "100")
   )
    
    
