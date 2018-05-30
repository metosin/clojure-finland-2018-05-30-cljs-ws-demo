(ns app.message.login
  (:require [clojure.tools.logging :as log]
            [plumbing.core :refer [defnk]]
            [app.message.moods :as moods]))

(defnk login [[:body username]]
  (log/debug "login:" username)
  (swap! moods/moods assoc username "")
  {:session {:username username}})


(defnk logout [[:session username]]
  (log/debug "logout:" username)
  (swap! moods/moods dissoc username))

(def handlers {:login #'login
               :logout #'logout})
