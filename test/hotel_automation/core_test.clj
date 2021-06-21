(ns hotel-automation.core-test
  (:require [clojure.test :refer :all]
            [hotel-automation.core :refer :all]
            [hotel-automation.hotel :refer :all]
            [hotel-automation.event :refer :all]))

(deftest initial-state-setup-test
  (testing "should return valid initial state"
    (let [floors 2
          main-corridor-per-floor 1
          sub-corridor-per-floor 2
          result (construct-initial-state floors main-corridor-per-floor sub-corridor-per-floor)
          first-floor (first (:floors result))
          main-corridors (:main-corridors first-floor)
          sub-corridors (:sub-corridors first-floor)
          ]
      (is (= 2 (count (:floors result))))
      (is (= 1 (count main-corridors)))
      (is (= 2 (count sub-corridors)))

      (is (= {:id 1 :light true :AC true} (first main-corridors)))
      (is (= {:id 1 :light false :AC true} (first sub-corridors)))
      (is (= {:id 2 :light false :AC true} (second sub-corridors))))))

(deftest motion-detected-in-sub-corridor-test
  (testing "should return valid state when motion is detected"
    (let [current-state (construct-initial-state 2 1 2)
          floor-id 1
          sub-corridor-id 2
          motion-event {:floor-id floor-id
                        :sub-corridor-id sub-corridor-id
                        :type :motion}
          result (process-event current-state motion-event)
          floor (first (filter #(= floor-id (:id %)) (:floors result)))
          sub-corridor2 (first (filter #(= sub-corridor-id (:id %)) (:sub-corridors floor)))
          sub-corridor1 (first (filter #(= 1 (:id %)) (:sub-corridors floor)))]

      (is (true? (:light sub-corridor2)))
      (is (true? (:AC sub-corridor2)))

      (is (false? (:AC sub-corridor1))))))

(deftest power-consumption-test
  (testing "should return total power consumed by the floor"
    (let [floor (first (construct-floor 1 1 2))]
      (is (= 35 (compute-floor-power-consumption floor))))))