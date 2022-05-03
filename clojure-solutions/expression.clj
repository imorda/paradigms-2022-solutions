(defn operation [op, & args]
  (fn [vars]
    (apply op (map #(% vars) args))))
(defn meanImpl [& args]
  (/ (reduce + args) (count args)))

(def add (partial operation +))
(def subtract (partial operation -))
(def multiply (partial operation *))
(def mean (partial operation meanImpl))
(def varn (partial operation (fn [& args] (let [meanV (apply meanImpl args)]
                                            (/ (reduce + (map #(Math/pow (- meanV %1) 2) args)) (count args))))))
(def divide
  (letfn [(divideImpl [a, b]
            (/ (double a) (double b)))]
    (partial operation #(if (== 1 (count %&))
                          (/ 1.0 (double (first %&)))
                          (reduce divideImpl %&)))))

(def negate subtract)
(def constant constantly)
(defn variable [symbol]
  {:pre [(string? symbol)]}
  #(get % symbol))

(def opMap {'+ add
            '- subtract
            '* multiply
            '/ divide
            'negate negate
            'mean mean
            'varn varn})

(defn parseFunction [input]
  (letfn [(parseTokenized [buffer]
            (cond
              (seq? buffer) (apply (get opMap (first buffer)) (map parseTokenized (rest buffer)))
              (number? buffer) (constant buffer)
              :else (variable (name buffer))))]
    (parseTokenized (read-string input))))
