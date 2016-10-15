# [Jäsentää](https://translate.google.co.uk/#fi/en/j%C3%A4sent%C3%A4%C3%A4)
[![Build Status](https://travis-ci.org/rm-hull/jasentaa.svg?branch=master)](http://travis-ci.org/rm-hull/jasentaa)
[![Coverage Status](https://coveralls.io/repos/rm-hull/jasentaa/badge.svg?branch=master)](https://coveralls.io/r/rm-hull/jasentaa?branch=master)
[![Dependencies Status](https://jarkeeper.com/rm-hull/jasentaa/status.svg)](https://jarkeeper.com/rm-hull/jasentaa)
[![Downloads](https://jarkeeper.com/rm-hull/jasentaa/downloads.svg)](https://jarkeeper.com/rm-hull/jasentaa)
[![Clojars Project](https://img.shields.io/clojars/v/rm-hull/jasentaa.svg)](https://clojars.org/rm-hull/jasentaa)
[![Maintenance](https://img.shields.io/maintenance/yes/2016.svg?maxAge=2592000)]()

A parser-combinator library in Clojure.

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.6.1 or above installed.

### Building

To build and install the library locally, run:

    $ cd jasentaa
    $ lein test
    $ lein install

### Including in your project

There is a version hosted at [Clojars](https://clojars.org/rm-hull/infix).
For leiningen include a dependency:

```clojure
[rm-hull/jasentaa "0.2.3"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>jasentaa</artifactId>
  <version>0.2.3</version>
</dependency>
```

## API Documentation

See [www.destructuring-bind.org/jasentaa](http://www.destructuring-bind.org/jasentaa/) for API details.

### Breaking changes between versions 0.1.x → 0.2.x

The **0.1.x** line worked on parsing a stream of characters. If the parser
became exhausted, then `parse-all` would return `nil` and no indication
of where the parser failed.

As of **0.2.0**, althought the parser still accepts a stream of characters, it
reprocesses them into a stream of [Location](https://github.com/rm-hull/jasentaa/blob/master/src/jasentaa/position.clj#L3)'s.
If the input cannot be fully parsed, `parse-all` now throws a [ParseException](https://docs.oracle.com/javase/8/docs/api/java/text/ParseException.html#ParseException-java.lang.String-int-),
where the message gives a human-readable location of where the parse failed,
and `ParseException#getErrorOffset` gives the zero-indexed offset to the start
of the unparseable text.

Combinators that previously operated on characters or strings now have to
extract the text using `jasentaa.position/strip-location`, so a previous
0.1.x code example that does:

```clojure
(def single-word
  (m/do*
    (w <- (token (plus alpha-num)))
    (m/return w)))
```

Should be coverted to:

```clojure
(def single-word
  (m/do*
    (w <- (token (plus alpha-num)))
    (m/return (strip-location w))))
```

## Worked Example #1

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

## Worked Example #2

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

## Further examples & implementations

* [ODS Search Appliance](https://github.com/rm-hull/ods-search-appliance) uses
  a similar EBNF grammar for search phrases to the above example. However
  rather than returning a data structure, the parsed result is a composed
  function that takes a trigram inverted-index, and returns a list of matching
  document IDs.

* [Warren's Abstract Machine](https://github.com/rm-hull/wam) is an
  _"in-progress"_ Prolog implementation which uses parser combinators to read
  Prolog programs (questions, facts and rules) before compiling into
  virtual machine instructions.

* [Infix](https://github.com/rm-hull/infix) is a Clojure library that allows
  infix math expressions to be read from a string, and 'compiled' into a
  function definition.

## Attribution

Substantial portions based on:
* https://gist.github.com/kachayev/b5887f66e2985a21a466
* https://pyparsing.wikispaces.com/

## References

* http://www.cs.uwyo.edu/~jlc/courses/3015/parser_pearl.pdf
* http://www.haskellforall.com/2012/10/parsing-chemical-substructures.html
* https://speakerdeck.com/kachayev/monadic-parsing-in-python

## License

### The MIT License (MIT)

Copyright (c) 2016 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
