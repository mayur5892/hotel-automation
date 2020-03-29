(ns hotel-automation.controller
  (:require [hotel-automation.persistence :refer [persist-state retrieve-state]]
            [hotel-automation.utils :refer [particular-state-by-id state-key]]))

(def power-unit-map {:light 5 :ac 10 :main-corridor 15 :sub-corridor 10})

(defn- compute [total-units {:keys [light ac]}]
  (cond-> total-units
          light (+ (:light power-unit-map))
          ac (+ (:ac power-unit-map))))

(defn- compute-floor-power-consumption [{:keys [main-corridors sub-corridors]}]
  (let [total-units-from-main-corridors (reduce compute 0 (vals main-corridors))]
    (reduce compute total-units-from-main-corridors (vals sub-corridors))))

(defn power-exceeds-max-limit? [{:keys [floors total-main-corridors total-sub-corridors]} floor-key]
  (> (compute-floor-power-consumption (get floors floor-key))
     (+ (* (power-unit-map :main-corridor) total-main-corridors)
        (* (power-unit-map :sub-corridor) total-sub-corridors))))

(defn turn-on-sub-corridors-ac [state floor-key]
  (update-in state
             [:floors floor-key :sub-corridors]
             #(reduce-kv (fn [m k _]
                           (update-in m [k :ac] (constantly true)))
                         % %)))

(defn- turn-off-sub-corridors-ac [state floor-key sub-corridor-key]
  (update-in state
             [:floors floor-key :sub-corridors]
             #(reduce-kv (fn [m k _]
                           (if (= k sub-corridor-key)
                             m (update-in m [k :ac] (constantly false))))
                         % %)))

(defn- change-sub-corridors-light [state floor-key sub-corridor-key light-state]
  (update-in state
             [:floors floor-key :sub-corridors sub-corridor-key :light]
             (constantly light-state)))

(defn- set-default-main-corridor-state [total-main-corridors]
  (reduce #(assoc % (keyword (str "main-corridor" %2))
                    {:id    (inc %2)
                     :ac    true
                     :light true})
          {} (range total-main-corridors)))

(defn- set-default-sub-corridor-state [total-sub-corridors]
  (reduce #(assoc % (keyword (str "sub-corridor" %2))
                    {:id (inc %2) :ac true :light false})
          {} (range total-sub-corridors)))

(defn- set-default-floor-state [no-of-floors no-of-main-corridors no-of-sub-corridor]
  (reduce #(assoc-in % [:floors (keyword (str "floor" %2))]
                     {:id             (inc %2)
                      :main-corridors (set-default-main-corridor-state no-of-main-corridors)
                      :sub-corridors  (set-default-sub-corridor-state no-of-sub-corridor)})
          {:total-floors         no-of-floors
           :total-main-corridors no-of-main-corridors
           :total-sub-corridors  no-of-sub-corridor}
          (range no-of-floors)))

(defn- enrich-event [event state floor-id sub-corridor-id]
  (assoc event :state state
               :floor-key (state-key "floor"
                                     (dec floor-id))
               :sub-corridor-key (state-key "sub-corridor"
                                            (dec sub-corridor-id))))


(defn default-state [no-of-floors no-of-main-corridors no-of-sub-corridor]
  (let [state (set-default-floor-state no-of-floors
                                       no-of-main-corridors
                                       no-of-sub-corridor)]
    (persist-state state)))

(defmulti state-controller (fn [event] (:event-type event)))

(defmethod state-controller :rest [{:keys [state floor-key sub-corridor-key]}]
  (let [updated-state (change-sub-corridors-light state
                                                  floor-key
                                                  sub-corridor-key
                                                  false)]
    (turn-on-sub-corridors-ac updated-state
                              floor-key)))


(defmethod state-controller :motion [{:keys [state floor-key sub-corridor-key]}]
  (let [updated-state (change-sub-corridors-light state
                                                  floor-key
                                                  sub-corridor-key
                                                  true)]
    (if (power-exceeds-max-limit? updated-state floor-key)
      (turn-off-sub-corridors-ac updated-state floor-key sub-corridor-key)
      updated-state)))


(defn handle-controller-event [{:keys [floor-id sub-corridor-id] :as event}]
  (when-let [state (retrieve-state)]
    (-> event
        (enrich-event state
                      floor-id
                      sub-corridor-id)
        state-controller
        persist-state)))



