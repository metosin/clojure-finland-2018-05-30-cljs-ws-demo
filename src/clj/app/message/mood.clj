(ns app.message.mood
  (:require [clojure.tools.logging :as log]
            [plumbing.core :refer [defnk]]
            [app.message.moods :as moods]))

(defnk get-mood [[:session username]]
  (log/debugf "get-mood: username=[%s]" username)
  {:body {:mood (get @moods/moods username)}})

(defnk set-mood [[:body new-mood] [:session username]]
  (log/debugf "set-mood: username=[%s], new-mood=[%s]" username new-mood)
  (swap! moods/moods assoc username new-mood)
  {:body {:message "Mood updated"}})

(def handlers {:get-mood #'get-mood
               :set-mood #'set-mood})
