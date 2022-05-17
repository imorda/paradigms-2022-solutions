(load-file "proto.clj")
(load-file "parser.clj")

(defn operation [op, & args]
  (fn [vars]
    (apply op (map #(% vars) args))))
(defn meanImpl [& args]
  (/ (reduce + args) (count args)))
(defn divideImpl [& args]
  (if (== 1 (count args))
    (/ 1.0 (double (first args)))
    (reduce #(/ (double %1) (double %2)) args)))
(defn varnImpl [& args] (let [meanV (apply meanImpl args)]
                          (/ (reduce + (map #(Math/pow (- meanV %1) 2) args)) (count args))))

(def add (partial operation +))
(def subtract (partial operation -))
(def multiply (partial operation *))
(def mean (partial operation meanImpl))
(def varn (partial operation varnImpl))
(def divide (partial operation divideImpl))

(def negate subtract)
(def constant constantly)
(defn variable [symbol]
  {:pre [(string? symbol)]}
  #(get % symbol))

(defn parseTokenized [buffer tokenMap constFact varFact]
  (cond
    (seq? buffer) (apply (get tokenMap (first buffer))
                         (map #(parseTokenized % tokenMap constFact varFact) (rest buffer)))
    (number? buffer) (constFact buffer)
    :else (varFact (name buffer))))

(def opMap {'+      add
            '-      subtract
            '*      multiply
            '/      divide
            'negate negate
            'mean   mean
            'varn   varn})
(defn parseFunction [input]
  (parseTokenized (read-string input) opMap constantly variable))

; Objects begin here
(def _expressions (field :expr))
(def _opSymbol (field :symbol))
(def _applyOperation (field :applyOp))
(def evaluate (method :eval))
(def toString (method :toString))
(def _applyDiff (method :applyDiff))
(def _applyUnaryDiff (field :applyUnaryDiff))
(def _applyBinDiff (field :applyBinDiff))
(def diff (method :diff))
(declare Constant Variable Multiply Divide Add Subtract Negate Mean Varn)

(def OperationPrototype
  {:eval     (fn [this vars] (apply (_applyOperation this) (map #(evaluate % vars) (_expressions this))))
   :toString (fn [this] (clojure.string/join "" ["(" (_opSymbol this) " "
                                                 (clojure.string/join " " (map toString (_expressions this)))
                                                 ")"]))
   :diff     (fn [this vars] (apply _applyDiff this (map #(diff % vars) (_expressions this))))
   })
(defn Operation [this & expr]
  (assoc this
    :expr expr))

(def TrivialOpPrototype
  (assoc OperationPrototype
    :toString (fn [this] (apply str (_expressions this)))))

(def BinReduciblePrototype
  (assoc OperationPrototype
    :diff (fn [this vars]
            (let [exps (_expressions this)]
              (if (== 1 (count exps))
                ((_applyUnaryDiff this) (first exps) (diff (first exps) vars))
                (last (reduce #(apply (_applyBinDiff this) (concat %1 %2))
                              (map vector exps
                                   (map #(diff % vars) exps)))))))))

(def VariablePrototype
  (assoc TrivialOpPrototype
    :eval (fn [this vars] (get vars (first (_expressions this))))
    :diff (fn [this var] (Constant (if (= var (first (_expressions this))) 1 0)))))
(def Variable (constructor Operation VariablePrototype))

(def ConstPrototype
  (assoc TrivialOpPrototype
    :eval (fn [this _] (first (_expressions this)))
    :diff (fn [_ _] (Constant 0))))
(def Constant (constructor Operation ConstPrototype))

(def AddPrototype
  (assoc OperationPrototype
    :symbol "+"
    :applyOp +
    :applyDiff (fn [_ & args] (apply Add args))))
(def Add (constructor Operation AddPrototype))

(def SubtractPrototype
  (assoc OperationPrototype
    :symbol "-"
    :applyOp -
    :applyDiff (fn [_ & args] (apply Subtract args))))
(def Subtract (constructor Operation SubtractPrototype))

(def MultiplyPrototype
  (assoc BinReduciblePrototype
    :symbol "*"
    :applyOp *
    :applyBinDiff (fn [a da b db] [(Multiply a b) (Add (Multiply da b) (Multiply a db))])
    :applyUnaryDiff #(identity %2)))
(def Multiply (constructor Operation MultiplyPrototype))

(def DividePrototype
  (assoc BinReduciblePrototype
    :symbol "/"
    :applyOp divideImpl
    :applyBinDiff (fn [a da b db] [(Divide a b) (Divide (Subtract (Multiply da b) (Multiply a db)) (Multiply b b))])
    :applyUnaryDiff (fn [a da] (Divide (Negate da) (Multiply a a)))))
(def Divide (constructor Operation DividePrototype))

(def NegatePrototype
  (assoc OperationPrototype
    :symbol "negate"
    :applyOp -
    :applyDiff (fn [_ & args] (apply Multiply (Constant -1) args))))
(def Negate (constructor Operation NegatePrototype))

(def MeanPrototype
  (assoc OperationPrototype
    :symbol "mean"
    :applyOp meanImpl
    :applyDiff (fn [_ & args] (Divide (apply Add args) (Constant (count args))))))
(def Mean (constructor Operation MeanPrototype))

(def VarnPrototype
  (assoc OperationPrototype
    :symbol "varn"
    :applyOp varnImpl
    :applyDiff (fn [this & args]
                 (let [mn (apply Mean (_expressions this))
                       dmn (apply _applyDiff mn args)]
                   (Divide (apply Add (map #(apply (fn [a da]
                                                     (Multiply
                                                       (Constant 2)
                                                       (Subtract a mn)
                                                       (Subtract da dmn))) %) (map vector (_expressions this) args)))
                           (Constant (count args)))))))
(def Varn (constructor Operation VarnPrototype))

(def objMap {'+      Add
             '-      Subtract
             '*      Multiply
             '/      Divide
             'negate Negate
             'mean   Mean
             'varn   Varn})
(defn parseObject [input]
  (parseTokenized (read-string input) objMap Constant Variable))
