(ns hotel-automation.core
  (:require [hotel-automation.event :as event]
            [hotel-automation.hotel :as hotel]
            [hotel-automation.utils :as utils])
  (:import [java.util Map List]))

(defn construct-initial-state [floors main-corridor-per-floor sub-corridor-per-floor]
  (let [default-state {:floors (hotel/construct-floor floors
             main-corridor-per-floor
             sub-corridor-per-floor)}]
    (utils/print-state default-state)
    default-state))

(defprotocol ProcessEvent
  (process-event [state event]))

(extend-protocol ProcessEvent
  Map
  (process-event [state event]
    (let [new-state (event/handle-event state event)]
      (utils/print-state new-state)
      new-state))

  List
  (process-event [state events]
    (println "inside List")
    (reduce
      #(event/handle-event % %2)
      state events)))