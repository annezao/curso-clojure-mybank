(ns mybank-web-api.core-test-annezao
  (:require [clojure.test :refer :all]
            [mybank-web-api.core :refer :all]
            [mybank-web-api.server :as server]
            [mybank-web-api.error :as error]))

(deftest saldo-test
  (testing "Testando saldo com conta inválida"
    (is (= (let [_ (server/start)
                 response (test-request server/server :get "/saldo/5")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-account :headers))))

  (testing "Testando saldo com conta válida"
    (is (= (let [_ (server/start)
                 response (test-request server/server :get "/saldo/1")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 200,
            :body "{:id-conta :1, :novo-saldo {:saldo 100}}"}))))

(deftest saque-test
  (testing "Testando saque com conta inválida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/5" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-account :headers))))

  (testing "Testando saque com conta válida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/3" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 200,
            :body "{:id-conta :3, :novo-saldo {:saldo 200.0}}"})))

  (testing "Testando saque com valor negativo"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/1" "-10")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-amount :headers))))
  
  (testing "Testando saque com valor 0"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/1" "0")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-amount :headers))))
  
  (testing "Testando saque maior que saldo"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/saque/1" "150")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/insufficient-funds :headers)))))

(deftest deposito-test
  (testing "Testando deposito com conta inválida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/deposito/5" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-account :headers))))

  (testing "Testando deposito com conta válida"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/deposito/2" "100")
                 _ (server/stop-server)]
             (dissoc response :headers))
           {:status 200,
            :body "{:id-conta :2, :novo-saldo {:saldo 300.0}}"})))

  (testing "Testando deposito com valor negativo"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/deposito/1" "-10")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-amount :headers))))
  
  (testing "Testando deposito com valor 0"
    (is (= (let [_ (server/start)
                 response (test-post server/server :post "/deposito/1" "0")
                 _ (server/stop-server)]
             (dissoc response :headers))
           (dissoc error/invalid-amount :headers)))))