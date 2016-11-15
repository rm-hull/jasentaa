# Worked Example #1

In [Getting Started with PyParsing](http://shop.oreilly.com/product/9780596514235.do),
**Paul McGuire** describes an example search string interface, with support for
AND, OR, and NOT keyword qualifiers, and gives examples of some typical search
phrases one might use in a search engine:

    wood and blue or red
    wood and (blue or red)
    (steel or iron) and "lime green"
    not steel or iron and "lime green"
    not(steel or iron) and "lime green"

The article then goes on to build up python code that returns the parsed
results in a hierarchical structure based on the precedence of operations among
the AND, OR, and NOT quantifiers, where NOT has the highest precendence and is
evaluated first, with AND next highest precedence, while OR is the lowest and
evaluated last.

Expressing this in BNF, we have the following rules:

* _**searchExpr** ::= searchAnd [ OR searchAnd ]..._
* _**searchAnd** ::= searchTerm [ AND searchTerm ]..._
* _**searchTerm** ::= \[NOT\] ( singleWord | quotedString | '(' searchExpr ')' )_

Following the _PyParsing_ implementation, we can build up the
parsers in Clojure starting with:

```clojure
(ns jasentaa.worked-example-1
  (:require
    [jasentaa.monad :as m]
    [jasentaa.position :refer [strip-location]]
    [jasentaa.parser :refer [parse-all]]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]))

(def digit (from-re #"[0-9]"))
(def letter (from-re #"[a-z]"))
(def alpha-num (any-of letter digit))
```

which just defines some basic character parsers; then, we use these to build up
parsers for _singleWord_, _quotedString_ and bracketed expressions.

```clojure
(declare search-expr)

(def single-word
  (m/do*
    (w <- (token (plus alpha-num)))
    (m/return (strip-location w))))

(def quoted-string
  (m/do*
    (symb "\"")
    (t <- (plus (any-of digit letter (match " "))))
    (symb "\"")
    (m/return (strip-location t))))

(def bracketed-expr
  (m/do*
    (symb "(")
    (expr <- (token search-expr))
    (symb ")")
    (m/return expr)))
```

(Note how it is necessary to forward declare `search-expr`)

Next, a _searchTerm_ parser is composed from the three prior parsers. The
returned value is wrapped with a `:NOT` keyword as necessary:

```clojure
(def search-term
  (m/do*
    (neg <- (optional (symb "not")))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (m/return (if (empty? neg) term (list :NOT term)))))
```

Finally the _searchAnd_ and _searchExpr_ parsers are implemented in terms
of the earlier definitions:

```clojure
(def search-and
  (m/do*
    (lst <- (separated-by search-term (symb "and")))
    (m/return (if (= (count lst) 1)
                (first lst)
                (cons :AND lst)))))

(def search-expr
  (m/do*
    (lst <- (separated-by search-and (symb "or")))
    (m/return (if (= (count lst) 1)
                (first lst)
                (cons :OR lst)))))
```

Notice how the returned values are (purposely) constructed in prefix notation,
whereas the _Getting Started with PyParsing_ examples are returned infix.
Prefix notation is (obviously) more LISPy, and as well as being consistent with
the host language, this makes the resulting aborescent structures simpler to
handle as well.

Testing the parsers for the given examples:

```clojure
(parse-all search-expr "wood and blue or red")
; => (:OR (:AND "wood" "blue") "red")

(parse-all search-expr "wood and (blue or red)")
; => (:AND "wood" (:OR "blue" "red"))

(parse-all search-expr "(steel or iron) and \"lime green\"")
; => (:AND (:OR "steel" "iron") "lime green")

(parse-all search-expr "not steel or iron and \"lime green\"")
; => (:OR (:NOT "steel") (:AND "iron" "lime green"))

(parse-all search-expr "not(steel or iron) and \"lime green\"")
; => (:AND (:NOT (:OR "steel" "iron")) "lime green")
```

This example is encapsulated as a [test](https://github.com/rm-hull/jasentaa/blob/master/test/jasentaa/worked_example_1.clj).
