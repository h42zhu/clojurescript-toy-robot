(ns simulator.main
  (:require [reagent.dom.client :as rdom]
            [simulator.components :as components]))




;; Entry point of the client app
(defn ^:export init []
  (let [root (rdom/create-root (js/document.getElementById "app"))]
    (components/init-event-listeners)
    (rdom/render root [components/grid])))