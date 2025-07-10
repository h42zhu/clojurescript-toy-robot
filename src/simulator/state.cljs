(ns simulator.state
  (:require [reagent.core :as r]
            [simulator.api :as api]))


;; the global application state
(defonce app-state (r/atom {:robot-placed false 
                            ;; 2 elements representing x y index, (0, 0) being the bottom left
                            :index [0 0] 
                            ;; 0 -> north, 1 -> east, 2, south, 3 west
                            :direction 0}))

(def tile-size 64)

(defn init-state []
  (let [last-index (api/get-index)])
  )

;; add watch for state changes and saves the new index (position) to DB
(add-watch app-state :index
           (fn [_ _ _ new-state]
             (api/save-index (:index new-state))))
