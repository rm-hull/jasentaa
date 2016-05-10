(ns jasentaa.parser.basic-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.monad :as m]
    [jasentaa.parser.basic :refer :all]))

(deftest check-any
  (is (= [[\a "pple"]] (any "apple")))
  (is (= [[\a ""]]     (any "a")))
  (is (= (m/failure)   (any [])))
  (is (= (m/failure)   (any nil)))
  (is (= (m/failure)   (any ""))))

(deftest check-match
  (is (= [[\a "pple"]] ((match "a") "apple")))
  (is (= [[\a ""]]     ((match "a") "a")))
  (is (= (m/failure)   ((match "a") "banana"))))

(deftest check-none-of
  (is (= [[\b "anana"]] ((none-of "a") "banana")))
  (is (= [[\b ""]]      ((none-of "a") "b")))
  (is (= (m/failure)    ((none-of "b") "banana"))))

(deftest check-from-re
  (is (= [[\a "pple"]]  ((from-re #"[a-z]") "apple")))
  (is (= [[\b "anana"]] ((from-re #"[a-z]") "banana")))
  (is (= [[\p "ear"]]   ((from-re #"[a-z]") "pear")))
  (is (= (m/failure)    ((from-re #"[a-z]") "Tomtato"))))