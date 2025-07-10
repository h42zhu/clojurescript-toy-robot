(ns simulator.api
  (:require [cljs-http.client :as http]))

(def api-base-url "http://localhost:3000")

(defn save-index [new-index]
  (let [url (str api-base-url "/save")
        ;; Prepare the request options for POST
        options {:json-params {:index new-index}
                 :headers {"Content-Type" "application/json"}}
        ;; Make the POST request
        response (http/post url options)]
    (when (:success response)
      (.-body response))))

(defn get-index []
  (let [url (str api-base-url "/index")
        ;; Prepare the request options for POST
        options {:headers {"Content-Type" "application/json"}}
        ;; Make the POST request
        response (http/get url options)]
    (when (:success response)
      (.-body response))))