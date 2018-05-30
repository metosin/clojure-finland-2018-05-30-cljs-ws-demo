(ns app.ws
  (:require [integrant.core :as ig]
            [eines.core :as eines]
            [eines.server.immutant :as eines-immutant]
            [eines.middleware.rsvp :as rsvp]
            [eines.middleware.session :as session]
            [app.message.handler :as handler]))

(defmethod ig/init-key ::handler [_ _]
  (-> (eines/handler-context handler/handle-message
                             {:middleware [(session/session-middleware)
                                           (rsvp/rsvp-middleware)]})
      (eines-immutant/create-handler)))

