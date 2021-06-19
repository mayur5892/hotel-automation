(ns hotel-automation.core)

(defn construct-main-corridors [main-corridors]
  (for [id (range 1 (inc main-corridors))]
    {:id id
     :light true
     :AC true}))

(defn construct-sub-corridors [sub-corridors]
  (for [id (range 1 (inc sub-corridors))]
    {:id id
     :light false
     :AC true}))

(defn construct-floor [floors main-corridors sub-corridors]
  (for [floor-id (range 1 (inc floors))]
    {:id floor-id
     :main-corridors (construct-main-corridors main-corridors)
     :sub-corridors (construct-sub-corridors sub-corridors)}))

(defn- boolean->ON-OFF [bool]
  (if bool
    "ON" "OFF"))

(defn- print-corridor [type {:keys [id light AC]}]
  (println  (format "%s corridor %s Light %s: %s AC: %s"
              type id id
              (boolean->ON-OFF light)
              (boolean->ON-OFF AC))))

(defn- print-state [state]
  (doseq [floor (:floors state)]
    (println "Floor " (:id floor))

    (doseq [main-corridor (:main-corridors floor)]
      (print-corridor "Main" main-corridor ))

    (doseq [sub-corridor (:sub-corridors floor)]
      (print-corridor "Sub" sub-corridor))))

(defn construct-initial-state [floors main-corridor-per-floor sub-corridor-per-floor]
  (let [default-state {:floors (construct-floor floors
             main-corridor-per-floor
             sub-corridor-per-floor)}]
    (print-state default-state)
    default-state))
