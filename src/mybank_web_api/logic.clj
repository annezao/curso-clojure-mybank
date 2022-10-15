  (ns mybank-web-api.logic
    (:use [clojure pprint])
    (:require [mybank-web-api.db :as db]
              [mybank-web-api.error :as error])
    (:gen-class))

(defn account-exists?
  [id-conta]
    (when (not (id-conta @db/contas))
      (throw (error/custom-ex-info error/invalid-account))))

(defn success-response
  [id-conta]
  {:status 200 :body {:id-conta id-conta
                      :novo-saldo (id-conta @db/contas)}})

(defn amount>saque?
  [conta amount]
  ;; estoura exceção se o saldo após o saque for menor que 0
  (when (< (- (:saldo conta) amount) 0)
    (throw (error/custom-ex-info error/insufficient-funds))))

(defn amount>=0?
  [amount]
    (when (<= amount 0)
      (throw (error/custom-ex-info error/invalid-amount))))

(defn get-saldo [request]
  (try
    (let [id-conta (-> request :path-params :id keyword)
          _ (account-exists? id-conta)]
      
      (success-response id-conta))

    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))

(defn make-deposit [request]
  (try

    (let [id-conta (-> request :path-params :id keyword)
          valor-deposito (-> request :body slurp parse-double)
          _ (account-exists? id-conta)
          _ (amount>=0? valor-deposito)
          _ (swap! db/contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
      
      (success-response id-conta))
    
    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))


(defn make-withdraw [request]
  (try
    (let [id-conta (-> request :path-params :id keyword)
          valor-saque (-> request :body slurp parse-double)
          _ (account-exists? id-conta)
          _ (amount>=0? valor-saque)
          _ (amount>saque? (id-conta @db/contas) valor-saque)
          _ (swap! db/contas (fn [m] (update-in m [id-conta :saldo] #(- % valor-saque))))]
      
      (success-response id-conta))
    
    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))
