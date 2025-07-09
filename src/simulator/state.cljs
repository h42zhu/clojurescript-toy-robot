(ns simulator.state
  (:require [reagent.core :as r]))


;; the global application state
(defonce app-state (r/atom {:robot-placed false 
                            ;; 2 elements representing x y index, (0, 0) being the bottom left
                            :index [0 0] 
                            ;; 0 -> north, 1 -> east, 2, south, 3 west
                            :direction 0}))

(def tile-size 64)


(defn index-to-position [idx]
  (let [[x y] idx]
    ;; add the spacing between tiles as well
    [(* (+ 2 tile-size) x) (* (+ 2 tile-size) y)]))