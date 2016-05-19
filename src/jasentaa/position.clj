(ns jasentaa.position)

(defn augment-location
  ([text]
   (augment-location text 1 1))

  ([text line col]
   (let [ch (first text)]
     (if-not (nil? ch)
       (cons
         [ch line col]
         (lazy-seq
           (augment-location
             (rest text)
             (if (= ch \newline) (inc line) line)
             (if (= ch \newline) 1 (inc col)))))))))

