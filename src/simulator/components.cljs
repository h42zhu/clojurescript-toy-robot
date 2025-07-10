(ns simulator.components
  (:require [goog.events :as events]
            [simulator.state :as state]))

;; If components are not required, using HTML Canvas is probably a better implementation choice

;; constants
(def grid-size 5)
(def robot-image-src "/image/robot.png")
(def arrow-image-src "/image/arrow.png")

(def robot-offset [4 8])
(def dir-to-classname
  {0 ""
   1 "rotate90"
   2 "rotate180"
   3 "rotate270"})

(def dir-to-coordinates
  {0 [0 1]
   1 [1 0]
   2 [0 -1]
   3 [-1 0]})

(def dir-to-text
  {0 "NORTH"
   1 "EAST"
   2 "SOUTH"
   3 "WEST"})

;; utility function for converting between tile index to position on the grid
(defn index-to-position [idx]
  (let [[x y] idx]
    ;; add the spacing between tiles as well
    [(* (+ 2 state/tile-size) x) (* (+ 2 state/tile-size) y)]))

;; Event listeners and state changes
(defn parse-event [e]
  (let [type e.type
        key e.key]
    (cond 
      (and (= "keyup" type) (= "ArrowUp" key)) :move
      (and (= "keyup" type) (= "ArrowLeft" key)) :left
      (and (= "keyup" type) (= "ArrowRight" key)) :right)))


(defn rotate [curr change]
  (let [new-dir (+ change curr)]
    (cond 
      (> new-dir 3) 0
      (< new-dir 0) 3
      :else new-dir)))

(defn move [curr change]
  (let [[x y] curr
        [x-delta y-delta] change
        new-x (max 0 (min (+ x x-delta) (dec grid-size)))
        new-y (max 0 (min (+ y y-delta) (dec grid-size)))]
    [new-x new-y]))

(defn update-robot [e]
  (let [parsed (parse-event e)]
    (when (:robot-placed @state/app-state)
      (cond
        (= :left parsed) (swap! state/app-state update :direction (fn [d] (rotate d -1)))
        (= :right parsed) (swap! state/app-state update :direction (fn [d] (rotate d 1)))
        (= :move parsed) (let [dir (:direction @state/app-state)
                               change (get dir-to-coordinates dir)]
                           (swap! state/app-state update :index (fn [idx] (move idx change))))))))

(defn init-event-listeners []
  (events/removeAll js/document "keyup") ;; remove all keyboard listeners 
  (events/listen js/document "keyup" update-robot) ;; remove all keyboard listeners
)

;; Components
(defn button
  [props]
  (let [{:keys [label on-click class]} props
        default-class "bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105"]
    [:button
     {:on-click on-click
      :class (str default-class " " class)}
     label]))

(defn image-container [x y src class-name image-class-name]
  [:div {:className (str class-name) :style {:left x :bottom y}}
   [:img {:src src :className image-class-name}]])

(defn place-robot []
  (let [{:keys [index direction]} @state/app-state
        [x y] (index-to-position index)
        [x-offset y-offset] robot-offset
        rotate-classname (get dir-to-classname direction)]
    [:div 
     (image-container (+ x-offset x) (+ y-offset y) robot-image-src "absolute" "")
     (image-container x y arrow-image-src "absolute" rotate-classname)]))


(defn tile [x y]
  [:div.tile {:key (str x y)
              :on-click (fn [_e] 
                          (swap! state/app-state assoc :robot-placed true)
                          (swap! state/app-state assoc :direction 0)
                          (swap! state/app-state assoc :index [x y]))}])

(defn grid []
  (let [{:keys [robot-placed]} @state/app-state]
    [:div.container
   (for [y (range grid-size)
         x (range grid-size)]
     (tile x (- grid-size y 1)))
   (when robot-placed
     (place-robot))]))

(defn app-container []
  [:div
   (grid)
   [:div.button-container
    (button {:label "Left" :on-click (fn [_e] (swap! state/app-state update :direction (fn [d] (rotate d -1))))} )
    (button {:label "Move" :on-click (fn [_e] 
                                       (let [dir (:direction @state/app-state)
                                             change (get dir-to-coordinates dir)]
                                         (swap! state/app-state update :index (fn [idx] (move idx change)))))})
    (button {:label "Right" :on-click (fn [_e] (swap! state/app-state update :direction (fn [d] (rotate d 1))))})]
   [:div
    (button {:label "Report" :on-click (fn [_e] 
                                          (let [{:keys [index direction]} @state/app-state]
                                            (js/alert (str (first index) "," (second index) "," (get dir-to-text direction "")))))} )]])