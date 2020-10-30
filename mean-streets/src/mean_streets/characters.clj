(ns mean-streets.characters)

(defn make [name]
  "Create a game character"
  {:name name
   :events nil})

(def secretary (make "miss Twocents"))
(def constable (make "John Flatfoot"))
(def coroner (make "T. Clever Byfar"))

(def character-map
  {:secretary secretary
   :constable constable
   :coroner   coroner})

               
