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

(defn choice
  "(a|b)

  Deterministic choice (+++) operator. Has the same behaviour
  as `or-else`, except that at most one result is returned."
  [p1 p2]
  (fn [input]
    (if-let [[x & xs] (m/bind input (or-else p1 p2))]
      [x]
      [])))

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

(defn token
  "Parse a token using a parser p, throwing away any trailing space."
  [p]
  (m/do*
    (a <- p)
    spaces
    (m/return a)))

(def symb
  "Parse a symbolic token."
  (comp token string))

(defn chain-left
  "Parse repeated applications of a parser p, separated by
  applications of a parser op whose result value is an
  operator that is assumed to associate to the left, and
  which is used to combine the results from the p parsers."
  ([p op a]
  (choice
    (chain-left p op)
    (m/return a)))

  ([p op]
  (m/do*
    (a <- p)
    (rst <- (many
              (m/do*
                (f <- op)
                (b <- p)
                (m/return [f b]))))
    (m/return
      (reduce
        (fn [acc [f b]] (f a b))
        a
        rst)))))
