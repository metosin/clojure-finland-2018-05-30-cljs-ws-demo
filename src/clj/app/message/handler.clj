(ns app.message.handler
  (:require [clojure.tools.logging :as log]
            [app.message.login :as login]
            [app.message.mood :as mood]))

(def handlers (merge login/handlers
                     mood/handlers))

(defn not-found [message]
  (log/warn "unknown message" (-> message :body :type))
  nil)

(defn handle-message [{:keys [body] :as message}]
  (log/info "incoming:" (pr-str message))
  (let [handler (get handlers (-> message :body :type) not-found)]
    (try
      (handler message)
      (catch Exception e
        (log/error e "error in message handler, type=" (-> message :body :type))
        {:body {:type :error}}))))
