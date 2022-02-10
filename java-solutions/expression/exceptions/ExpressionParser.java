package expression.exceptions;

import expression.*;
import expression.parser.BaseParser;
import expression.parser.CharSource;
import expression.parser.StringSource;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ExpressionParser implements TripleParser{
    @Override
    public TripleExpression parse(String expression) throws ParseException {
        return new ExpressionParserImpl(expression).parseExpression();
    }

    private static class ExpressionParserImpl extends BaseParser {
        private final List<SupportedBinaryOperations> supportedBinOps = List.of(
                new SupportedBinaryOperations(CheckedPow.OPERATION_SYM, CheckedPow::new, 0),
                new SupportedBinaryOperations(CheckedLog.OPERATION_SYM, CheckedLog::new, 0),
                new SupportedBinaryOperations(CheckedAdd.OPERATION_SYM, CheckedAdd::new, 20),
                new SupportedBinaryOperations(CheckedSubtract.OPERATION_SYM, CheckedSubtract::new, 20),
                new SupportedBinaryOperations(CheckedMultiply.OPERATION_SYM, CheckedMultiply::new, 10),
                new SupportedBinaryOperations(CheckedDivide.OPERATION_SYM, CheckedDivide::new, 10),
                new SupportedBinaryOperations(Max.OPERATION_SYM, Max::new, 30),
                new SupportedBinaryOperations(Min.OPERATION_SYM, Min::new, 30)
        );
        private final Map<String, SupportedUnaryOperations> supportedUnaryOps = Map.of(
                CheckedNegate.OPERATION_SYM, new SupportedUnaryOperations(CheckedNegate::new, -1),
                TZero.OPERATION_SYM, new SupportedUnaryOperations(TZero::new, -1),
                LZero.OPERATION_SYM, new SupportedUnaryOperations(LZero::new, -1),
                CheckedAbs.OPERATION_SYM, new SupportedUnaryOperations(CheckedAbs::new, -1)
        );
        private final String SUPPORTED_VARIABLES = "xyz";

        public ExpressionParserImpl(CharSource source) {
            super(source);
        }

        public ExpressionParserImpl(String source) {
            this(new StringSource(source));
        }

        private PriorityExpression parseExpression() throws ParseException {
            PriorityExpression res = parseOperation();
            skipWhitespace();
            if (eof()) {
                return res;
            }
            throw new EOFException(source);
        }

        private PriorityExpression parseOperation() throws ParseException {
            return parseOperation(Integer.MAX_VALUE);
        }

        private PriorityExpression parseOperation(int maxPriority) throws ParseException {
            skipWhitespace();

            PriorityExpression leftOperand = parseOperand();
            while (true) {
                SupportedBinaryOperations operator = takeBinaryOperator(maxPriority);

                if (operator == null) {
                    return leftOperand;
                }

                skipWhitespace();
                PriorityExpression rightOperand = parseOperation(operator.priority() - 1);

                leftOperand = operator.expFactory().apply(leftOperand, rightOperand);
            }
        }

        private PriorityExpression parseOperand() throws ParseException {
            skipWhitespace();
            if (take('(')) {
                PriorityExpression op = parseOperation();
                if (!take(')')) {
                    throw new ExpressionParseException("expected ')'", source, peekOrEOF());
                }
                return op;
            }
            if (test(Character.DECIMAL_DIGIT_NUMBER)) {
                StringBuilder number = new StringBuilder();
                return tryExtractConst(number);
            }

            if (test(x -> SUPPORTED_VARIABLES.indexOf(x) > -1)) {
                char varSymbol = take();
                return new Variable(String.valueOf(varSymbol));
            }

            SupportedUnaryOperations operator = takeUnaryOperator();
            if (operator != null) {
                if (operator == supportedUnaryOps.get("-") && test(Character.DECIMAL_DIGIT_NUMBER)) {
                    StringBuilder number = new StringBuilder().append('-');
                    return tryExtractConst(number);
                }
                if (test('(')) {
                    return operator.expFactory().apply(new BraceEnclosed(parseOperand()));
                }
                return operator.expFactory().apply(parseOperand());
            }
            throw new InvalidOperandException(source, peekOrEOF());
        }

        private PriorityExpression tryExtractConst(StringBuilder number) throws ParseException {
            takeInteger(number);
            try {
                return new Const(Integer.parseInt(number.toString()));
            } catch (final NumberFormatException e) {
                throw new NumberParseException(source, number.toString());
            }
        }

        private void takeInteger(StringBuilder sb) throws ParseException {
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
            } else {
                if (!eof()) {
                    sb.append(take());
                }
                throw new NumberParseException(source, sb.toString());
            }
        }

        private SupportedBinaryOperations takeBinaryOperator(int maxPriority) throws ParseException {
            skipWhitespace();

            for (SupportedBinaryOperations i : supportedBinOps) {
                if (i.priority() <= maxPriority && take(i.name())) {
                    testAlphanumericTagAmbiguity(i.name());
                    return i;
                }
            }
            return null;
        }

        private void testAlphanumericTagAmbiguity(String tag) throws ParseException {
            if (isAlphanumericCharType(getCharType())
                    && isAlphanumericCharType(Character.getType(tag.charAt(tag.length() - 1)))) {
                throw new InvalidOperatorException(source, "illegal characters after " + tag);
            }
        }

        private boolean isAlphanumericCharType(int type) {
            return type == Character.UPPERCASE_LETTER
                    || type == Character.LOWERCASE_LETTER
                    || type == Character.DECIMAL_DIGIT_NUMBER;
        }

        private SupportedUnaryOperations takeUnaryOperator() throws ParseException {
            for (Map.Entry<String, SupportedUnaryOperations> i : supportedUnaryOps.entrySet()) {
                if (take(i.getKey())) {
                    testAlphanumericTagAmbiguity(i.getKey());
                    return i.getValue();
                }
            }
            return null;
        }

        record SupportedBinaryOperations(
                String name,
                BiFunction<PriorityExpression, PriorityExpression, PriorityExpression> expFactory,
                int priority
        ) {
        }

        record SupportedUnaryOperations(
                Function<PriorityExpression, PriorityExpression> expFactory,
                int priority
        ) {
        }
    }
}
