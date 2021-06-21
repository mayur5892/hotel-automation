(ns hotel-automation.utils)


(defn- boolean->ON-OFF [bool]
  (if bool
    "ON" "OFF"))

(defn- print-corridor [type {:keys [id light AC]}]
  (println  (format "%s corridor %s Light %s: %s AC: %s"
              type id id
              (boolean->ON-OFF light)
              (boolean->ON-OFF AC))))

(defn print-state [state]
  (doseq [floor (:floors state)]
    (println "Floor " (:id floor))

    (doseq [main-corridor (:main-corridors floor)]
      (print-corridor "Main" main-corridor ))

    (doseq [sub-corridor (:sub-corridors floor)]
      (print-corridor "Sub" sub-corridor))))

(defn find-by-id [state id]
  (first (filter #(= id (:id %)) state)))