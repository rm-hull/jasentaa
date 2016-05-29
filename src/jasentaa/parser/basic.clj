(ns jasentaa.parser.basic
  (:require
    [jasentaa.monad :as m :refer [>>=]]))

(defn any [input]
  (if (empty? input)
    (m/failure input)
    (list [(first input) (subs input 1)])))

(defn sat
  "A basic combinator that takes a predicate, and yields a parser that
  consumes a single character if it satisfies the predicate, and fails
  otherwise."
  [pred]
  (>>= any (fn [v]
             (if (pred v)
               (m/return v)
               m/failure))))

(defn char-cmp
  "Does a character comparison using a specific function"
  [f]
  (fn [c] (sat (partial f (first c)))))

(def match
  "Recognises a given char"
  (char-cmp =))

(def none-of
  "Rejects a given char"
  (char-cmp not=))

(defn from-re [re]
  (sat (fn [v]
         (not (nil? (re-find re (str v)))))))

(defmacro fwd
  "Delays the evaluation of a parser that was forward (declare)d and
   it has not been defined yet. For use in (def)s of no-arg parsers,
   since the parser expression evaluates immediately."
  [p]
  (let [x (gensym)]
    `(fn [~x] (~p ~x))))
