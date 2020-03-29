(ns hotel-automation.core-test
  (:require [clojure.test :refer :all]
            [hotel-automation.core :refer :all]))

;;assert data
(def default-state {:total-floors 2,
            :total-main-corridors 1,
            :total-sub-corridors 2,
            :floors {:floor0 {:id 1,
                              :main-corridors {:main-corridor0 {:id 1, :ac true, :light true}},
                              :sub-corridors {:sub-corridor0 {:id 1, :ac true, :light false},
                                              :sub-corridor1 {:id 2, :ac true, :light false}}},
                     :floor1 {:id 2,
                              :main-corridors {:main-corridor0 {:id 1, :ac true, :light true}},
                              :sub-corridors {:sub-corridor0 {:id 1, :ac true, :light false},
                                              :sub-corridor1 {:id 2, :ac true, :light false}}}}})

(def motion-event-test-data {:event-type :motion
                             :floor-id 1
                             :sub-corridor-id  2})

(def no-motion-event-test-data {:event-type :rest
                             :floor-id 1
                             :sub-corridor-id  2})

(deftest default-state-test
  (testing "Print the default state of Controller."
    (is (= default-state (set-default-state 2 1 2)))))


(deftest motion-event-test
  (testing "Print the state of the Controller after handling the event."
    (is (get-in (on-controller-event motion-event-test-data)
            [:floors :floor0 :sub-corridors :sub-corridor1 :light]))))

(deftest no-motion-event-test
  (testing "Print the state of the Controller after handling the event."
    (is (not (get-in (on-controller-event no-motion-event-test-data)
                [:floors :floor0 :sub-corridors :sub-corridor1 :light])))))