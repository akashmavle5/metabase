(ns metabase.models.pulse-card-test
  (:require [clojure.test :refer :all]
            [metabase.models
             [pulse :refer [Pulse]]
             [pulse-card :refer :all]]
            [toucan.util.test :as tt]))

(deftest test-next-position-for
  (testing "No existing cards"
    (tt/with-temp Pulse [{pulse-id :id}]
      (is (zero? (next-position-for pulse-id)))))
  (testing "With cards"
    (tt/with-temp* [Pulse [{pulse-id :id}]
                    PulseCard [_ {:pulse_id pulse-id
                                  :card_id  1
                                  :position 2}]]
      (is (= 3 (next-position-for pulse-id))))))
