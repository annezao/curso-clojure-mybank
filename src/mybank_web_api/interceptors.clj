(ns mybank-web-api.interceptors
  (:use [clojure pprint])
  (:require [io.pedestal.interceptor :as i]
            [clojure.data.json :as json]
            [mybank-web-api.logic :as logic]
            [mybank-web-api.response :as response]))

(def ok       (partial response/response 200))
(def created  (partial response/response 201))
(def accepted (partial response/response 202))
(def error    (partial response/response 500))

;; 1. Debugging Interceptors
(def echo
  {:name ::echo
   :enter #(assoc % :response (ok (:request %)))})

(defn print-n-continue
  "Logs the context and return the same."
  [context]
  (pprint "LOG: " context)
  context)

(def print-n-continue-int
  (i/interceptor {:name :print-n-continue
                  :enter print-n-continue
                  :leave print-n-continue}))

(defn hello
  "Função de roteamento que retorna uma mensagem HTTP de sucesso com `Hello!` no corpo."
  [_]
  (ok "Hello!"))

(def hello-interceptor
  (i/interceptor {:name ::hello
                  :enter (fn [ctx] (->> ctx
                                        hello
                                        (assoc ctx :response)))}))

;; business interceptors
(defn carrega-contas
  "Função de roteamento que carrega as contas ao contexto."
  [context database]
  (pprint "Carregando contas ao contexto!!")
  (assoc context :contas (:contas database)))

(defn carrega-contas-interceptor
  [database]
  (i/interceptor {:name  :contas-interceptor
                  :enter (fn [context] (carrega-contas context database))}))

(defn lista-contas
  "Recupera contas (`:contas`) e associa a um :response"
  [context]
  (as-> (:contas context) contas
    (response/response (if (nil? @contas) 500 200) @contas)
    (assoc context :response contas)))

(def lista-contas-interceptor
  (i/interceptor {:name  :contas-interceptor
                  :enter lista-contas}))

(defn get-saldo
  "Recupera saldo das contas disponíveis em :contas de `request`.
  Retorna uma response com :status :headers e :body."
  [contexto]
  (let [contas (:contas contexto)
        id-conta (-> contexto :request :path-params :id keyword)]
    (merge contexto {:response
                     (logic/get-saldo contas id-conta)})))

(def get-saldo-interceptor
  (i/interceptor {:name  :get-saldo
                  :enter get-saldo}))

(defn make-deposit 
  [contexto]
  (let [contas (:contas contexto)
        id-conta (-> contexto :request :path-params :id keyword)
        valor-deposito (-> contexto :request :body slurp parse-double)]
    (merge contexto {:response
                     (logic/make-deposit contas id-conta valor-deposito)})))

(def make-deposit-interceptor
  (i/interceptor {:name :make-deposit
                  :enter make-deposit}))

(defn make-withdraw
  [contexto]
  (let [contas (:contas contexto)
        id-conta (-> contexto :request :path-params :id keyword)
        valor-withdraw (-> contexto :request :body slurp parse-double)]
    (merge contexto {:response
                     (logic/make-withdraw contas id-conta valor-withdraw)})))

(def make-withdraw-interceptor
  (i/interceptor {:name :make-withdraw
                  :enter make-withdraw}))

;; 3. transorma resposta para o tipo pedido
(defn accepted-type
  "Retorna accept-type da requisição."
  [context]
  (get-in context [:request :accept :field] "text/plain"))

(defn transform-content
  "Transforma conteúdo de `body` para o tipo `content-type`."
  [body content-type]
  (case content-type
    "text/html"        body
    "text/plain"       body
    "application/edn"  (pr-str body)
    "application/json" (json/write-str body)))

(defn coerce-to
  "Atualiza `body` para que esteja no formato adequado ao `Content-Type`."
  [response content-type]
  (-> response
      (update :body transform-content content-type)
      (assoc-in [:headers "Content-Type"] content-type)))

(def coerce-body
  "Interceptor p/ forçar `body` para o tipo escolhido em `Content-Type`."
  (i/interceptor {:name ::coerce-body
                  :leave
                  (fn [context]
                    (pprint "Forçando body a se adaptar ao Content-Type..." context)
                    (if (get-in
                         context
                         [:response :headers "Content-Type"])
                      context
                      (update-in context [:response]
                       coerce-to (accepted-type context))))}))

