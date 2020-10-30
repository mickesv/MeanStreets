(ns mean-streets.core
  (:require [mean-streets.game]
            [mean-streets.game-repl])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (->
   (mean-streets.game/make nil #{:phone-book :no-tea})
   (mean-streets.game-repl/launch)))
