(ns widgetry.util
  "Common widgetry fns"
  (:require [widgetry.widget :as w]
            [widgetry.rendering :as r]))

(defn create!
  [& options]
  (let [{:keys [create destroy transform]
         :or {transform w/default-transform! destroy r/remove-all!}} (apply hash-map options)]
    (fn [wid domid ichan & args]
      (let [widget {:wid wid
                    :domid domid
                    :ichan ichan
                    :destroy #(destroy domid)
                    :options (apply hash-map args)}
            tchan (w/start! widget {} transform)]
        (create widget)
        (assoc widget :tchan tchan)))))
