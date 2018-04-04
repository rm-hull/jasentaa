(ns jasentaa.position
  (:require
   [clojure.string :as s])
  (:import
   #?(:clj [java.text ParseException]
      :cljs [])))

(defrecord Location [char line col offset full-text])

(defn augment-location
  ([text]
   (augment-location text 1 1 0 text))

  ([text line col offset full-text]
   (let [ch (first text)]
     (if-not (nil? ch)
       (cons
        (Location. ch line col offset full-text)
        (lazy-seq
         (augment-location
          (rest text)
          (if (= ch \newline) (inc line) line)
          (if (= ch \newline) 1 (inc col))
          (inc offset)
          full-text)))))))

(defn strip-location [input]
  (cond
    (not (nil? (:char input)))
    (:char input)

    (seq? input)
    (apply str (map strip-location input))

    :else
    input))

(defn show-error [location]
  (if (and location  (< (:offset location) (count (:full-text location))))
    (let [input   (:full-text location)
          start   (inc (or (s/last-index-of input \newline (:offset location)) -1))
          end     (or (s/index-of input \newline (:offset location)) (count input))
          padding (apply str (repeat (dec (:col location)) " "))]
      (str (subs input start end) \newline padding "^" \newline))))


(defn parse-exception-interop [msg num]
  #?(:clj (ParseException. msg num)
     :cljs (js/Error. msg)))

(defn parse-exception [location]
  (if (nil? location)
    (parse-exception-interop (str "Unable to parse text") 0)
    (parse-exception-interop
     (str
      "Failed to parse text at line: " (:line location) ", col: " (:col location)
      \newline (show-error location))
     (int (:offset location)))))
