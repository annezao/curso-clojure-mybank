(ns mybank-web-api.routes
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]

            [mybank-web-api.interceptors :as app-interceptors])
  (:gen-class))

(def routes
  (route/expand-routes
   #{;; 1. using function
     ["/hello" :get app-interceptors/hello :route-name :hello]
     ;; 1. using interceptor
     ["/hellov2" :get app-interceptors/hello-interceptor :route-name :hellov2]
     ;; 1. using anom func
     ["/echo" :get #(hash-map :body % :status 200) :route-name :echo]

     ;; ;; 2. using parameters
     ;; TODO: use interceptors
     ["/hello/:name" :get (fn [context]
                            {:status 200
                             :body (format "Hello, %s!"
                                           (get-in context [:path-params :name]))})
      :route-name :hello-user]
     ;; 2. Pega tudo
     ["/pega-tudo/*subpage" :get app-interceptors/echo :route-name :pega-tudo]
     ;; 2. body/params interceptor
     ["/body-params" :post [(body-params/body-params)
                            app-interceptors/print-n-continue
                            app-interceptors/echo] :route-name :hello-body]
     ;; 2. Using constraints
     ["/constraints/:user-id" :get [(body-params/body-params)
                                    app-interceptors/print-n-continue
                                    app-interceptors/echo]
      :constraints {:user-id #"^[a-zA-Z0-9]*"}]
     ;; ^[a-zA-Z0-9]*

     ;; 3. business logic (coerce-body)
     ["/contas" :get [app-interceptors/print-n-continue
                      ;; 3. add interceptador p/ alterar reposta final
                      app-interceptors/coerce-body
                      app-interceptors/lista-contas-interceptor] :route-name :contas]
     ["/saldo/:id" :get [app-interceptors/get-saldo-interceptor] :route-name :saldo]
     ["/deposito/:id" :post app-interceptors/make-deposit-interceptor :route-name :deposito]
     ["/saque/:id" :post app-interceptors/make-withdraw-interceptor :route-name :saque]}))