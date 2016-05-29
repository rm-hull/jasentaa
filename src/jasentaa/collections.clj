(ns jasentaa.collections)

(defn join [a b]
  (cond
    (nil? a)
    (recur [] b)

    (nil? b)
    (recur a [])

    (not (list? a))
    (recur (list a) b)

    (not (list? b))
    (recur a (list b))

    :else
    (concat a b)))
