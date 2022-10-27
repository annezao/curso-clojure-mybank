 (ns mybank-web-api.core
   (:use [clojure pprint])
   (:require [mybank-web-api.server :as server]

             [mybank-web-api.database :as db]
             [com.stuartsierra.component :as component])
   (:gen-class)) 
 
(comment
 """
   Tentei organizar o código em arquivos com cada especificidade.
   
   𓂅⊹⋆ core.clj
      é o main da aplicação, o app começa daqui
   
   𓂅⊹⋆ server.clj
      é onde se encontra a variável/símbolo que representa do servidor,
      e onde estão os métodos pra iniciar, parar e resetá-lo

   𓂅⊹⋆ database.clj
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

(def new-sys
  (component/system-map
   :database (db/new-database)
   :web-server (component/using
                (server/new-servidor)
                [:database])))

(def sys (atom nil))
(defn start-server [] (reset! sys (component/start new-sys)))
(defn stop-server [] (component/stop new-sys))
  
(comment
  (require '[clj-http.client :as client])
  (client/post "http://localhost:8890/deposito/1" {:body "199.93"})
  (start-server)
  (:web-server @sys)

  (server/test-request :get "/hello")
  (server/test-request :get "/hellov2")
  (server/test-request :get "/hello/eric")
  (server/test-request :get "/echo")
  (server/test-request :post "/body-params")
  (server/test-request :get "/contas")
  (server/test-request :get "/pega-tudo/por/exemplo")
  (server/test-request :get "/pega-tudo/por/exemplo?foo=bar&foo=foobar")
  (server/test-request :get "/constraints/1")
  (server/test-request :get "/constraints/bla")

  (server/test-request :get "/saldo/1")
  (server/test-request :get "/saldo/2")
  (server/test-request :get "/saldo/3")
  (server/test-request :get "/saldo/4")
  (server/test-request :get "/saldo/5")

  (server/test-post :post "/deposito/1" "6")
  (server/test-post :post "/deposito/1" "100")
  (server/test-post :post "/deposito/2" "2")
  (server/test-post :post "/deposito/3" "300")
  (server/test-post :post "/deposito/4" "100")

  (server/test-post :post "/deposito/1" "-10")

  (server/test-post :post "/saque/1" "-10")
  (server/test-post :post "/saque/1" "10")
  (server/test-post :post "/saque/3" "1")
  (server/test-post :post "/saque/4" "100")
  (server/test-post :post "/saque/1" "0")

  (stop-server)
  ) 