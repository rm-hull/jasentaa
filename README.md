# [Jäsentää](https://translate.google.co.uk/#fi/en/j%C3%A4sent%C3%A4%C3%A4) [![Build Status](https://travis-ci.org/rm-hull/jasentaa.svg?branch=master)](http://travis-ci.org/rm-hull/jasentaa) [![Coverage Status](https://coveralls.io/repos/rm-hull/jasentaa/badge.svg?branch=master)](https://coveralls.io/r/rm-hull/jasentaa?branch=master) [![Dependencies Status](https://jarkeeper.com/rm-hull/jasentaa/status.svg)](https://jarkeeper.com/rm-hull/jasentaa) [![Downloads](https://jarkeeper.com/rm-hull/jasentaa/downloads.svg)](https://jarkeeper.com/rm-hull/jasentaa) [![Clojars Project](https://img.shields.io/clojars/v/rm-hull/jasentaa.svg)](https://clojars.org/rm-hull/jasentaa)

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
[rm-hull/jasentaa "0.1.0"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>jasentaa</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Worked Example

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

* _**searchTerm** ::= [NOT] ( singleWord | quotedString | '(' searchExpr ')' )_

Following the _PyParsing_ implementation, we can build up the
parsers in Clojure starting with:

```clojure
(ns jasentaa.worked-example
  (:require
    [clojure.test :refer :all]
    [jasentaa.monad :as m]
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
```

(Note how it is necessary to forward declare `search-expr`)

Next, a _searchTerm_ parser is composed from the three prior parsers. The
returned value is wrapped with a `:NOT` keyword as necessary:

```clojure
(def search-term
  (m/do*
    (neg <- (optional (and-then (string "not") spaces)))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (m/return (if (empty? neg) term (list :NOT term)))))
```

Finally the _searchAnd_ and _searchExpr_ parsers are implemented in terms
of the earlier definitions:

```clojure
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

This example is encapsulated as a [test](https://github.com/rm-hull/jasentaa/blob/master/test/jasentaa/working_example.clj).

## Further examples

* [ODS Search Appliance](https://github.com/rm-hull/ods-search-appliance) uses
  a similar EBNF grammar for search phrases to the above example. However
  rather than returning a data structure, the parsed result is a composed
  function that takes a trigram inverted-index, and returns a list of matching
  document IDs.

* [Warren's Abstract Machine](https://github.com/rm-hull/wam) is an
  _"in-progress"_ Prolog implementation which uses parser combinators to read
  Proglog programs (questions, facts and rules) before compiling into
  WAM instructions.

* [Infix](https://github.com/rm-hull/infix) is a Clojure library that allows
  infix math expressions to be read from a string, and 'compiled' into a
  function definition.

## Attribution

Substantial portions based on:
* https://gist.github.com/kachayev/b5887f66e2985a21a466
* https://pyparsing.wikispaces.com/

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
