(ns hotel-automation.core-test
  (:require [clojure.test :refer :all]
            [hotel-automation.core :refer :all]))

(deftest initial-state-setup-test
  (testing "should return valid initial state"
    (let [floors 2
          main-corridor-per-floor 1
          sub-corridor-per-floor 2
          expected-state {:floors [{:id 1
                                   :main-corridors [{:id 1
                                                     :light true
                                                     :AC true}]
                                    :sub-corridors [{:id 1
                                                     :light false
                                                     :AC true}
                                                    {:id 2
                                                     :light false
                                                     :AC true}]}
                                   {:id 2
                                    :main-corridors [{:id 1
                                                      :light true
                                                      :AC true}]
                                    :sub-corridors [{:id 1
                                                     :light false
                                                     :AC true}
                                                    {:id 2
                                                     :light false
                                                     :AC true}]}]}
          result (construct-initial-state floors main-corridor-per-floor sub-corridor-per-floor)]
      (is (= 2 (count (:floor result)))))))