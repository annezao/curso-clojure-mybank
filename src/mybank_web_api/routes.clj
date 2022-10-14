(ns mybank-web-api.routes
  (:require [io.pedestal.http.route :as route]
            [mybank-web-api.logic :as logic])
  (:gen-class))
  
  (defn get-saldo [request]
   (logic/get-saldo request))

(defn make-deposit [request]
  (logic/make-deposit request))

(defn make-withdraw [request]
  (logic/make-withdraw request))

(def routes
  (route/expand-routes
   #{["/saldo/:id" :get get-saldo :route-name :saldo]
     ["/deposito/:id" :post make-deposit :route-name :deposito]
     ["/saque/:id" :post make-withdraw :route-name :saque]}))