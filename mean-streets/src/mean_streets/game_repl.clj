(ns mean-streets.game-repl
  (:require [clojure.string :as str]
            [mean-streets.actions :refer [execute-action]]))

(defn game-prompt [game-state]
  (let [current (:current-scene game-state)
        name (first (:name (get-in game-state current)))
        prompt (str (when name (str "[" name "]")) "$ ")]
    prompt))

(defn print-flush-output [game-state]
  (when-not (= "" (:output game-state))
    (println (:output game-state)))
  (assoc game-state :output ""))

(defn print-prompt [game-state]
  (print (game-prompt game-state))
  (flush)
  game-state)

(defn string->vec [in-str]
  (->
   in-str
   (str/trim)
   (str/lower-case)
   (str/split #" ")))

(defn process-input [game-state input]
  (execute-action input game-state))

(defn launch [game-state]
  "The basic game REPL. Reads input and translates to actions in the game"
  (let [initial-state (print-flush-output game-state)]
    (loop [state (print-prompt initial-state)]
      (let [input (string->vec (read-line))
            new-state (process-input state input)
            new-state (print-flush-output new-state)]
        (if-not (:quitting new-state)
          (recur (print-prompt new-state)))))))

