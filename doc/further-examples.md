# Further examples & implementations

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
