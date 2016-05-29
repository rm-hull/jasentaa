(ns jasentaa.collections-test
  (:require
    [clojure.test :refer :all]
    [jasentaa.collections :refer [join]]))

(deftest check-join
  (is (= [1 2] (join 1 2)))
  (is (= [3 4] (join [3] 4)))
  (is (= [5 6] (join 5 [6])))
  (is (= [7 8] (join [7] [8])))
  (is (= [9]   (join 9 nil)))
  (is (= [0]   (join nil 0)))
  (is (= []    (join nil nil))))
