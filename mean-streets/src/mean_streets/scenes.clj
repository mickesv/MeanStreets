(ns mean-streets.scenes)

(defn make [names items characters actions disallowed-actions]
  {:name names
   :items (set items)
   :characters (set characters)
   :actions actions
   :denied-actions disallowed-actions})

(def alley (make
            ["Noir Alley" "noir" "alley"]
            #{:body :tea}
            #{:coroner :constable}
            {:look "A lonely streetlight shines in from the main street. Moist drips from the walls."}
            {:leave "I'm afraid I can't let you do that, Dave"}))

(def scene-map
  {:alley alley})
