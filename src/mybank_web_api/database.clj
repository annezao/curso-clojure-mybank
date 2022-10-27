(ns mybank-web-api.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database []
  component/Lifecycle

  (start 
   [this]
    ;; Iniciar o atomo contendo dados das contas.edn bancárias inicialmente procurando um
    ;;; arquivo resources/contas.edn.edn, caso não exista iniciar o map pré definido.
    (let [arquivo (-> "resources/contas.edn"
                      slurp
                      read-string)]
      (assoc this :contas (atom arquivo))))


  (stop 
   [this]
    ;; Limpar as contas.edn da memória e Salvar em disco para uso futuro.
    (assoc this :store nil)))

(defn new-database []
  (->Database))



