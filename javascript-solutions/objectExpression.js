"use strict";

function NotImplementedError(message) {


    this.name = "NotImplementedError";
    this.message = message;
}

NotImplementedError.prototype = Error.prototype;

function ParseError(message) {

    this.name = "ParseError";
    this.message = message;
}

ParseError.prototype = Error.prototype;

class CharSource {
    constructor() {
        this.curSymbol = undefined;
    }

    take(symbols) {
        if (this.test(symbols)) {
            this.getNewSymbol();
            return symbols;
        }
        return '';
    }

    expect(symbols) {
        if (this.test(symbols)) {
            this.getNewSymbol();
            return;
        }
        throw this.error(`expected ${symbols}, got ${this.curSymbol}`);
    }

    test(symbols) {
        return symbols.includes(this.curSymbol);
    }

    extractToken(acceptableChars) {
        let ans = [];
        while (acceptableChars.includes(this.curSymbol)) {
            ans.push(this.curSymbol);
            this.getNewSymbol();
        }
        return ans.join('');
    }

    skipChars(chars) {
        while (chars.includes(this.curSymbol)) {
            this.getNewSymbol();
        }
    }

    getNewSymbol() {
        throw new NotImplementedError("getSymbol() not implemented");
    }

    error(message) {
        throw new NotImplementedError("error() not implemented");
    }
}


class StringSource extends CharSource {
    constructor(string, ...props) {
        super(...props);
        this.string = string;
        this.pos = 0;
        this.getNewSymbol();
    }

    getNewSymbol() {
        if (this.pos >= this.string.length) {
            this.curSymbol = "EOS";
        } else {
            this.curSymbol = this.string[this.pos];
            this.pos++;
        }
    }

    error(message) {
        return new ParseError(`Parse error occured while parsing string "${this.string}" at position ${this.pos}: ${message}.`)
    }
}


class Operation {
    constructor(...expressions) {
        this.expressions = expressions;
    }

    prefix() {
        return `(${this.opSymbol} ${this.expressions.map((expression) => expression.prefix()).join(" ")})`;
    }

    evaluate(...vars) {
        return this.applyOperation(...this.expressions.map((expression) => expression.evaluate(...vars)))
    }

    toString() {
        return this.expressions.map((expression) => expression.toString()).join(" ") + " " + this.opSymbol;
    }

    applyOperation(...nums) {
        throw new NotImplementedError("applyOperation() not implemented");
    }

    diff(variable) {
        throw new NotImplementedError("diff() not implemented");
    }

    get opSymbol() {
        throw new NotImplementedError("get opSymbol() not implemented");
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

    prefix() {
        return this.toString();
    }

    get opSymbol() {
        return undefined;
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
    static VAR_SYMBOLS = ["x", "y", "z"];

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

    get opSymbol() {
        return '+';
    }
}

class Subtract extends AdditiveOp {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return a - b;
    }

    get opSymbol() {
        return '-';
    }
}

class Pow extends Operation {
    constructor(...expr) {
        super(...expr);
    }

    applyOperation(a, b) {
        return Math.pow(a, b);
    }

    get opSymbol() {
        return 'pow';
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

    get opSymbol() {
        return 'log';
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

    get opSymbol() {
        return 'ln';
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

    get opSymbol() {
        return '*';
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

    get opSymbol() {
        return '/';
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

    get opSymbol() {
        return 'negate';
    }

    diff(...args) {
        return new Multiply(new Const(-1), this.expressions[0]).diff(...args);
    }
}

class OpTemplate {
    constructor(arity, obj) {
        this._arity = arity;
        this._obj = obj;
    }

    get arity() {
        return this._arity;
    }

    get obj() {
        return this._obj;
    }
}

const opsMap = {
    "+": new OpTemplate(2, Add),
    "-": new OpTemplate(2, Subtract),
    "*": new OpTemplate(2, Multiply),
    "/": new OpTemplate(2, Divide),
    "log": new OpTemplate(2, Log),
    "pow": new OpTemplate(2, Pow),
    "negate": new OpTemplate(1, Negate),
    "ln": new OpTemplate(1, Ln)
};


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
    } else if (current in opsMap) {
        const template = opsMap[current];
        const args = Array.from({length: template.arity}, () => parseTokenized(stack));
        args.reverse();
        return new template.obj(...args);
    } else {
        return new Const(parseFloat(current));
    }
}


class PrefixParser {
    constructor(src) {
        this.source = src
    }

    skipSpaces() {
        this.source.skipChars(' ');
    }

    tryParseNumber() {
        const token = this.source.take('-')
            + this.source.extractToken("0123456789")
            + this.source.take('.')
            + this.source.extractToken("0123456789");

        if (token.length === 0) {
            return undefined;
        }

        const parsed = parseFloat(token);

        if (isNaN(parsed)) {
            throw this.source.error(`Unparseable numeric token "${token}"`);
        }

        return parsed;
    }

    static OP_CHARS = Object.getOwnPropertyNames(opsMap).join('');

    parse() {
        this.skipSpaces();

        let token = this.source.extractToken(Variable.VAR_SYMBOLS.join(''));

        if (token.length !== 0) {
            if (Variable.VAR_SYMBOLS.includes(token)) {
                return new Variable(token);
            }
            throw this.source.error(`Unrecognized variable-token "${token}"`);
        }

        token = this.tryParseNumber();
        if (token !== undefined) {
            return new Const(token);
        }

        this.source.expect('(');

        this.skipSpaces();

        token = this.source.extractToken(PrefixParser.OP_CHARS);

        if (this.source.test('1234567890')) {
            throw this.source.error("expected token separator after operator, got number")
        }

        let ans = undefined;

        if (token in opsMap) {
            const template = opsMap[token];
            ans = new template.obj(...Array.from({length: template.arity}, () => this.parse()));
        }

        this.skipSpaces();

        this.source.expect(')');

        return ans;
    }
}

const parse = str => parseTokenized(str.split(" "));
const parsePrefix = str => {
    const src = new StringSource(str);
    const parser = new PrefixParser(src);
    const parsed = parser.parse();
    parser.skipSpaces();
    src.expect("EOS");
    return parsed;
}
