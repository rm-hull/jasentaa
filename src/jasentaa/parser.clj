(ns jasentaa.parser
  (:refer-clojure :exclude [apply])
  (:require
    [jasentaa.parser.combinators :refer [spaces]]
    [jasentaa.monad :as m :refer [>>=]]
    [jasentaa.position :refer [augment-location]]))

(defn apply
  "Apply a parser, throwing away any leading space:"
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
    (augment-location)
    (apply parser)
    (filter (comp empty? second))
    ffirst))

