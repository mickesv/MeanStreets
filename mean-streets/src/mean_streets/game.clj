(ns mean-streets.game
  (:require [mean-streets.scenes :refer [scene-map]]
            [mean-streets.characters :refer [character-map]]
            [mean-streets.items :refer [item-map]]))


(defn make [current-scene inventory]
  {:start-date (.toString (java.util.Date.))
   :current-scene current-scene
   :inventory (set inventory)
   :output "It is a drissly night. You get a call.\n
\"It's me\" says Ms. Twocents. \"There's been a murder at Noir Alley\"."
   :prompt ""
   :data {:scenes scene-map
          :characters character-map
          :items item-map}
   :quitting false})
      
   
