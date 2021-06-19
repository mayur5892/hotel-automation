(ns hotel-automation.core-test
  (:require [clojure.test :refer :all]
            [hotel-automation.core :refer :all]))

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