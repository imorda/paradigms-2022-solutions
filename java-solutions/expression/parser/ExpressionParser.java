package expression.parser;

import expression.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExpressionParser implements TripleParser {
    @Override
    public TripleExpression parse(final String expression) {
        return new ExpressionParserImpl(expression).parseExpression();
    }

    private static class ExpressionParserImpl extends BaseParser {
        private final Map<String, SupportedBinaryOperations> supportedBinOps = Map.of(
                Add.OPERATION_SYM, new SupportedBinaryOperations(Add::new, 20),
                Subtract.OPERATION_SYM, new SupportedBinaryOperations(Subtract::new, 20),
                Multiply.OPERATION_SYM, new SupportedBinaryOperations(Multiply::new, 10),
                Divide.OPERATION_SYM, new SupportedBinaryOperations(Divide::new, 10),
                Max.OPERATION_SYM, new SupportedBinaryOperations(Max::new, 30),
                Min.OPERATION_SYM, new SupportedBinaryOperations(Min::new, 30)
        );
        private final Map<String, SupportedUnaryOperations> supportedUnaryOps = Map.of(
                Negate.OPERATION_SYM, new SupportedUnaryOperations(Negate::new, 0),
                TZero.OPERATION_SYM, new SupportedUnaryOperations(TZero::new, 0),
                LZero.OPERATION_SYM, new SupportedUnaryOperations(LZero::new, 0)
        );
        private final String supportedVariables = "xyz";

        public ExpressionParserImpl(final CharSource source) {
            super(source);
        }

        public ExpressionParserImpl(final String source) {
            this(new StringSource(source));
        }

        private PriorityExpression parseExpression() {
            final PriorityExpression res = parseOperation();
            skipWhitespace();
            if (eof()) {
                return res;
            }
            throw error("Expected end-of-file");
        }

        private PriorityExpression parseOperation() {
            return parseOperation(Integer.MAX_VALUE);
        }

        private PriorityExpression parseOperation(final int maxPriority) {
            skipWhitespace();

            PriorityExpression leftOperand = parseOperand();
            while (true) {
                skipWhitespace();

                final SupportedBinaryOperations operator = takeBinaryOperator(maxPriority);

                if (operator == null) {
                    return leftOperand;
                }

                skipWhitespace();
                final PriorityExpression rightOperand = parseOperation(operator.priority() - 1);

                leftOperand = operator.expFactory().apply(leftOperand, rightOperand);
            }
        }

        private PriorityExpression parseOperand() {
            skipWhitespace();
            if (take('(')) {
                final PriorityExpression op = parseOperation();
                expect(')');
                return op;
            }
            if (test(Character.DECIMAL_DIGIT_NUMBER)) {
                final StringBuilder number = new StringBuilder();
                return tryExtractConst(number);
            }

            if (test(x -> supportedVariables.indexOf(x) > -1)) {
                final char varSymbol = take();
                return new Variable(String.valueOf(varSymbol));
            }
            final SupportedUnaryOperations operator = takeUnaryOperator();
            if (operator != null) {
                if (operator == supportedUnaryOps.get("-") && test(Character.DECIMAL_DIGIT_NUMBER)) {
                    final StringBuilder number = new StringBuilder().append('-');
                    return tryExtractConst(number);
                }

                if (test('(')) {
                    return operator.expConstructor().apply(new BraceEnclosed(parseOperand()));
                }

                return operator.expConstructor().apply(parseOperand());
            }
            throw error("Unrecognized operand");
        }

        private PriorityExpression tryExtractConst(final StringBuilder number) {
            takeInteger(number);
            try {
                return new Const(Integer.parseInt(number.toString()));
            } catch (final NumberFormatException e) {
                throw error("Invalid number " + number);
            }
        }

        private void takeInteger(final StringBuilder sb) {
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
            } else {
                throw error("Integer expected");
            }
        }

        private SupportedBinaryOperations takeBinaryOperator(final int maxPriority) {
            for (final Map.Entry<String, SupportedBinaryOperations> i : supportedBinOps.entrySet()) {
                if (i.getValue().priority() <= maxPriority && take(i.getKey())) {
                    return i.getValue();
                }
            }
            return null;
        }

        private SupportedUnaryOperations takeUnaryOperator() {
            for (final Map.Entry<String, SupportedUnaryOperations> i : supportedUnaryOps.entrySet()) {
                if (take(i.getKey())) {
                    return i.getValue();
                }
            }
            return null;
        }

        record SupportedBinaryOperations(
                BiFunction<PriorityExpression, PriorityExpression, PriorityExpression> expFactory,
                int priority
        ) {
        }

        record SupportedUnaryOperations(
                Function<PriorityExpression, PriorityExpression> expConstructor,
                int priority
        ) {
        }
    }
}
