(ns mean-streets.actions
  (:require [clojure.string :as str]
            [clojure.set :refer [intersection union difference]]
            [mean-streets.items :as items]
            [mean-streets.scenes :as scenes]))

;; TODO: no println or read-line in here... or at least in a very specific place.

;; Dispatcher
;; ----------
(def synonyms '#{[quit q exit]
                 [look l peek gander inspect examine]
                 [goto g enter visit]
                 [inventory i inv]
                 [take t nick pickup]})

(defn synonym-lookup [word]
  (some (fn [words]
          (when (some #(= (str %) (str word)) words)
            (str (first words))))
        synonyms))

(defmulti execute-action
  (fn [input game-state] (or (synonym-lookup (first input))
                             (first input))))

;; Helpers
;; ----------
(defn prompt [out]
  (print out)
  (flush)
  (str/trim (str/lower-case (read-line))))

(def location-items [:data :items])
(def location-scenes [:data :scenes])

(defn mentioned-elements [input elements]
  (filter #(as-> % $
             (set (:name (val $)))
             (intersection $ (set input))
             (not (empty? $))) elements))

(defn filter-available [elements available]
  (filter #(as-> % $
             (intersection #{(key $)} (set available))
             (not (empty? $))) elements))

(defn collect-output [elements prompt action-kw]
  (->>
   elements
   (map #(let [name (first (:name (val %)))
               action (get-in (val %) [:actions action-kw])
               denied (get-in (val %) [:denied-actions action-kw])]
           (str prompt " " name " " 
                (cond
                  (true? denied) (str "is denied")
                  denied (str denied)
                  action (str action)))))             
   (str/join "\n")))

(defn pretty-format-items [item-list game-state]
  (let [item-names (map #(first (:name (find-element % location-items game-state))) item-list)]
    (str/join "\n" item-names)))

;; No-object actions
;; ----------

(defmethod execute-action :default [input game-state]
  (if-not (str/blank? (first input))
    (assoc game-state :output (str "I don't know how to perform " (first input)))
    game-state))

; TODO use a dialogue engine here instead of the promt and readline
(defmethod execute-action "quit" [input game-state]
  (let [in (prompt "Are you sure you want to quit? ")
        y? (str/starts-with? in "y")]
    (if y? (assoc game-state
                  :output "Ok Bye!"
                  :quitting true)
        game-state)))

(defmethod execute-action "leave" [input game-state]
  (let [loc (or (first (:name (get-in game-state (:current-scene game-state))))
                "nowhere")
        denied-reason (get-in game-state (conj (:current-scene game-state) :denied-actions :leave))]
    (if denied-reason
      (assoc game-state :output denied-reason)
      (assoc game-state
             :output (str "You leave " loc)
             :current-scene nil))))

(defmethod execute-action "where" [input game-state]
  (assoc game-state
         :output
         (str "You are: "
              (if-not (:current-scene game-state)
                "nowhere"
                (first
                 (get-in game-state (conj (:current-scene game-state) :name)))))))

(defmethod execute-action "_gs" [input game-state]
  (assoc game-state :output (str game-state)))

(defn look-inventory [game-state]
  (str "You have:\n" (pretty-format-items (:inventory game-state) game-state)))

(defmethod execute-action "inventory" [input game-state]
  (assoc game-state :output (look-inventory game-state)))

;; Single-object actions
;; ----------

;; TODO look at scene should also list characters
(defn look-scene [game-state & scene]
  (let [scn (or (first scene) (when (:current-scene game-state)
                        (get-in game-state (:current-scene game-state))))
        denied-reason (get-in scn [:denied-actions :look])]
    (cond
      (not scn) (str "You are nowhere, there is nothing to see.")
      (true? denied-reason) (str "You cannot see anything.")
      denied-reason (str denied-reason)      
      :else (str (get-in scn [:actions :look]) 
                 "\n\nAvailable items\n"
                 (pretty-format-items (:items scn) game-state)))))

(defn look-item [input game-state]
  (->
   (rest input)
   (mentioned-elements (get-in game-state location-items))
   (filter-available (union (:inventory game-state)
                            (when (:current-scene game-state)
                              (get-in game-state
                                      (conj (:current-scene game-state) :items)))))
   (collect-output "Looking at" :look)))
  
(defmethod execute-action "look" [input game-state]
  (assoc game-state
         :output (cond
                   (empty? (rest input)) (look-scene game-state)
                   (= "inv" (second input)) (look-inventory game-state)
                   :else (look-item input game-state))))

(defmethod execute-action "goto" [input game-state]
  (let [scene (first (mentioned-elements #{(second input)} (get-in game-state location-scenes)))
        scene-id (conj location-scenes (key scene))]
    (if-not scene
      (assoc game-state :output (str "I can't find that place"))
      (assoc game-state
             :current-scene scene-id
             :output (str "You enter "
                          (first (:name (val scene))) "\n"
                          (look-scene game-state (val scene)))))))

(defmethod execute-action "take" [input game-state]
  (cond
    (empty? (rest input)) (assoc game-state :output (str "Try taking something."))
    (not (:current-scene game-state)) (assoc game-state :output (str "Try being somewhere where there is something to take."))
    :else
    (let [mentioned (mentioned-elements (rest input) (get-in game-state location-items))
          available (filter-available mentioned (get-in game-state (conj (:current-scene game-state) :items)))
          allowed (filter #(not (get-in (val %) [:denied-actions :take])) available)]
      (->
       (assoc-in game-state (conj (:current-scene game-state) :items)
                 (difference (set (get-in game-state (conj (:current-scene game-state) :items)))
                             (set (keys allowed))))
       (assoc :inventory (union (:inventory game-state) (set (keys allowed))))
       (assoc :output (collect-output available "Taking" :take))))))
