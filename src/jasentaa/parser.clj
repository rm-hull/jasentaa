(ns jasentaa.parser
  (:require
    [jasentaa.monad :as m :refer [>>=]]
    [jasentaa.position :refer [emit]]))

(defn parse-all
  "Attempts to fully consume the input using the supplied parser.
   Returns nil if the input cannot be fully parsed."
  [parser input]
  (->>
    parser
    (m/bind (emit input))
    (filter #(= "" (second %)))
    ffirst))

