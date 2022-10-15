(ns mybank-web-api.error
  (:use [clojure pprint])
  (:gen-class))

(def invalid-account
  {:status 500
   :body "Conta inválida!"})

(def insufficient-funds
  {:status 400
   :body "Saldo insuficiente!"})

(def invalid-amount
  {:status 400
   :body "O valor da operação deve ser maior que zero."})

(def interval-server-error
  {:status 500
   :body "Ocorreu um erro inesperado."})

; Como não sei criar erros customizáveis java-clojure, encontrei o meio termo usando ex-info
(defn custom-ex-info
  [custom-error]
  (ex-info (:body custom-error)
           {:type custom-error}))

(defn fire-in-the-hole 
  ([error] 
   (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map error) ".")))
   (interval-server-error))
  ([error response-error] 
   (pprint (str "ヽ( `д´*)ノ Error: " (:cause (Throwable->map error) ".")))
   response-error))