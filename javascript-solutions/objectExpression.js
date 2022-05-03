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

function CharSource() {
    this.curSymbol = undefined;
}

CharSource.prototype.take = function (symbols) {
    if (this.test(symbols)) {
        this.getNewSymbol();
        return symbols;
    }
    return '';
}
CharSource.prototype.expect = function (symbols, errExpectMsg) {
    if (this.test(symbols)) {
        this.getNewSymbol();
        return;
    }
    throw this.error(`expected ${errExpectMsg === undefined ? symbols : errExpectMsg}, got ${this.curSymbol}`);
}

CharSource.prototype.test = function (symbols) {
    return symbols.includes(this.curSymbol);
}

CharSource.prototype.extractToken = function (acceptableChars) {
    let ans = [];
    while (acceptableChars.includes(this.curSymbol)) {
        ans.push(this.curSymbol);
        this.getNewSymbol();
    }
    return ans.join('');
}

CharSource.prototype.skipChars = function (chars) {
    while (chars.includes(this.curSymbol)) {
        this.getNewSymbol();
    }
}

function StringSource(string, ...props) {
    CharSource.call(this, ...props);
    this.string = string;
    this.pos = 0;
    this.getNewSymbol();
}

StringSource.prototype = Object.create(CharSource.prototype);
StringSource.prototype.constructor = StringSource;

StringSource.prototype.getNewSymbol = function () {
    if (this.pos >= this.string.length) {
        this.curSymbol = "EOS";
    } else {
        this.curSymbol = this.string[this.pos];
        this.pos++;
    }
}

StringSource.prototype.error = function (message) {
    return new ParseError(`Parse error occured while parsing string "${this.string}" at position ${this.pos}: ${message}.`)
}


function Operation(...expressions) {
    this.expressions = expressions;
}

Operation.prototype.prefix = function () {
    return `(${this.opSymbol} ${this.expressions.map((expression) => expression.prefix()).join(" ")})`;
}
Operation.prototype.postfix = function () {
    return `(${this.expressions.map((expression) => expression.postfix()).join(" ")} ${this.opSymbol})`;
}

Operation.prototype.evaluate = function (...vars) {
    return this.applyOperation(...this.expressions.map((expression) => expression.evaluate(...vars)))
}

Operation.prototype.diff = function (...vars) {
    return this.applyDiff(...this.expressions.map((expression) => expression.diff(...vars)))
}

Operation.prototype.toString = function () {
    return this.expressions.map((expression) => expression.toString()).join(" ") + " " + this.opSymbol;
}


function TrivialOp(...props) {
    Operation.call(this, ...props);
}

TrivialOp.prototype = Object.create(Operation.prototype);
TrivialOp.prototype.constructor = TrivialOp;
TrivialOp.prototype.evaluate = function (...vars) {
    return this.applyOperation(...vars);
}
TrivialOp.prototype.diff = function (...vars) {
    return this.applyDiff(...vars);
}
TrivialOp.prototype.toString = function () {
    return this.expressions[0].toString();
}
TrivialOp.prototype.prefix = function () {
    return this.toString();
}
TrivialOp.prototype.postfix = function () {
    return this.toString();
}
TrivialOp.prototype.prefix = function () {
    return this.toString();
}

function AdditiveOp(...props) {
    Operation.call(this, ...props);
}

AdditiveOp.prototype = Object.create(Operation.prototype);
AdditiveOp.prototype.constructor = AdditiveOp;
AdditiveOp.prototype.applyDiff = function (...diffs) {
    return new this.constructor(...diffs);
}

function Variable(value) {
    TrivialOp.call(this, value);
}

Variable.VAR_SYMBOLS = ["x", "y", "z"];
Variable.prototype = Object.create(TrivialOp.prototype);
Variable.prototype.constructor = Variable;
Variable.prototype.applyOperation = function (...vars) {
    return vars[Variable.VAR_SYMBOLS.indexOf(this.expressions[0].toString())];
}
Variable.prototype.applyDiff = function (variable) {
    return new Const(variable === this.toString() ? 1 : 0);
}

function Const(value) {
    TrivialOp.call(this, value);
}

Const.prototype = Object.create(TrivialOp.prototype);
Const.prototype.constructor = Const;
Const.prototype.applyOperation = function () {
    return this.value;
}
Object.defineProperty(Const.prototype, 'value', {
    get: function () {
        return this.expressions[0];
    }
});
Const.prototype.applyDiff = () => new Const(0);

function Add(a, b) {
    AdditiveOp.call(this, a, b);
}

Add.prototype = Object.create(AdditiveOp.prototype);
Add.prototype.constructor = Add;
Add.prototype.applyOperation = (a, b) => a + b;
Object.defineProperty(Add.prototype, 'opSymbol', {get: () => '+'});

