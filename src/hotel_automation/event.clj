(ns hotel-automation.event)


(defn update-state [state id new-state]
  (->> state
    (remove #(= id (:id %)))
    (cons new-state)))

(defn update-sub-corridor [sub-corridors id]
  (let [sub-corridor (first (filter #(= id (:id %))  sub-corridors))
        new-state (assoc sub-corridor :light true)]
    (update-state sub-corridors id new-state)))

(defmulti handle-event (fn [_ event] (:type event)))

(defmethod handle-event :motion
  [current-state {:keys [floor-id sub-corridor-id]}]
  (prn current-state)
  (let [floor (first (filter #(= floor-id (:id %)) (:floors current-state)))
        new-floor-state (update floor :sub-corridors update-sub-corridor sub-corridor-id)]
    (update current-state :floors update-state floor-id new-floor-state)))

(defmethod handle-event :rest
  [current-state event]
  )