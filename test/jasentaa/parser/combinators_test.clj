(ns jasentaa.parser.combinators-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.monad :as m]
    [jasentaa.position :refer [emit]]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]))

(deftest check-and-then
  (let [parser (and-then (match "a") (match "b"))]
    (is (= [["ab" "el"]] (parser "abel")))
    (is (= (m/failure)   (parser "apple")))
    (is (= (m/failure)   (parser "")))))

(deftest check-or-else
  (let [parser (or-else (match "a") (match "b"))]
    (is (= [[\a "pple"]]  (parser "apple")))
    (is (= [[\b "anana"]] (parser "banana")))
    (is (= (m/failure)    (parser "orange")))))

(deftest check-many
  (let [parser (many (match "a"))]
    (is (= [[\a] ""]          (first (parser "a"))))
    (is (= [[\a \a \a] "bbb"] (first (parser "aaabbb"))))
    (is (= ["" ""]            (first (parser ""))))
    (is (= [[\a] "pple"]      (first (parser "apple"))))
    (is (= ["" "orange"]      (first (parser "orange"))))))
