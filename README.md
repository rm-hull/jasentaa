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

## Basic Usage

> TODO

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
