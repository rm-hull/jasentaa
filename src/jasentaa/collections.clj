(ns jasentaa.collections)

(defn join [a b]
  (cond
    (nil? a)
    (recur [] b)

    (nil? b)
    (recur a [])

    (not (coll? a))
    (recur (list a) b)

    (not (coll? b))
    (recur a (list b))


    :else
    (concat a b)))

(join [1] 2)
(join 1 [2])
(join 1 2)
(join [1] [2])
(join nil 3)
(join 3 nil)
(join nil 3)


(coll? nil)
