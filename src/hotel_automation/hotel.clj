(ns hotel-automation.hotel)


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