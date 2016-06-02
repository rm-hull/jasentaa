(ns jasentaa.position)

(defrecord Location [char line col offset])

(defn augment-location
  ([text]
   (augment-location text 1 1 0))

  ([text line col offset]
   (let [ch (first text)]
     (if-not (nil? ch)
       (cons
     (Location. ch line col offset)
     (lazy-seq
       (augment-location
         (rest text)
         (if (= ch \newline) (inc line) line)
         (if (= ch \newline) 1 (inc col))
         (inc offset))))))))

(defn strip-location [augmented-text]
  (apply str (map :char augmented-text)))

(defn parse-exception [location]
  (if (nil? location)
    (java.text.ParseException. (str "Unable to parse empty text") 0)
    (java.text.ParseException.
      (str "Failed to parse text at line: " (:line location) ", col: " (:col location))
      (int (:offset location)))))
