"use strict";


class Operation {
    constructor(symbol, ...expressions) {
        this.symbol = symbol;
        this.expressions = expressions;
    }

    evaluate(...vars) {
        return this.applyOperation(...this.expressions.map((expression) => expression.evaluate(...vars)))
    }

    toString() {
        return this.expressions.map((expression) => expression.toString()).join(" ") + " " + this.symbol;
    }

    applyOperation(...nums) {
        throw new Error("Evaluate must be implemented");
    }

    diff(variable) {
        throw new Error("Diff must be implemented");
    }
}

class TrivialOp extends Operation {
    constructor(...props) {
        super(...props);
    }

    evaluate(...vars) {
        return this.expressions[0];
    }

    toString() {
        return this.expressions[0].toString();
    }
}

class AdditiveOp extends Operation {
    constructor(...props) {
        super(...props);
    }

    diff(...args) {
        return new this.constructor(this.expressions[0].diff(...args),
            this.expressions[1].diff(...args));
    }
}

class Variable extends TrivialOp {
    constructor(value) {
        super(undefined, value);
    }

    evaluate(...vars) {
        return vars[Variable.varSymbols.indexOf(this.expressions[0].toString())];
    }

    diff(variable) {
        return new Const(variable === this.toString() ? 1 : 0);
    }
}

Variable.varSymbols = ["x", "y", "z"];

class Const extends TrivialOp {
    constructor(value) {
        super(undefined, value);
    }

    get value() {
        return this.expressions[0];
    }

    diff() {
        return new Const(0);
    }
}

class Add extends AdditiveOp {
    constructor(...expr) {
        super("+", ...expr);
    }

    applyOperation(a, b) {
        return a + b;
    }
}

class Subtract extends AdditiveOp {
    constructor(...expr) {
        super("-", ...expr);
    }

    applyOperation(a, b) {
        return a - b;
    }
}

class Multiply extends Operation {
    constructor(...expr) {
        super("*", ...expr);
    }

    applyOperation(a, b) {
        return a * b;
    }

    diff(...args) {
        return new Add(
            new Multiply(
                this.expressions[0].diff(...args),
                this.expressions[1]
            ),
            new Multiply(
                this.expressions[0],
                this.expressions[1].diff(...args)
            )
        );
    }
}

class Divide extends Operation {
    constructor(...expr) {
        super("/", ...expr);
    }

    applyOperation(a, b) {
        return a / b;
    }

    diff(...args) {
        return new Divide(
            new Subtract(
                new Multiply(
                    this.expressions[0].diff(...args),
                    this.expressions[1]
                ),
                new Multiply(
                    this.expressions[0],
                    this.expressions[1].diff(...args)
                )
            ),
            new Multiply(
                this.expressions[1],
                this.expressions[1]
            )
        );
    }
}

class Negate extends Operation {
    constructor(...expr) {
        super("negate", ...expr);
    }

    applyOperation(exp) {
        return -exp;
    }

    diff(...args) {
        return new Multiply(new Const(-1), this.expressions[0]).diff(...args);
    }
}


const parse = str => parseTokenized(str.split(" "));

const binOpsDict = {"+": Add, "-": Subtract, "*": Multiply, "/": Divide};
const unaryOpsDict = {"negate": Negate};


const parseTokens = function (n, stack) {
    let ans = Array.from({length: n}, () => parseTokenized(stack));
    ans.reverse();
    return ans;
}

const parseTokenized = function (stack) {
    let current = stack.pop().trim().toLowerCase();
    while (current.length === 0) {
        if (stack.length === 0) {
            return;
        }
        current = stack.pop().trim().toLowerCase();
    }
    if (Variable.varSymbols.includes(current)) {
        return new Variable(current);
    } else if (binOpsDict[current] !== undefined) {
        return new binOpsDict[current](...parseTokens(2, stack));
    } else if (unaryOpsDict[current] !== undefined) {
        return new unaryOpsDict[current](...parseTokens(1, stack));
    } else {
        return new Const(parseInt(current));
    }
}
