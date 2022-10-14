(ns mybank-web-api.core-test
  (:require [clojure.test :refer :all]
            [mybank-web-api.core :refer :all]
            [mybank-web-api.server :as server]))

; Exercício - Aula02 - Unit Tests
;  1 - Executar os testes em um terminal.
;  2 - Adicionar casos de testes para saque. (Não pode ter saldo negativo)
;  3 - Implementar pelo menos 2 melhorias nos endpoints a partir de casos de teste imaginados que falham.

(deftest saque-test
  (testing "Testando saque com conta inválida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/4" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 500, :body "Conta inválida!"})))

  (testing "Testando saque com conta válida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/1" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 200,
            :body "{:id-conta :1, :novo-saldo {:saldo 0.0}}"})))

  (testing "Testando saque com valor negativo"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/1" "-10")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 200,
            :body "{:id-conta :1, :novo-saldo {:saldo 10.0}}"}))))