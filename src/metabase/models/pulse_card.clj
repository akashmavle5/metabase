(ns metabase.models.pulse-card
  (:require [metabase.util :as u]
            [metabase.util.schema :as su]
            [schema.core :as s]
            [toucan
             [db :as db]
             [models :as models]]))

(models/defmodel PulseCard :pulse_card)

(defn- next-position-for
  [pulse-id]
  (->> (db/select PulseCard :pulse_id pulse-id)
       (map :position)
       (apply max)
       inc))

(def ^:private NewPulseCard
  {:card_id                      su/IntGreaterThanZero
   :pulse_id                     su/IntGreaterThanZero
   :dashboard_card_id            su/IntGreaterThanZero
   (s/optional-key :position)    (s/maybe s/Int)
   (s/optional-key :include_csv) (s/maybe s/Bool)
   (s/optional-key :include_xls) (s/maybe s/Bool)})

(s/defn create!
  "Creates a new PulseCard, joining the given card, pulse, and dashboard card and setting appropriate defaults for other
  values if they're not provided."
  [new-pulse-card :- NewPulseCard]
  (let [{:keys [card_id pulse_id dashboard_card_id position include_csv include_xls]} new-pulse-card]
    (db/insert! PulseCard
      :card_id           card_id
      :pulse_id          pulse_id
      :dashboard_card_id dashboard_card_id
      :position          (u/or-with some? position (next-position-for pulse_id))
      :include_csv       (u/or-with some? include_csv false)
      :include_xls       (u/or-with some? include_xls false))))
