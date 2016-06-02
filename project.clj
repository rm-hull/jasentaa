(defproject rm-hull/jasentaa "0.2.0-SNAPSHOT"
  :description "A parser-combinator library in Clojure"
  :url "https://github.com/rm-hull/jacentaa"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]]
  :scm {:url "git@github.com:rm-hull/jasentaa.git"}
  :plugins [
    [lein-codox "0.9.5"] ]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :source-paths ["src"]
    :output-path "doc/api"
    :source-uri "http://github.com/rm-hull/jasentaa/blob/master/{filepath}#L{line}" }
  :min-lein-version "2.6.1"
  :profiles {
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]]}})
