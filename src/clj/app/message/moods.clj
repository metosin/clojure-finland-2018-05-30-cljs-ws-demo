(ns app.message.moods
  (:require [clojure.tools.logging :as log]
            [eines.core :as eines]))

(defonce moods (atom {}))

(defn broadcast! [new-moods]
  (log/debug "new-moods" (pr-str new-moods))
  (let [message {:body {:type :moods
                        :moods new-moods}}]
    (doseq [socket (vals @eines/sockets)]
      (let [{:keys [send!]} socket]
        (send! message)))))

(add-watch moods :broadcaster (fn [_ _ _ new-moods]
                                (broadcast! new-moods)))
