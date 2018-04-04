(defproject rm-hull/jasentaa "0.2.4"
  :description "A parser-combinator library in Clojure"
  :url "https://github.com/rm-hull/jacentaa"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :scm {:url "https://github.com/rm-hull/jasentaa.git"}
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :jar-exclusions [#"(?:^|/).git"]
  :codox {
    :source-paths ["src/clj" "src/cljc" "src/cljs"]
    :output-path "doc/api"
    :doc-files [
      "doc/worked-example-1.md"
      "doc/worked-example-2.md"
      "doc/further-examples.md"
      "doc/references.md"
      "LICENSE.md"]
    :source-uri "http://github.com/rm-hull/jasentaa/blob/master/{filepath}#L{line}"
    :themes [:default [:google-analytics {:tracking-code "UA-39680853-9" }]]}
  :min-lein-version "2.6.1"
  :profiles {
             :dev {
                   :global-vars {*warn-on-reflection* true}
                   :plugins [[lein-doo "0.1.10"]
                             [lein-codox "0.10.3"]
                             [lein-cloverage "1.0.10"]
                             [lein-cljfmt "0.5.7"]]
                   :dependencies [
                                  [org.clojure/clojure "1.9.0"]
                                  [org.clojure/clojurescript "1.10.238"]
                                  [google-analytics-codox-theme "0.1.0"]]}}
  :aliases {"test" ["do" "clean," "test,"
                    "doo" "rhino" "test" "once"]}
  :doo {:paths {:rhino "lein run -m org.mozilla.javascript.tools.shell.Main"}}

  :cljsbuild {:builds
              [{:id "test"
                :source-paths ["src" "test"]
                :compiler {:output-to "target/unit-test.js"
                           :optimizations :whitespace}}]})
