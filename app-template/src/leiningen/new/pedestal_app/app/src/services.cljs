(ns {{namespace}}.services
  (:require [io.pedestal.app.match :as match]
            [widgetry.log :as l])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:use [cljs.core.async :only [chan <! >! put! alts! timeout close!]]))

(defn inform-set [inform ichan]
  (.log js/console (str "Send to Server: " (pr-str inform))))

(def config
  (match/index [[inform-set [:services :message] :inform-set]]))

(defn start-services! [ichan]
  (let [tchan (chan 10)]
    (go (while true
          (let [transform (<! tchan)]
            (l/log "->" :transform-services :t transform)
            (doseq [transformation transform]
              (when-let [handler-fn (ffirst (match/match-items config transformation))]
                (handler-fn transformation ichan))))))
    tchan))