function Subtract(a, b) {
    AdditiveOp.call(this, a, b);
}

Subtract.prototype = Object.create(AdditiveOp.prototype);
Subtract.prototype.constructor = Subtract;
Subtract.prototype.applyOperation = (a, b) => a - b;
Object.defineProperty(Subtract.prototype, 'opSymbol', {get: () => '-'});

function Pow(a, b) {
    Operation.call(this, a, b);
}

Pow.prototype = Object.create(Operation.prototype);
Pow.prototype.constructor = Pow;
Pow.prototype.applyOperation = (a, b) => Math.pow(a, b);
Object.defineProperty(Pow.prototype, 'opSymbol', {get: () => 'pow'});
Pow.prototype.applyDiff = function (...diffs) {
    return new Add(
        new Multiply(
            new Multiply(
                this.expressions[1],
                new Pow(this.expressions[0], new Subtract(this.expressions[1], new Const(1)))
            ),
            diffs[0]
        ),
        new Multiply(
            new Multiply(new Pow(this.expressions[0], this.expressions[1]), new Ln(this.expressions[0])),
            diffs[1]
        )
    );
}

function Log(a, b) {
    Operation.call(this, a, b);
}

Log.prototype = Object.create(Operation.prototype);
Log.prototype.constructor = Log;
Log.prototype.applyOperation = (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a));
Object.defineProperty(Log.prototype, 'opSymbol', {get: () => 'log'});
Log.prototype.applyDiff = function (...diffs) {
    return new Divide(
        new Subtract(
            new Multiply(new Multiply(new Divide(new Const(1), this.expressions[1]), diffs[1]), new Ln(this.expressions[0])),
            new Multiply(new Ln(this.expressions[1]), new Multiply(new Divide(new Const(1), this.expressions[0]), diffs[0]))
        ),
        new Multiply(new Ln(this.expressions[0]), new Ln(this.expressions[0]))
    );
}

function Ln(expr) {
    Operation.call(this, expr);
}

Ln.prototype = Object.create(Operation.prototype);
Ln.prototype.constructor = Ln;
Ln.prototype.applyOperation = val => Math.log(Math.abs(val));
Object.defineProperty(Ln.prototype, 'opSymbol', {get: () => 'ln'});
Ln.prototype.applyDiff = function (...diffs) {
    return new Multiply(new Divide(new Const(1), this.expressions[0]), diffs[0])
}

function Multiply(a, b) {
    Operation.call(this, a, b);
}

Multiply.prototype = Object.create(Operation.prototype);
Multiply.prototype.constructor = Multiply;
Object.defineProperty(Multiply.prototype, 'opSymbol', {get: () => '*'});
Multiply.prototype.applyOperation = (a, b) => a * b;
Multiply.prototype.applyDiff = function (...diffs) {
    return new Add(
        new Multiply(diffs[0], this.expressions[1]),
        new Multiply(this.expressions[0], diffs[1])
    );
}

function Divide(a, b) {
    Operation.call(this, a, b);
}

Divide.prototype = Object.create(Operation.prototype);
Divide.prototype.constructor = Divide;
Divide.prototype.applyOperation = (a, b) => a / b;
Object.defineProperty(Divide.prototype, 'opSymbol', {get: () => '/'});
Divide.prototype.applyDiff = function (...diffs) {
    return new Divide(
        new Subtract(
            new Multiply(diffs[0], this.expressions[1]),
            new Multiply(this.expressions[0], diffs[1])
        ),
        new Multiply(this.expressions[1], this.expressions[1])
    );
}

function Mean(...args) {
    Operation.call(this, ...args);
}

Mean.prototype = Object.create(Operation.prototype);
Mean.prototype.constructor = Mean;
Mean.prototype.applyOperation = (...args) => [...args].reduce((a, b) => a + b, 0) / args.length;
Object.defineProperty(Mean.prototype, 'opSymbol', {get: () => 'mean'});
Mean.prototype.applyDiff = function (...diffs) {
    return new Divide(
        diffs.reduce((a, b) => new Add(a, b), new Const(0)),
        new Const(this.expressions.length)
    );
}

function Var(...args) {
    Operation.call(this, ...args);
}

