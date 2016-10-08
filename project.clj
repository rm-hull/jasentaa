(defproject rm-hull/jasentaa "0.2.4-SNAPSHOT"
  :description "A parser-combinator library in Clojure"
  :url "https://github.com/rm-hull/jacentaa"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :scm {:url "git@github.com:rm-hull/jasentaa.git"}
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
        [lein-codox "0.10.0"]
        [lein-cloverage "1.0.7"]
        [lein-cljfmt "0.5.6"]]
      :dependencies [
        [org.clojure/clojure "1.8.0"]]}})
