(ns jasentaa.test-helpers
  (:require
    [jasentaa.monad :as m]
    [jasentaa.position :refer :all]))

(defn test-harness [parser input]
  (let [result (first (parser (augment-location input)))]
    (if (empty? result)
      (m/failure)
      (list [
    (if (char? (-> result first :char))
      (-> result first :char)
      (mapv :char (first result)))
    (strip-location (fnext result))]))))