(ns {{namespace}}.widgets.message
  (:require [dommy.core :as dommy]
            [widgetry.rendering :as r]
            [widgetry.widget :as w]
            [widgetry.util :as util])
  (:require-macros [dommy.macros :refer [sel1]])
  (:use [cljs.core.async :only [put!]]))

(defmulti transform! (fn  [_ _  [_ op]] op))

(defmethod transform! :default [context state transformation]
  (w/default-transform! context state transformation))

(defmethod transform! :set-text [context state [_ _ text]]
  (dommy/set-text! (sel1 [:.message]) text))

(defn- create-widget! [{:keys [domid wid ichan options]}]
  (dommy/append!
    (sel1 domid)
    [:.message])
  (put! ichan [[wid :startup]]))

(def create! (util/create! :create create-widget! :transform transform!))
