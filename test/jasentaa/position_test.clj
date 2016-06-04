(ns jasentaa.position-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.position :refer :all])
  (:import
    [jasentaa.position Location]))

(deftest check-augment-then-strip
  (is (= "the quick brown fox"
     (strip-location
       (augment-location
         "the quick brown fox")))))

(deftest check-augment-location-plain-string
  (is (nil? (augment-location "")))
  (is (= (augment-location "Hello\nWorld!")
     (list
       (Location. \H 1 1 0)
       (Location. \e 1 2 1)
       (Location. \l 1 3 2)
       (Location. \l 1 4 3)
       (Location. \o 1 5 4)
       (Location. \newline 1 6 5)
       (Location. \W 2 1 6)
       (Location. \o 2 2 7)
       (Location. \r 2 3 8)
       (Location. \l 2 4 9)
       (Location. \d 2 5 10)
       (Location. \! 2 6 11)))))

(deftest check-strip-location
  (is (= \h (strip-location (Location. \h 1 1 0))))
  (is (= nil (strip-location nil)))
  (is (= "Hello" (strip-location "Hello"))))

(deftest check-exception
  (is (thrown-with-msg? java.text.ParseException
    #"Unable to parse empty text"
    (throw (parse-exception nil))))
  (is (thrown-with-msg? java.text.ParseException
    #"Failed to parse text at line: 6, col: 31"
    (throw (parse-exception (Location. \Y 6 31 321))))))
