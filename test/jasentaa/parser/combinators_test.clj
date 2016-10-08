(ns jasentaa.parser.combinators-test
  (:require
   [clojure.test :refer :all]
   [jasentaa.monad :as m]
   [jasentaa.test-helpers :refer :all]
   [jasentaa.parser.basic :refer :all]
   [jasentaa.parser.combinators :refer :all]))

(deftest check-and-then
  (let [parser (and-then (match "a") (match "b"))]
    (is (= [[[\a \b] "el"]] (test-harness parser "abel")))
    (is (= (m/failure)   (test-harness parser "apple")))
    (is (= (m/failure)   (test-harness parser "")))))

(deftest check-or-else
  (let [parser (or-else (match "a") (match "b"))]
    (is (= [[\a "pple"]]  (test-harness parser "apple")))
    (is (= [[\b "anana"]] (test-harness parser "banana")))
    (is (= (m/failure)    (test-harness parser "orange")))))

(deftest check-many
  (let [parser (many (match "a"))]
    (is (= [[\a] ""]          (first (test-harness parser "a"))))
    (is (= [[\a \a \a] "bbb"] (first (test-harness parser "aaabbb"))))
    (is (= [[] nil]           (first (test-harness parser ""))))
    (is (= [[\a] "pple"]      (first (test-harness parser "apple"))))
    (is (= [[] "orange"]      (first (test-harness parser "orange"))))))
