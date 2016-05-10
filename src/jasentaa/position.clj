(ns jasentaa.position)

(defn emit
  ([text]
   (emit text 1 1))

  ([text line col]
   (let [ch (first text)]
     (if-not (nil? ch)
       (cons
         {:char ch :line line :col col}
         (lazy-seq
           (emit
             (rest text)
             (if (= ch \newline) (inc line) line)
             (if (= ch \newline) 1 (inc col)))))))))

(emit "Hello\nWorld")
