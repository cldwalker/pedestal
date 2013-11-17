(ns {{namespace}}.start
  (:require [io.pedestal.app.construct :as construct]
            [io.pedestal.app.route :as route]
            [io.pedestal.app.util.observers :as observers]
            [widgetry.root :as root]
            [widgetry.registry :as registry]
            [{{namespace}}.app :as app]
            [{{namespace}}.services :as services]
            [{{namespace}}.widgets.message :as wmessage])
  (:use [cljs.core.async :only [put! chan close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def widgets
  {:message wmessage/create!})

(defn hide-functions [transform-message]
  (mapv (fn [msg]
          (mapv #(if (fn? %) :f %) msg))
        transform-message))

(defn log-print [message]
  (cond (= (:in message) :router)
        (.log js/console (str "> " (pr-str :router
                                           (:id message)
                                           (hide-functions (:transform message)))))
        
        :else (.log js/console (pr-str message))))

(defn create-app [start-services!]
  (let [cin (construct/build {:info {}} app/config)
        services-transform (start-services! cin)
        widgets-transform-c (chan 10)]
    
    (route/router [:ui :router] widgets-transform-c)
    (observers/subscribe :log log-print)
    (registry/set-router! [:ui :router] widgets-transform-c cin)
    
    (put! cin [[[:io.pedestal.app.construct/router] :channel-added
                services-transform [:services :* :**]]])
    
    (put! cin [[[:io.pedestal.app.construct/router] :channel-added
                widgets-transform-c [:ui :* :**]]])
    
    (let [root-widget (root/create! [:ui :root] :#content cin :widgets widgets)]
      (registry/add-widget! root-widget))
    
    (put! cin [[[:app] :startup]])
    cin))

(defn ^:export main []
  (create-app services/start-services!))
