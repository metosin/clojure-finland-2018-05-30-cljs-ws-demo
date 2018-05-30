(ns app.components
  (:require [integrant.core :as ig]
            [potpuri.core :as p]
            [syksy.core :as syksy]
            [syksy.web.index :as index]
            [app.ws :as ws]))

(defn components []
  (p/deep-merge
    (syksy/default-components {:index-body (index/index {:title "ClojureFinland WebSocket demo"})
                               :routes (ig/ref ::ws/handler)})
    {::ws/handler {}}))
