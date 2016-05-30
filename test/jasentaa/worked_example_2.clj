(ns jasentaa.worked-example-2
  (:require
    [clojure.test :refer :all]
    [jasentaa.monad :as m]
    [jasentaa.parser :as p]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]))

; BNF Grammar, based at that described in: 'FUNCTIONAL PEARLS: Monadic Parsing in Haskell'
; (http://www.cs.uwyo.edu/~jlc/courses/3015/parser_pearl.pdf)
;
;    expr   ::= expr addop term | term
;    term   ::= term mulop factor | factor
;    factor ::= digit | ( expr )
;    digit  ::= 0 | 1 | . . . | 9
;
;    addop  ::= + | -
;    mulop  ::= * | /

(declare expr)

(defn- digit? [^Character c]
  (Character/isDigit c))

(def digit
  (m/do*
    (x <- (token (sat digit?)))
    (m/return (- (byte x) (byte \0)))))

(def factor
  (choice
    digit
    (m/do*
      (symb "(")
      (n <- (fwd expr))
      (symb ")")
      (m/return n))))

(def addop
  (choice
    (m/do*
      (symb "+")
      (m/return +))
    (m/do*
      (symb "-")
      (m/return -))))

(def mulop
  (choice
    (m/do*
      (symb "*")
      (m/return *))
    (m/do*
      (symb "/")
      (m/return /))))

(def term
  (chain-left factor mulop))

(def expr
  (chain-left term addop))

(deftest check-evaluate-expr
  (is (= [[-1 ""]] (take 1 (p/apply expr " 1 - 2 * 3 + 4 ")))))