Var.prototype = Object.create(Operation.prototype);
Var.prototype.constructor = Var;
Var.prototype.applyOperation = (...args) => {
    const mean = Mean.prototype.applyOperation(...args);
    return [...args].reduce((a, b) => a + Math.pow(b - mean, 2), 0) / args.length;
}
Object.defineProperty(Var.prototype, 'opSymbol', {get: () => 'var'});
Var.prototype.applyDiff = function (...diffs) {
    const mean = new Mean(...this.expressions);
    const dmean = mean.applyDiff(...diffs);
    return new Divide(
        this.expressions.map((val, i) => [val, diffs[i]]).reduce((accumulator, values) => new Add(
            accumulator,
            new Multiply(
                new Multiply(new Const(2), new Subtract(values[0], mean)),
                new Subtract(values[1], dmean)
            )), new Const(0)), new Const(this.expressions.length)
    );
}

function Negate(expr) {
    Operation.call(this, expr);
}

Negate.prototype = Object.create(Operation.prototype);
Negate.prototype.constructor = Negate;
Negate.prototype.applyOperation = exp => -exp;
Object.defineProperty(Negate.prototype, 'opSymbol', {get: () => 'negate'});
Negate.prototype.applyDiff = function (...diffs) {
    return new Multiply(new Const(-1), diffs[0]);
}


const opsMap = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "log": Log,
    "pow": Pow,
    "negate": Negate,
    "ln": Ln,
    "mean": Mean,
    "var": Var
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
        const args = Array.from({length: template.length}, () => parseTokenized(stack));
        args.reverse();
        return new template(...args);
    } else {
        return new Const(parseFloat(current));
    }
}


function Parser(src) {
    this.source = src
}

Parser.prototype.skipSpaces = function () {
    this.source.skipChars(' ');
}

Parser.prototype.tryParseNumber = function () {
    const token = this.source.take('-')
        + this.source.extractToken(Parser.NUM_CHARS)
        + this.source.take('.')
        + this.source.extractToken(Parser.NUM_CHARS);

    if (token.length === 0) {
        return undefined;
    }

    const parsed = parseFloat(token);

    if (isNaN(parsed)) {
        throw this.source.error(`Unparseable numeric token "${token}"`);
    }

    return parsed;
}

Parser.prototype.parseOperand = function () {
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

    this.source.expect('(', "operand");

    this.skipSpaces();
}

Parser.OP_CHARS = Object.getOwnPropertyNames(opsMap).join('');
Parser.NUM_CHARS = "1234567890";


function PrefixParser(...args) {
    Parser.call(this, ...args);
}

PrefixParser.prototype = Object.create(Parser.prototype);
PrefixParser.prototype.constructor = PrefixParser;
PrefixParser.prototype.parse = function () {
    let operand = this.parseOperand();
    if (operand !== undefined) {
        return operand;
    }

    let token = this.source.extractToken(Parser.OP_CHARS);

    if (this.source.test(Parser.NUM_CHARS)) {
        throw this.source.error("expected token separator after operator, got number")
    }

    let ans = undefined;

    if (token in opsMap) {
        let args = [];
        while (!this.source.test(')')) {
            args.push(this.parse());
            this.skipSpaces();
        }
        const template = opsMap[token];
        if (template.length > 0 && template.length !== args.length) {
            throw this.source.error(`Invalid number of arguments passed for operation "${token}": ${args.length} instead of ${template.length}`);
        }
        ans = new template(...args);
    } else {
        throw this.source.error(`invalid operation type: "${token}"`);
    }

    this.skipSpaces();

    this.source.expect(')');

    return ans;
}


function PostfixParser(args) {
    Parser.call(this, args);
}

PostfixParser.prototype = Object.create(Parser.prototype);
PostfixParser.prototype.constructor = PostfixParser;
PostfixParser.prototype.parse = function () {
    let operand = this.parseOperand();
    if (operand !== undefined) {
        return operand;
    }

    let args = [];

    let ans = undefined;
    while (true) {
        this.skipSpaces();
        let token = this.source.extractToken(Parser.OP_CHARS);
        if (token === '-' && this.source.test(Parser.NUM_CHARS)) {  // Special case when '-' is part of a number
            args.push(new Const(-this.tryParseNumber()));
            continue;
        }

        let template = opsMap[token];
        if (template !== undefined) {
            if (template.length > 0 && template.length !== args.length) {
                throw this.source.error(`Invalid number of arguments passed for operation "${token}": ${args.length} instead of ${template.length}`);
            }
            ans = new template(...args);
            break;
        } else if (token.length > 0){
            throw this.source.error(`Unable to parse token "${token}"`);
        }

        args.push(this.parse());
    }

    this.skipSpaces();

    this.source.expect(')');

    return ans;
}

const runParser = parserClass => str => {
    const src = new StringSource(str);
    const parser = new parserClass(src);
    const parsed = parser.parse();
    parser.skipSpaces();
    src.expect("EOS");
    return parsed;
}


const parse = str => parseTokenized(str.split(" "));
const parsePrefix = runParser(PrefixParser);
const parsePostfix = runParser(PostfixParser);
