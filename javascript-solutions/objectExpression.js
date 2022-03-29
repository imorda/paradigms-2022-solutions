"use strict";


class Operation {
    constructor(...expressions) {
        this.expressions = expressions;
    }

    evaluate(...vars) {
        return this.applyOperation(...this.expressions.map((expression) => expression.evaluate(...vars)))
    }

    toString() {
        return this.expressions.map((expression) => expression.toString()).join(" ");
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
        super(value);
    }

    evaluate(...vars) {
        return vars[Variable.VAR_SYMBOLS.indexOf(this.expressions[0].toString())];
    }

    diff(variable) {
        return new Const(variable === this.toString() ? 1 : 0);
    }
}

Variable.VAR_SYMBOLS = ["x", "y", "z"];

class Const extends TrivialOp {
    constructor(value) {
        super(value);
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
        super(...expr);
    }

    applyOperation(a, b) {
        return a + b;
    }

    toString() {
        return super.toString() + " +";
    }
}

class Subtract extends AdditiveOp {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return a - b;
    }

    toString() {
        return super.toString() + " -";
    }
}

class Pow extends Operation {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return Math.pow(a, b);
    }

    toString() {
        return super.toString() + " pow";
    }

    diff(...args) {
        return new Add(
            new Multiply(
                new Multiply(
                    this.expressions[1],
                    new Pow(
                        this.expressions[0],
                        new Subtract(
                            this.expressions[1],
                            new Const(1)
                        )
                    )
                ),
                this.expressions[0].diff(...args)
            ),
            new Multiply(
                new Multiply(
                    new Pow(
                        this.expressions[0],
                        this.expressions[1]
                    ),
                    new Ln(this.expressions[0])
                ),
                this.expressions[1].diff(...args)
            )
        );
    }
}

class Log extends Operation {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return Math.log(Math.abs(b)) / Math.log(Math.abs(a));
    }

    toString() {
        return super.toString() + " log";
    }

    diff(...args) {
        return new Divide(
            new Ln(this.expressions[1]),
            new Ln(this.expressions[0])
        ).diff(...args);
    }
}

class Ln extends Operation {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(val) {
        return Math.log(Math.abs(val));
    }

    toString() {
        return super.toString() + " ln";
    }

    diff(...args) {
        return new Multiply(
            new Divide(
                new Const(1),
                this.expressions[0]),
            this.expressions[0].diff(...args)
        )
    }
}

class Multiply extends Operation {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return a * b;
    }

    toString() {
        return super.toString() + " *";
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
        super(...expr);
    }

    applyOperation(a, b) {
        return a / b;
    }

    toString() {
        return super.toString() + " /";
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
        super(...expr);
    }

    applyOperation(exp) {
        return -exp;
    }

    toString() {
        return super.toString() + " negate";
    }

    diff(...args) {
        return new Multiply(new Const(-1), this.expressions[0]).diff(...args);
    }
}


const parse = str => parseTokenized(str.split(" "));

const binOpsDict = {"+": Add, "-": Subtract, "*": Multiply, "/": Divide, "log": Log, "pow": Pow};
const unaryOpsDict = {"negate": Negate, "ln": Ln};


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
    if (Variable.VAR_SYMBOLS.includes(current)) {
        return new Variable(current);
    } else if (binOpsDict[current] !== undefined) {
        return new binOpsDict[current](...parseTokens(2, stack));
    } else if (unaryOpsDict[current] !== undefined) {
        return new unaryOpsDict[current](...parseTokens(1, stack));
    } else {
        return new Const(parseFloat(current));
    }
}
