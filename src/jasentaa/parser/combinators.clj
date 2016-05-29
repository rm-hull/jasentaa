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
  "(a|b)

  Non-deterministic choice (++) operator. Applies both parsers
  to the argument string, and appends their list of results."
  [p1 p2]
  (fn [input]
    (lazy-cat (m/bind input p1) (m/bind input p2))))

(declare plus)
(declare optional)

(defn many
  "(a*)

  Parse repeated applications of a parser; the many combinator
  permits zero or more applications of the parser."
  [parser]
  (optional (plus parser)))

(defn plus
  "(a+) equals to (aa*)

  Parse repeated applications of a parser; the plus combinator
  permits one or more applications of the parser."
  [parser]
  (m/do*
    (a <- parser)
    (as <- (many parser))
    (m/return (cons a as))))

(defn optional
  "(a?)"
  [parser]
  (or-else parser (m/return "")))

(defn any-of [& parsers]
  (reduce or-else parsers))

(def space
  "Parse a single space, tab, newline or carriage-return."
  (any-of
    (match " ")
    (match "\t")
    (match "\n")
    (match "\r")))

(def spaces
  "Parse a string of (zero or more) spaces, tabs, and newlines."
  (many space))

(defn string
  "Parse a specific string."
  [input]
  (reduce and-then (map (comp match str) input)))


