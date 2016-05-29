(ns jasentaa.parser
  (:refer-clojure :exclude [apply])
  (:require
    [jasentaa.parser.combinators :refer [spaces]]
    [jasentaa.monad :as m :refer [>>=]]))

(defn apply
  [parser input]
  (m/bind
    input
    (m/do*
      spaces
      parser)))

(defn parse-all
  "Attempts to fully consume the input using the supplied parser.
   Returns nil if the input cannot be fully parsed."
  [parser input]
  (->>
    input
    (apply parser)
    (filter (comp empty? second))
    ffirst))

