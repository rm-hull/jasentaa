(ns jasentaa.parser.basic-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.test-helpers :refer :all]
    [jasentaa.monad :as m]
    [jasentaa.position :refer :all]
    [jasentaa.parser.basic :refer :all]))

(deftest check-any
  (is (= [[\a "pple"]] (test-harness any "apple")))
  (is (= [[\a ""]]     (test-harness any "a")))
  (is (= (m/failure)   (test-harness any [])))
  (is (= (m/failure)   (test-harness any nil)))
  (is (= (m/failure)   (test-harness any ""))))

(deftest check-match
  (is (= [[\a "pple"]] (test-harness (match "a") "apple")))
  (is (= [[\a ""]]     (test-harness (match "a") "a")))
  (is (= (m/failure)   (test-harness (match "a") "banana"))))

(deftest check-none-of
  (is (= [[\b "anana"]] (test-harness (none-of "a") "banana")))
  (is (= [[\b ""]]      (test-harness (none-of "a") "b")))
  (is (= (m/failure)    (test-harness (none-of "b") "banana"))))

(deftest check-from-re
  (is (= [[\a "pple"]]  (test-harness (from-re #"[a-z]") "apple")))
  (is (= [[\b "anana"]] (test-harness (from-re #"[a-z]") "banana")))
  (is (= [[\p "ear"]]   (test-harness (from-re #"[a-z]") "pear")))
  (is (= (m/failure)    (test-harness (from-re #"[a-z]") "Tomtato"))))
