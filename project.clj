(defproject rm-hull/jasentaa "0.0.1-SNAPSHOT"
  :description "A parser-combinator library in Clojure"
  :url "https://github.com/rm-hull/jacentaa"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]]
  :scm {:url "git@github.com:rm-hull/jasentaa.git"}
  :plugins [
    [codox "0.9.4"] ]
  :source-paths ["src"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/jasentaa/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.6.1"
  :profiles {
    :dev {
      :global-vars {*warn-on-reflection* true}
      :plugins [
        [lein-cloverage "1.0.6"]]}})
