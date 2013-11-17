(ns ^:shared {{namespace}}.app
  (:require [clojure.set :as set]
            [io.pedestal.app.util.platform :as platform]))

(defn visible-widgets [[[_ event wid]]]
  (cond (= event :created-widget) [[[[:info :visible] (fnil conj #{}) wid]]]
        (= event :removed-widget) [[[[:info :visible] disj wid]]]))

(defn startup [inform-message]
  [[[[:ui :root] :change-screen :message [:ui :message]]]])

(defn set-text [inform-message]
  [[[[:ui :message] :set-text "Hello World"]
    [[:services :message] :inform-set "Hello World"]]])

(defn inspect [s]
  (fn [inform-message]
    (.log js/console s)
    (.log js/console (pr-str inform-message))
    []))

(def config
  {:in [[visible-widgets [:registry] :*]
        [startup [:app] :startup]
        [set-text [:ui :message] :startup]
        [(inspect "<<<<<<<<") [:**] :*]]

   :out [[(inspect ">>>>>>>>") [:**] :*]]})
