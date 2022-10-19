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
 """
   Tentei organizar o código em arquivos com cada especificidade.
   
   𓂅⊹⋆ core.clj
      é o main da aplicação, o app começa daqui
   
   𓂅⊹⋆ server.clj
      é onde se encontra a variável/símbolo que representa do servidor,
      e onde estão os métodos pra iniciar, parar e resetá-lo

   𓂅⊹⋆ db.clj
      como esse exercício não tem conexão com banco de dados, nesse arquivo
      se encontram as estrutuas de dados pra testar a API   
   
   𓂅⊹⋆ error.clj
      os métodos pra definir e printar erros estão nesse arquivo

   𓂅⊹⋆ routes.clj
      é onde estão as rotas/endpoints da API, cada uma linkando aos métodos
      onde estão as regras de negócio   
   
   𓂅⊹⋆ logic.clj
      aqui é onde se encontram os métodos com as regras de negócio implementadas  
""") 
  
(comment
  (server/start) 
  (server/stop-server) 
  (server/reset-server) 
  
  (test-request server/server :get "/hello")
  (test-request server/server :get "/hellov2")
  (test-request server/server :get "/hello/eric")
  (test-request server/server :get "/echo")
  (test-request server/server :post "/body-params")
  (test-request server/server :get "/contas")
  (test-request server/server :get "/pega-tudo/por/exemplo")
  (test-request server/server :get "/pega-tudo/por/exemplo?foo=bar&foo=foobar")
  (test-request server/server :get "/constraints/1")
  (test-request server/server :get "/constraints/bla")

  (test-request server/server :get "/saldo/1")
  (test-request server/server :get "/saldo/2")
  (test-request server/server :get "/saldo/3")
  (test-request server/server :get "/saldo/4")
  (test-request server/server :get "/saldo/5")

  (test-post server/server :post "/deposito/1" "6")
  (test-post server/server :post "/deposito/1" "100")
  (test-post server/server :post "/deposito/2" "2")
  (test-post server/server :post "/deposito/3" "300")
  (test-post server/server :post "/deposito/4" "100")

  (test-post server/server :post "/deposito/1" "-10")
    
  (test-post server/server :post "/saque/1" "-10")
  (test-post server/server :post "/saque/1" "10")
  (test-post server/server :post "/saque/3" "1")
  (test-post server/server :post "/saque/4" "100")
  (test-post server/server :post "/saque/1" "0")
  )
   
