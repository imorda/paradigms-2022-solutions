(defn vect? [& args]
  (and (every? vector? args)
       (every? true? (map #(every? number? %) args))))

(defn same_len? [& args]
  (apply == (mapv count args)))

(defn no_zero_v? [& args]
  (every? false? (map #(contains? 0 %) args)))

(defn matrix? [& args]
  (and (every? vector? args)
       (every? true? (map #(and (apply vect? %)
                                (apply same_len? %)) args))))

; :NOTE: Упростить
(defn descending? [v]
  {:pre [(vect? v)]}
  (letfn [(descending' [current, other]
            (if (== (count other) 0)
              true
              (if (== (- current (first other)) 1)
                (recur (first other) (rest other))
                false)))]
    (descending' (first v) (rest v))))

(defn simplex_n [arg]
  {:post [(not (nil? %)) (> % 0)]}
  (if (vect? arg)
    (count arg)
    (let [inner_n (mapv simplex_n arg)] (if
                                          (and (descending? inner_n) (== 1 (last inner_n)))
                                          (first inner_n)))))

(defn vect_eval [op & args]
  {:pre [(apply vect? args) (apply same_len? args)]
   :post [(vect? %) (same_len? (first args) %)]}
  (apply mapv op args))

; :NOTE: Упростить
(defn v+ [& args]
  (apply vect_eval + args))

(defn v- [& args]
  (apply vect_eval - args))

(defn v* [& args]
  (apply vect_eval * args))

(defn vd [& args]
  (apply vect_eval / args))

(defn scalar [& args]
  {:post [(number? %)]}
  (reduce + (apply v* args)))

(defn vect [& args]
  {:pre [(apply vect? args) (apply == 3 (mapv count args))]
   :post [(vect? %) (== 3 (count %))]}
  (reduce #(vector
             (- (* (nth %1 1) (nth %2 2)) (* (nth %2 1) (nth %1 2)))
             (- (* (nth %1 2) (nth %2 0)) (* (nth %2 2) (nth %1 0)))
             (- (* (nth %1 0) (nth %2 1)) (* (nth %2 0) (nth %1 1)))) args))

(defn v*s [v, & scalars]
  {:pre [(vect? v) (every? number? scalars)]
   :post [(vect? %) (same_len? % v)]}
; :NOTE: 0 скаляров
  (mapv (partial * (reduce * scalars)) v))

(defn transpose [m]
  {:pre [(matrix? m)]
   :post [(or (== 0 (count (first m)) (count %))
              (and (matrix? %)
                   (same_len? % (first m))
                   (same_len? (first %) m)))]
   }
  (apply mapv vector m))

(defn m*m [& args]
  {:post [(matrix? %)]}
  (reduce (fn [a, b]
            {:pre [(matrix? a b) (same_len? (first a) b)]
             :post [(matrix? %) (same_len? % a) (same_len? (first %) (first b))]}
            (mapv #(mapv (partial scalar %) (transpose b)) a)) args))

(defn m*v [m, & vectors]
  {:pre [(matrix? m) (apply vect? vectors) (apply same_len? (first m) vectors)]
   :post [(vect? %) (same_len? % m)]}
  (mapv (apply partial scalar vectors) m))

(defn m*s [m, & scalars]
  {:pre [(matrix? m) (every? number? scalars)]
   :post [(matrix? %) (same_len? m %) (same_len? (first m) (first %))]}
  (mapv (fn [v]
          (apply v*s v scalars)) m))

(defn matrix_eval [op, & args]
  {:pre  [(apply matrix? args) (apply same_len? args) (apply same_len? (map first args))]
   :post [(matrix? %) (same_len? % (first args)) (same_len? (first %) (first (first args)))]}
  (apply mapv op args))

(defn m+ [& args]
  (apply matrix_eval v+ args))

(defn m- [& args]
  (apply matrix_eval v- args))

(defn m* [& args]
  (apply matrix_eval v* args))

(defn md [& args]
  (apply matrix_eval vd args))

(defn simplex_eval [op_v, op_x, & args]
  {:pre [(apply == (map simplex_n args))]
   :post [(== (simplex_n %) (simplex_n (first args)))]}
  (if (apply vect? args)
    (apply op_v args)
    (apply mapv op_x args)))

(defn x+ [& args]
  (apply simplex_eval v+, x+, args))

(defn x- [& args]
  (apply simplex_eval v-, x-, args))

(defn x* [& args]
  (apply simplex_eval v*, x*, args))

(defn xd [& args]
  (apply simplex_eval vd, xd, args))
