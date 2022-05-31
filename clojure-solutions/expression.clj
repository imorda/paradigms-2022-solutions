(load-file "proto.clj")
(load-file "parser.clj")

(defn operation [op]
  (fn [& args]
    (fn [vars]
      (apply op (map #(% vars) args)))))
(defn meanImpl [& args]
  (/ (reduce + args) (count args)))
(defn divideImpl [& args]
  (if (== 1 (count args))
    (/ 1.0 (double (first args)))
    (reduce #(/ (double %1) (double %2)) args)))
(defn varnImpl [& args] (let [meanV (apply meanImpl args)]
                          (/ (reduce + (map #(Math/pow (- meanV %1) 2) args)) (count args))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def mean (operation meanImpl))
(def varn (operation varnImpl))
(def divide (operation divideImpl))

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
    (symbol? buffer) (varFact (name buffer))))

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
(def toStringInfix (method :toStringInfix))
(def _applyDiff (method :applyDiff))
(def _applyUnaryDiff (field :applyUnaryDiff))
(def _applyBinDiff (field :applyBinDiff))
(def diff (method :diff))
(declare Constant ZERO ONE M_ONE TWO Variable Multiply Divide Add Subtract Negate Mean Varn ILn ILog IPow)

(def OperationPrototype
  {:eval          (fn [this vars] (apply (_applyOperation this) (map #(evaluate % vars) (_expressions this))))
   :toString      (fn [this] (clojure.string/join "" ["(" (_opSymbol this) " "
                                                      (clojure.string/join " " (map toString (_expressions this)))
                                                      ")"]))
   :toStringInfix (fn [this] (clojure.string/join "" ["("
                                                      (clojure.string/join (clojure.string/join "" [" "
                                                                                                    (_opSymbol this)
                                                                                                    " "])
                                                                           (map toStringInfix (_expressions this)))
                                                      ")"]))
   :diff          (fn [this vars] (apply _applyDiff this (map #(diff % vars) (_expressions this))))
   })
(defn Operation [this & expr]
  (assoc this
    :expr expr))

(def TrivialOpPrototype
  (assoc OperationPrototype
    :toString (fn [this] (apply str (_expressions this)))
    :toStringInfix (partial toString)))

(def UnaryOpPrototype
  (assoc OperationPrototype
    :toStringInfix (fn [this] (clojure.string/join "" [(_opSymbol this)
                                                       "("
                                                       (toStringInfix (first (_expressions this)))
                                                       ")"]))))

(def BinReduciblePrototype
  (assoc OperationPrototype
    :diff (fn [this vars]
            (let [exps (_expressions this)]
              (if (== 1 (count exps))
                ((_applyUnaryDiff this) (first exps) (diff (first exps) vars))
                (last (reduce #(apply (_applyBinDiff this) (concat %1 %2))
                              (map vector exps
                                   (map #(diff % vars) exps)))))))))

(def ConstPrototype
  (assoc TrivialOpPrototype
    :eval (fn [this _] (first (_expressions this)))
    :diff (fn [_ _] ZERO)))
(def Constant (constructor Operation ConstPrototype))
(def ZERO (Constant 0))
(def ONE (Constant 1))
(def M_ONE (Constant -1))
(def TWO (Constant 2))

(def VariablePrototype
  (assoc TrivialOpPrototype
    :eval (fn [this vars] (get vars ((comp str #(Character/toLowerCase %) first first) (_expressions this))))
    :diff (fn [this var] (if (= var ((comp str #(Character/toLowerCase %) first first)
                                     (_expressions this))) ONE ZERO))))
(def Variable (constructor Operation VariablePrototype))

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

(def ILnPrototype
  (assoc UnaryOpPrototype
    :symbol "ln"
    :applyOp #(Math/log (Math/abs %))
    :applyDiff (fn [this & args] (apply Multiply (apply Divide ONE (_expressions this)) args))))
(def ILn (constructor Operation ILnPrototype))

(def ILogPrototype
  (assoc BinReduciblePrototype
    :symbol "//"
    :applyOp (fn [& args] (apply divideImpl (reverse (map #(Math/log (Math/abs %)) args))))
    :applyBinDiff (fn [b db a da] [(ILog a b) (Divide (Subtract (Multiply (Multiply (Divide ONE a) da)
                                                                          (ILn b))
                                                                (Multiply (ILn a)
                                                                          (Multiply (Divide ONE b) db)))
                                                      (Multiply (ILn b) (ILn b)))])))
(def ILog (constructor Operation ILogPrototype))

(def IPowPrototype
  (assoc BinReduciblePrototype
    :symbol "**"
    :applyOp (fn [& args] (reduce #(Math/pow %1 %2) args))
    :applyBinDiff (fn [a da b db] [(IPow a b) (Add (Multiply (Multiply b (IPow a (Subtract b ONE))) da)
                                                   (Multiply (Multiply (IPow a b) (ILn a)) db))])))
(def IPow (constructor Operation IPowPrototype))

(def DividePrototype
  (assoc BinReduciblePrototype
    :symbol "/"
    :applyOp divideImpl
    :applyBinDiff (fn [a da b db] [(Divide a b) (Divide (Subtract (Multiply da b) (Multiply a db)) (Multiply b b))])
    :applyUnaryDiff (fn [a da] (Divide (Negate da) (Multiply a a)))))
(def Divide (constructor Operation DividePrototype))

(def NegatePrototype
  (assoc UnaryOpPrototype
    :symbol "negate"
    :applyOp -
    :applyDiff (fn [_ & args] (apply Multiply M_ONE args))))
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
                                                     (Multiply TWO (Subtract a mn)
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

; Parser begins here
(def parseObjectInfix
  (let [
        *all-chars (mapv char (range 0 128))
        *digit (+char (apply str (filter #(Character/isDigit %) *all-chars)))
        *space (+char (apply str (filter #(Character/isWhitespace %) *all-chars)))
        *letter (+char (apply str (filter #(Character/isLetter %) *all-chars)))
        *ws (+ignore (+star *space))
        *number (+map read-string (+seqf #(apply str (flatten %&)) (+opt (+char "-")) (+plus *digit)
                                         (+opt (+seq (+char ".") (+plus *digit)))))
        *word (+str (+plus *letter))]
    (letfn [
            (*string [value] (apply +seqf str (map #(+char (str %)) value)))
            (*operator [expectedOpMap] (apply +or (map *string (keys expectedOpMap))))
            (*operationLeftAssoc [higherPriorityOp currentPriorityOpMap]
              (+seqf #(reduce (fn [accumulator [operator operand]]
                                ((get currentPriorityOpMap operator) accumulator operand)) %1 %2)
                     *ws higherPriorityOp (+star (+seq *ws (*operator currentPriorityOpMap) *ws higherPriorityOp))))
            (*operationRightAssoc [higherPriorityOp currentPriorityOpMap]
              (+seqf (fn [& args] (apply (fn
                                           ([lOperand _] lOperand)
                                           ([lOperand operator rOperand] ((get currentPriorityOpMap operator)
                                                                          lOperand rOperand))) (flatten args)))
                     *ws higherPriorityOp
                     (+opt (+seq *ws (*operator currentPriorityOpMap)
                                 *ws (delay (*operationRightAssoc higherPriorityOp currentPriorityOpMap))))))
            (*operationUnary [higherPriorityOp currentPriorityOpMap]
              (+or (+seqf (fn [operator operand] ((get currentPriorityOpMap operator) operand))
                          *ws (*operator currentPriorityOpMap)
                          *ws (delay (*operationUnary higherPriorityOp currentPriorityOpMap)))
                   (+seqn 0 *ws higherPriorityOp)))
            (*operand [] (+or (+seqn 1 *ws (+char "(") *ws (delay (*lowestPriorityOperation)) *ws (+char ")"))
                              (+seqf Variable *ws *word)
                              (+seqf Constant *ws *number)))
            (*unaryOps [] (*operationUnary (*operand) {"negate" Negate "ln" ILn}))
            (*powLog [] (*operationRightAssoc (*unaryOps) {"**" IPow "//" ILog}))
            (*divideMultiply [] (*operationLeftAssoc (*powLog) {"*" Multiply "/" Divide}))
            (*addSubtract [] (*operationLeftAssoc (*divideMultiply) {"+" Add "-" Subtract}))
            (*lowestPriorityOperation [] (*addSubtract))]
      (+parser (+seqn 0 *ws (*lowestPriorityOperation) *ws)))))
