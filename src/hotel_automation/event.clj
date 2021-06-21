(ns hotel-automation.event
  (:require [ hotel-automation.utils :as utils]))


(def per-unit-rate {:main-corridor 15
                    :sub-corridor 10})

(defn update-state [state {:keys [id] :as new-state}]
  (->> state
    (remove #(= id (:id %)))
    (cons new-state)))

(defn update-sub-corridor-light [sub-corridors id light]
  (let [sub-corridor (utils/find-by-id sub-corridors id)
        new-state (assoc sub-corridor :light light)]
    (update-state sub-corridors new-state)))


(defn- max-power-per-floor [{:keys [main-corridors sub-corridors]}]
  (+
    (* (:main-corridor per-unit-rate) (count main-corridors))
    (* (:sub-corridor per-unit-rate) (count sub-corridors))))

(defn compute-floor-power-consumption [{:keys [main-corridors sub-corridors]}]
  (reduce (fn [power {:keys [light AC]}]
            (cond-> power
              light (+ 5)
              AC (+ 10)))
    0 (concat main-corridors sub-corridors)))

(defn- switch-off-sub-corridor-ac [sub-corridors sub-corridor-id]
  (let [new-state (->> sub-corridors
                    (filter #(not= sub-corridor-id (:id %)))
                    first
                    (#(assoc % :AC false)))]
    (update-state sub-corridors new-state)))

(defn- switch-on-sub-corridor-ac [sub-corridors]
  (let [new-state (->> sub-corridors
                    (filter #(false? (:AC %)))
                    first
                    (#(assoc % :AC true)))]
    (update-state sub-corridors new-state)))

(defn- handle-over-power-consumption [state floor sub-corridor-id]
  (let [new-floor-state (update floor :sub-corridors switch-off-sub-corridor-ac sub-corridor-id)]
    (update state :floors update-state new-floor-state)))

(defn handle-under-power-consumption [state floor ]
  (let [new-floor-state (update floor :sub-corridors switch-on-sub-corridor-ac)]
    (update state :floors update-state new-floor-state)))

(defn- manage-floor-power-consumption [state floor-id sub-corridor-id]
  (let [floor (utils/find-by-id (:floors state) floor-id)
        current-power-consumption (compute-floor-power-consumption floor)
        max-power-per-floor (max-power-per-floor floor)]
    (cond
      (= current-power-consumption max-power-per-floor) state
      (> current-power-consumption max-power-per-floor) (handle-over-power-consumption state floor sub-corridor-id)
      (< current-power-consumption max-power-per-floor) (handle-under-power-consumption state floor))))

(defmulti handle-event (fn [_ event] (:type event)))

(defmethod handle-event :motion
  [current-state {:keys [floor-id sub-corridor-id]}]
  (let [floor (utils/find-by-id (:floors current-state) floor-id)
        new-floor-state (update floor :sub-corridors update-sub-corridor-light sub-corridor-id true)
        updated-state (update current-state :floors update-state new-floor-state)]
    (manage-floor-power-consumption updated-state floor-id sub-corridor-id)))

(defmethod handle-event :rest
  [current-state {:keys [floor-id sub-corridor-id]}]
  (let [floor (utils/find-by-id (:floors current-state) floor-id)
        new-floor-state (update floor :sub-corridors update-sub-corridor-light sub-corridor-id false)
        updated-state (update current-state :floors update-state new-floor-state)]
    (manage-floor-power-consumption updated-state floor-id sub-corridor-id)))