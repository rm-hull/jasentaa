(ns jasentaa.worked-example-2
  (:require
   [clojure.test :refer :all]
   [jasentaa.monad :as m]
   [jasentaa.parser :as p]
   [jasentaa.position :refer [strip-location]]
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
   (m/return (- (byte (strip-location x)) (byte \0)))))

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
  (let [expected (+ 4 (- 1 (* 2 3)))]  ; => -1
    (is (= [[expected ()]] (take 1 (p/apply expr " 1 - 2 * 3 + 4 "))))))

;; Now use chain-right:
(def term'
  (chain-right factor mulop))

(def expr'
  (chain-right term addop))

(deftest check-evaluate-expr'
  (let [expected (- 1 (+ 4 (* 2 3)))]
    (is (= [[expected ()]] (take 1 (p/apply expr' " 1 - 2 * 3 + 4 "))))))
