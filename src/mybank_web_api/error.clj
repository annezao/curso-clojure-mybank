(ns mybank-web-api.error
  (:use [clojure pprint])
  (:require [mybank-web-api.response :as response])
  (:gen-class))

(def invalid-account
  (response/error "Conta inválida!"))

(def insufficient-funds
  (response/bad-request "Saldo insuficiente!"))

(def invalid-amount
  (response/bad-request "O valor da operação deve ser maior que zero."))

(def interval-server-error
  (response/error "Ocorreu um erro inesperado."))

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