# Worked Example #2

The previous example yielded a resulting data structure which corresponded to the
parsed input. There is no reason why the result cannot be evaluated as part of the
parsing process. **Graham Hutton** and **Erik Meijer** presented a simple integer
calculator in [Functional Pearls: _Monadic Parsing in Haskell_](http://www.cs.uwyo.edu/~jlc/courses/3015/parser_pearl.pdf)
which does exactly this.

Considering a standard grammar for arithmetic expressions built up from single digits
using the operators +, -, * and /, together with parentheses:

* _**expr** ::= expr addop term | term_
* _**term** ::= term mulop factor | factor_
* _**factor** ::= digit | ( expr )_
* _**digit** ::= 0 | 1 | ... | 9_
* _**addop** ::= + | -_
* _**mulop** ::= * | /_

As per the _Haskell_ implementation, we need to forward declare
the _expr_ parser:

```clojure
(ns jasentaa.worked-example-2
  (:require
    [jasentaa.monad :as m]
    [jasentaa.position :refer :all])
    [jasentaa.parser :as p]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]))

(declare expr)
```

The _digit_ parser follows the exact same implementation as the Haskell
example; A check is made to see if the current input satisfies the `digit?`
predicate, and the returned value is calculated from the ordinal value of the
character minus zero's ordinal.

```clojure
(defn- digit? [^Character c]
  (Character/isDigit c))

(def digit
  (m/do*
    (x <- (token (sat digit?)))
    (m/return (- (byte (strip-location x)) (byte \0)))))
```

_factor_ is either a single digit or a bracketed-expression:

```clojure
(def factor
  (choice
    digit
    (m/do*
      (symb "(")
      (n <- (fwd expr))
      (symb ")")
      (m/return n))))
```

_addop_ and _mulop_ yield a choice of the core function for +, -, * and /
respectively. _term_ and _expr_ are then simple `chain-left` applications
as per the declared grammar:

```clojure
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
```

Testing the example expression yields the expected result:

```clojure
(take 1 (p/apply expr " 1 - 2 * 3 + 4 "))
; => ([-1, ()])
; i.e. (+ 4 (- 1 (* 2 3)))
```

`chain-left` associates from the left, so this expression evaluates as _((1 - (2 * 3)) + 4)_.
`chain-right` associates from the right, so substituting that would evaluate as _(1 - ((2 * 3) + 4))_,
resulting in -9. Clearly, in both cases, multiplcation binds before addition.

```clojure
(def term'
  (chain-right factor mulop))

(def expr'
  (chain-right term addop))

(take 1 (p/apply expr' " 1 - 2 * 3 + 4 "))
; => ([-9, ()])
; i.e. (- 1 (+ 4 (* 2 3)))
```

_I can't immediately think of a scenarion where `chain-right` would be used
over `chain-left` - postfix notation perhaps? - but other than that..._

This example is also encapsulated as another [test](https://github.com/rm-hull/jasentaa/blob/master/test/jasentaa/worked_example_2.clj).
