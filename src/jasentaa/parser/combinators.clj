(ns jasentaa.parser.combinators
  (:require
    [jasentaa.parser.basic :refer [match]]
    [jasentaa.monad :as m :refer [>>=]]))

(defn and-then
  "(ab)"
  [p1 p2]
  (m/do*
    (r1 <- p1)
    (r2 <- p2)
    (m/return (str r1 r2))))

(defn or-else
  "(a|b)"
  [p1 p2]
  (fn [input]
    (lazy-cat (m/bind input p1) (m/bind input p2))))

(declare plus)
(declare optional)

(defn many
  "(a*)"
  [parser]
  (optional (plus parser)))

(defn plus
  "(a+) equals to (aa*)"
  [parser]
  (m/do*
    (a <- parser)
    (as <- (many parser))
    (m/return (cons a as))))

(defn optional
  "(a?)"
  [parser]
  (or-else parser (m/return "")))

(def space
  (or-else
    (match " ")
    (match "\t")))

(def spaces
  (many space))

(defn any-of [& parsers]
  (reduce or-else parsers))

(defn string [s]
  (reduce and-then (map #(match (str %)) s)))

