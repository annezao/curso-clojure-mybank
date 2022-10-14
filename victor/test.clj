; Aula02 - Unit Tests
(comment
  (ns mybank-web-api.core-test
    (:require [clojure.test :refer :all]
              [mybank-web-api.core :refer :all]
              [io.pedestal.http :as http]
              [io.pedestal.http.route :as route]
              [io.pedestal.test :as test-http]
              [io.pedestal.interceptor :as i]
              [io.pedestal.interceptor.chain :as chain]
              [clojure.pprint :as pp]))

  (deftest requests
    (testing "Chamando request e testando o valor de body retornado"
      (let [_ (start)
            resp (test-request server :get "/saldo/1")
            _ (http/stop @server)]
        (is (= "{:saldo 100}" (:body resp))))))

  (deftest are-req
    (testing "Multiple get resp"
      (are [a b] (= (-> (test-request server :get a)
                        :body) b)
        "/saldo/1" "{:saldo 100}"
        "/saldo/2" "{:saldo 200}"
        "/saldo/3" "{:saldo 300}")))

  (def resp (test-request server :get "/saldo/1"))
  (:body resp)
  (run-tests)
  (macroexpand '(are [x y] (= x y)
                  2 (+ 1 1)
                  4 (* 2 2)))

  (macroexpand '(are [a b c] (= (+ a b) c)
                  1 2 3
                  6 6 12))

  (macroexpand '(are [a b c] (= (* a b) c)
                  1 2 2
                  6 6 36))

  (macroexpand '(are [a b] (= (-> (test-request server :get a)
                                  :body) c)
                  "/saldo/1" "{:saldo 100}"))

  (test-request server :get "/saldo/1")
  (run-tests)
  (start)
  (http/stop @server)

  ;(cljs.core.match)

  (test-request server :get "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")
  (test-post server :post "/deposito/1" "199.93")
  (test-post server :post "/deposito/4" "325.99")

  ;curl http://localhost:9999/saldo/1
  ;curl -d "199.99" -X POST http://localhost:9999/deposito/1

  (chain/execute {:title "Titulo"} [contas-interceptorwidget-finder])
  ;(http/default-interceptors [])
  (chain/execute {:title "Titulo"} [(i/interceptor contas-interceptor) widget-finder widget-renderer])
  (chain/execute {:title "Titulo"} [widget-renderer])
  {:title    "Titulo",
   :widget   {:id 1, :title "Titulo"},
   :response {:status 200, :body "Widget ID 1, Title 'Titulo'"}}
  {:title    "Titulo",
   :response {:status 404, :body "Not Found"}})
