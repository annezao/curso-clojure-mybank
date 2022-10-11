  (ns mybank-web-api.logic
    (:use [clojure pprint])
    (:require [mybank-web-api.db :as db]
              [mybank-web-api.error :as error])
    (:gen-class))

(defn get-saldo [request]
  (try

    (let [id-conta (-> request :path-params :id keyword)]
      (if (id-conta @db/contas)
        {:status 200 :body {:saldo (id-conta @db/contas)}}
        (throw (Throwable. "Conta inválida!"))))
    (catch Throwable e
      (error/print-error e 500 (Throwable->map e)))))

(defn make-deposit [request]
  (try

    (let [id-conta (-> request :path-params :id keyword)
          valor-deposito (-> request :body slurp parse-double)
          _ (swap! db/contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
      {:status 200 :body {:id-conta id-conta
                          :novo-saldo (id-conta @db/contas)}})
    (catch Throwable e
      (error/print-error e 500 "Conta inválida!"))))


(defn make-withdraw [request]
  (try
    (let [id-conta (-> request :path-params :id keyword)
          valor-saque (-> request :body slurp parse-double)
          _ (swap! db/contas (fn [m] (update-in m [id-conta :saldo] #(- % valor-saque))))]
      {:status 200 :body {:id-conta id-conta
                          :novo-saldo (id-conta @db/contas)}})
    (catch Throwable e
      (error/print-error e 500 "Conta inválida!"))))
