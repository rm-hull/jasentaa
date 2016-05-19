(ns jasentaa.parser
  (:require
    [jasentaa.monad :as m :refer [>>=]]))

(defn parse-all
  "Attempts to fully consume the input using the supplied parser.
   Returns nil if the input cannot be fully parsed."
  [parser input]
  (->>
    parser
    (m/bind input)
    (filter (comp empty? second))
    ffirst))

