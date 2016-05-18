(ns jasentaa.worked-example
  (:require
    [clojure.test :refer :all]
    [jasentaa.monad :as m]
    [jasentaa.parser :refer [parse-all]]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]))

; BNF Grammar, based at that described in: 'Getting Started with PyParsing'
; (http://shop.oreilly.com/product/9780596514235.do)
;
; searchExpr ::= searchAnd [ OR searchAnd ]...
; searchAnd  ::= searchTerm [ AND searchTerm ]...
; searchTerm ::= [NOT] ( singleWord | quotedString | '(' searchExpr ')' )

(def digit (from-re #"[0-9]"))
(def letter (from-re #"[a-z]"))
(def alpha-num (any-of letter digit))

(declare search-expr)

(def single-word
  (m/do*
    spaces
    (word <- (plus alpha-num))
    spaces
    (m/return (apply str word))))

(def quoted-string
  (m/do*
    spaces
    (match "\"")
    (text <- (plus (any-of digit letter (match " "))))
    (match "\"")
    spaces
    (m/return (apply str text))))

(def bracketed-expr
  (m/do*
    (match "(")
    spaces
    (expr <- search-expr)
    spaces
    (match ")")
    (m/return expr)))

(def search-term
  (m/do*
    (neg <- (optional (and-then (string "not") spaces)))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (m/return (if (empty? neg) term (list :NOT term)))))

(def search-and
  (m/do*
    (fst <- search-term)
    (rst <- (many (m/do* (plus space) (string "and") (plus space) search-term)))
    (m/return (if (empty? rst) fst (cons :AND (cons fst rst))))))

(def search-expr
  (m/do*
    (fst <- search-and)
    (rst <- (many (m/do* (plus space) (string "or") (plus space) search-and)))
    (m/return (if (empty? rst) fst (cons :OR (cons fst rst))))))

(deftest check-grammar
  (is (= [:OR [:AND "wood" "blue"] "red"]
         (parse-all search-expr "wood and blue or red")))

  (is (= [:AND "wood" [:OR "blue" "red"]]
        (parse-all search-expr "wood and (blue or red)")))

  (is (= [:AND [:OR "steel" "iron"] "lime green"]
        (parse-all search-expr "(steel or iron) and \"lime green\"")))

  (is (= [:OR [:NOT "steel"] [:AND "iron" "lime green"]]
        (parse-all search-expr "not steel or iron and \"lime green\"")))

  (is (= [:AND [:NOT [:OR  "steel" "iron"]] "lime green"]
        (parse-all search-expr "not(steel or iron) and \"lime green\""))))
