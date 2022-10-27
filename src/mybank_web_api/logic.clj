  (ns mybank-web-api.logic
    (:use [clojure pprint])
    (:require [mybank-web-api.database :as db]
              [mybank-web-api.error :as error]
              [mybank-web-api.response :as response])
    (:gen-class))

(defn account-exists?
  [contas id-conta]
    (when (not (id-conta @contas))
      (throw (error/custom-ex-info error/invalid-account))))

(defn success-response
  [contas id-conta]
  (response/ok {:id-conta id-conta
       :novo-saldo (id-conta @contas)}))

(defn amount>withdraw?
  [conta amount]
  ;; estoura exceção se o saldo após o withdraw for menor que 0
  (when (< (- (:saldo conta) amount) 0)
    (throw (error/custom-ex-info error/insufficient-funds))))

(defn amount>=0?
  [amount]
    (when (<= amount 0)
      (throw (error/custom-ex-info error/invalid-amount))))

(defn get-saldo 
  [contas id-conta]
  (try
    (let [_ (account-exists? contas id-conta)]
      (success-response contas id-conta))

    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))

(defn make-deposit
  [contas id-conta valor-deposito]
  (try
    (let [_ (account-exists? contas id-conta)
          _ (amount>=0? valor-deposito)
          _ (swap! contas (fn [m] (update-in m [id-conta :saldo] #(+ % valor-deposito))))]
      
      (success-response contas id-conta))
    
    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))


(defn make-withdraw
  [contas id-conta valor-withdraw]
  (try
    (let [_ (account-exists? contas id-conta)
          _ (amount>=0? valor-withdraw)
          _ (amount>withdraw? (id-conta @contas) valor-withdraw)
          _ (swap! contas (fn [m] (update-in m [id-conta :saldo] #(- % valor-withdraw))))]
      
      (success-response contas id-conta))
    
    (catch clojure.lang.ExceptionInfo e
      (error/fire-in-the-hole e (-> e ex-data :type)))
    (catch Throwable e
      (error/fire-in-the-hole e))))
