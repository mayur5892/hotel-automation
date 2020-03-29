(ns hotel-automation.core
  (:require [cheshire.core :as json]
            [hotel-automation.controller :refer [default-state handle-controller-event]]
            [hotel-automation.utils :refer [print-current-state]]))


(defn set-default-state [no-of-floors no-of-main-corridors no-of-sub-corridor]
  (when-let [state (default-state no-of-floors
                                    no-of-main-corridors
                                    no-of-sub-corridor)]
    (print-current-state state)
    state))

(defn on-controller-event [event]
  (if-let [state (handle-controller-event event)]
    (do
      (print-current-state state)
      state)
    (println "System not Configured. Please set default state")))









