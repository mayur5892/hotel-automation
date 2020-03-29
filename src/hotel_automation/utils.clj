(ns hotel-automation.utils)

(defn state-key [type id]
  (keyword (str type id)))

(defn particular-state-by-id [state type id]
  (let [item-key (state-key type id)]
    (item-key state)))

(defn encode [field]
  (if field
    "ON" "OFF"))

(defn- print-main-corridors [main-corridors main-corridor-count]
  (dotimes[i main-corridor-count]
    (let [{:keys [id light ac]} (particular-state-by-id main-corridors "main-corridor" i)]
      (println "Main corridor " id " Light " id ":" (encode light) " AC : " (encode ac)))))

(defn- print-sub-corridors [sub-corridors sub-corridor-count]
  (dotimes[i sub-corridor-count]
    (let [{:keys [id light ac]} (particular-state-by-id sub-corridors "sub-corridor" i)]
      (println "Sub corridor " id " Light " id ":" (encode light) " AC : " (encode ac)))))

(defn print-current-state [{:keys [ floors total-floors total-main-corridors total-sub-corridors]}]
  (dotimes[i total-floors]
    (let [{:keys [id main-corridors sub-corridors]} (particular-state-by-id floors "floor" i)]
      (println "            Floor " id)
      (print-main-corridors main-corridors total-main-corridors)
      (print-sub-corridors sub-corridors total-sub-corridors))))

