(ns app.front.main
  (:require [reagent.core :as r]
            [eines.client :as eines]
            [reagent-dev-tools.core :as dev-tools]
            [reagent-dev-tools.state-tree :as dev-state]))

(defonce state (r/atom nil))

(dev-state/register-state-atom "State" state)

;;
;; Login:
;;

(defn login! [username]
  (eines/send!
    {:body {:type :login, :username username}}
    (fn [response]
      (if (eines/success? response)
        (do (js/console.log "success:" response)
            (swap! state assoc-in [:login :username] username))
        (js/console.error "fail:" response)))))

(defn logout! []
  (eines/send!
    {:body {:type :logout}}
    (fn [response]
      (if (eines/success? response)
        (swap! state dissoc :login)
        (js/console.error "fail:" response)))))

(defn login-view [{:keys [login]}]
  (let [{:keys [value]} login]
    [:div.login
     [:h1 "Login"]
     [:form.login-form {:on-submit (fn [e]
                                     (.preventDefault e)
                                     (login! value))}
      [:input.login-input {:type :text
                           :value value
                           :on-change (fn [e]
                                        (->> e .-target .-value (swap! state assoc-in [:login :value])))
                           :placeholder "Username..."}]]]))

;;
;; Mood:
;;

(defn current-moods-view [{:keys [moods]}]
  [:div.current-moods
   [:h2 "Current moods"]
   [:table
    [:thead
     [:tr
      [:th "User"]
      [:th "Mood"]]]
    [:tbody
     (for [[username mood] moods]
       [:tr {:key username}
        [:td username]
        [:td mood]])]]])

(defn send-mood-update! [new-mood]
  (eines/send!
    {:body {:type :set-mood, :new-mood new-mood}}
    (fn [response]
      (if (eines/success? response)
        (do (js/console.log "success:" (pr-str response))
            (swap! state assoc :form-value ""))
        (js/console.error "fail:" (pr-str response))))))

(defn mood-form [{:keys [form-value]}]
  [:form.mood-form {:on-submit (fn [e]
                                 (.preventDefault e)
                                 (send-mood-update! form-value))}
   [:input.mood-input {:type :text
                       :value form-value
                       :on-change (fn [e]
                                    (->> e .-target .-value (swap! state assoc :form-value)))
                       :placeholder "Update your mood..."}]])

(defn mood-view [state]
  [:div.mood-view
   [:h1 "What's your mood?"]
   [current-moods-view state]
   [mood-form state]
   [:button.logout {:on-click (fn [_] (logout!))} "Logout"]])

;;
;; Main:
;;

(defn main-view []
  (let [state @state]
    (if (-> state :login :username)
      [mood-view state]
      [login-view state])))

;;
;; Server messages:
;;

(defmulti on-message (comp :type :body))

(defmethod on-message :default [message]
  (js/console.warn "unknown message:" message)
  {:body {:type :not-found
          :message "not found"}})

(defmethod on-message :moods [message]
  (let [moods (-> message :body :moods)]
    (js/console.log "Server sent moods:" moods)
    (swap! state assoc :moods moods)))

;;
;; WebSocket:
;;

(defn on-connect []
  (js/console.log "on-connect"))

(defn on-close []
  (js/console.log "on-close"))

(defn on-error []
  (js/console.log "on-error"))

;;
;; Init:
;;

(do (js/console.log "init...")
    (eines/init! {:on-connect on-connect
                  :on-message on-message
                  :on-close on-close
                  :on-error on-error})
    (r/render [main-view] (js/document.getElementById "app"))
    (when-let [dev-el (js/document.getElementById "dev")]
      (r/render [dev-tools/dev-tool {}] dev-el)))
