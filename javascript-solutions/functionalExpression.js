"use strict";

const binOp = f => (left, right) => (x, y, z) => f(left(x, y, z), right(x, y, z));
const unOp = f => (exp) => (x, y, z) => f(exp(x, y, z));

const variable = symbol => (x, y, z) => symbol === 'x' ? x : symbol === 'y' ? y : symbol === 'z' ? z : undefined;
const cnst = num => () => num;

const negate = unOp((exp) => -exp);
const abs = unOp((exp) => Math.abs(exp));
const add = binOp((left, right) => left + right);
const subtract = binOp((left, right) => left - right);
const multiply = binOp((left, right) => left * right);
const divide = binOp((left, right) => left / right);
const pi = cnst(Math.PI);
const e = cnst(Math.E);
const iff = (cond, branch1, branch2) => (x, y, z) => cond(x, y, z) >= 0 ? branch1(x, y, z) : branch2(x, y, z);


const parse = str => parseTokenized(str.split(" "));

const binOpsDict = {"+": add, "-": subtract, "*": multiply, "/": divide};
const unaryOpsDict = {"negate": negate, "abs": abs};
const ternaryOpsDict = {"iff": iff};
const constsDict = {"pi": pi, "e": e};

const parseTokens = function(n, stack) {
    let ans = [];
    for(let i = 0; i < n; i++){
        ans.push(parseTokenized(stack));
    }
    ans.reverse();
    return ans;
}

const parseTokenized = function (stack) {
    let current = stack.pop().trim().toLowerCase();
    while (current.length === 0){
        if(stack.length === 0){
            return;
        }
        current = stack.pop().trim().toLowerCase();
    }
    if ("xyz".includes(current)) {
        return variable(current);
    } else if (binOpsDict[current] !== undefined) {
        return binOpsDict[current](...parseTokens(2, stack));
    } else if (unaryOpsDict[current] !== undefined) {
        return unaryOpsDict[current](...parseTokens(1, stack));
    } else if (ternaryOpsDict[current] != undefined){
        return ternaryOpsDict[current](...parseTokens(3, stack));
    } else if (constsDict[current] != undefined) {
        return constsDict[current];
    } else {
        return cnst(parseInt(current));
    }
}


let expression = parse("x x * 2 x * - 1 +");


for (let i = 0; i <= 10; i++) {
    println(i + ": " + expression(i, 0, 0));
}