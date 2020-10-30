(ns mean-streets.items)

(defn make
  ([names actions] (make names actions #{}))
  ([names actions denied-actions] {:name names
                                   :actions actions
                                   :denied-actions denied-actions}))

(def phone-book (make ["phonebook"]
                      {:look "It is a black leather phone book that fits nicely in your pocket"
                       :read true}))

(def no-tea (make ["no tea" "tea"]
                  {:taste "no tea tastes like misery"
                   :look "You can see the bottom!"}
                  {:drop true :throw true}))

(def body (make ["dead body" "body" "stiff" "corpse" "dummy" "poor sod"]
                {:look "Your run-of-the-mill dead body. Male, 40-50. Dark hair. Dressed in jeans and t-shirt"
                 :lift true
                 :move true}
                {:take true}))

(def tea (make ["tea" "brew"]
               {:taste "Bliss"
                :look "It is a nice cup of black tea."}
               {:throw true}))

(def item-map
  {:phone-book phone-book
   :no-tea no-tea
   :tea tea
   :body body})

(comment
(def all-items
  {:phone-book {:description "a black leather phone book"
                :actions [:look :open]}
   :no-tea {:description "no tea"
            :actions [:smell]}
   :pocket-lint {:description "some pocket lint"
                 :actions [:look]}
   :chalk-outline {:description "chalk outline of a body"
                   :actions [:look]}
   :body {:description "dead body. Male, 40-50. Dark hair. Dressed in jeans and t-shirt"
          :actions [:look :move]}
   :knife {:description "knife with a gilded blade and a black leather handle"
           :actions [:look :pick-up :drop :use]}
   :red-rose {:description "red rose that looks expensive"
              :actions [:look :pick-up :drop :use :smell]}
   :typewriter {:description "a Facit typewriter where the letters QWSD are stuck"
                :actions [:look]}
   :filing-cabinet {:desciption "a locked filing-cabinet that you used to have a key to"
                    :actions [:look]}})
)
