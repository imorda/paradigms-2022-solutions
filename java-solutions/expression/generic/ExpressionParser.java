package expression.generic;

import expression.exceptions.*;
import expression.parser.BaseParser;
import expression.parser.CharSource;
import expression.parser.StringSource;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ExpressionParser<T extends ExpressionNumber<T, ?>> implements GenericParser<T> {
    private final Function<String, T> strNumFactory;

    public ExpressionParser(final Function<String, T> strNumFactory) {
        this.strNumFactory = strNumFactory;
    }

    @Override
    public GenericExpression<T> parse(final String expression) throws ParseException {
        return new ExpressionParserImpl(expression, strNumFactory).parseExpression();
    }

    private class ExpressionParserImpl extends BaseParser {
        private static final String[] SUPPORTED_VARIABLES = {"x", "y", "z"};
        private final Function<String, T> numbersFactory;
        private final List<SupportedBinaryOperations<T>> supportedBinOps = List.of(
                new SupportedBinaryOperations<>(Add.OPERATION_SYM, Add<T>::new, 20),
                new SupportedBinaryOperations<>(Subtract.OPERATION_SYM, Subtract<T>::new, 20),
                new SupportedBinaryOperations<>(Multiply.OPERATION_SYM, Multiply<T>::new, 10),
                new SupportedBinaryOperations<>(Divide.OPERATION_SYM, Divide<T>::new, 10),
                new SupportedBinaryOperations<>(Max.OPERATION_SYM, Max<T>::new, 30),
                new SupportedBinaryOperations<>(Min.OPERATION_SYM, Min<T>::new, 30)
        );
        private final Map<String, SupportedUnaryOperations<T>> supportedUnaryOps = Map.of(
                Negate.OPERATION_SYM, new SupportedUnaryOperations<T>(Negate::new, -1),
                Count.OPERATION_SYM, new SupportedUnaryOperations<T>(Count::new, -1)
        );

        public ExpressionParserImpl(final CharSource source, final Function<String, T> numFactory) {
            super(source);
            numbersFactory = numFactory;
        }

        public ExpressionParserImpl(final String source, final Function<String, T> numFactory) {
            this(new StringSource(source), numFactory);
        }

        private PriorityExpression<T> parseExpression() throws ParseException {
            final PriorityExpression<T> res = parseOperation();
            skipWhitespace();
            if (eof()) {
                return res;
            }
            throw new EOFException(source);
        }

        private PriorityExpression<T> parseOperation() throws ParseException {
            return parseOperation(Integer.MAX_VALUE);
        }

        private PriorityExpression<T> parseOperation(final int priority) throws ParseException {
            skipWhitespace();

            PriorityExpression<T> leftOperand = parseOperand();
            while (true) {
                final SupportedBinaryOperations<T> operator = takeBinaryOperator(priority);

                if (operator == null) {
                    return leftOperand;
                }

                skipWhitespace();
                final PriorityExpression<T> rightOperand = parseOperation(priority - 1);

                leftOperand = operator.expFactory().apply(leftOperand, rightOperand);
            }
        }

        private PriorityExpression<T> parseOperand() throws ParseException {
            skipWhitespace();
            if (take('(')) {
                final PriorityExpression<T> op = parseOperation();
                if (!take(')')) {
                    throw new ExpressionParseException("expected ')'", source, peekOrEOF());
                }
                return op;
            } else if (test(Character.DECIMAL_DIGIT_NUMBER)) {
                return tryExtractConst(new StringBuilder());
            } else {
                final String varSym = takeVariableSymbol();
                if (varSym != null) {
                    return new Variable<>(varSym);
                } else {
                    final SupportedUnaryOperations<T> operator = takeUnaryOperator();
                    if (operator != null) {
                        if (operator == supportedUnaryOps.get("-") && test(Character.DECIMAL_DIGIT_NUMBER)) {
                            final StringBuilder number = new StringBuilder().append('-');
                            return tryExtractConst(number);
                        }
                        if (test('(')) {
                            return operator.expFactory().apply(new BraceEnclosed<>(parseOperand()));
                        }
                        return operator.expFactory().apply(parseOperand());
                    }
                    throw new InvalidOperandException(source, peekOrEOF());
                }
            }
        }

        private String takeVariableSymbol() {
            for (String i : SUPPORTED_VARIABLES) {
                if (take(i)) {
                    return i;
                }
            }
            return null;
        }

        private PriorityExpression<T> tryExtractConst(final StringBuilder number) throws ParseException {
            takeNumber(number);
            try {
                return new Const<>(numbersFactory.apply(number.toString()));
            } catch (final NumberFormatException e) {
                throw new NumberParseException(source, number.toString());
            }
        }

        private void takeNumber(final StringBuilder sb) throws ParseException {
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
            } else if (!eof()) {
                sb.append(take());
                throw new NumberParseException(source, sb.toString());
            }
            if (take('.')) {
                sb.append('.');
                if (!between('0', '9')) {
                    throw new NumberParseException(source, sb.toString());
                }
                while (between('0', '9')) {
                    sb.append(take());
                }
            }
        }

        private SupportedBinaryOperations<T> takeBinaryOperator(final int priority) throws ParseException {
            skipWhitespace();

            for (final SupportedBinaryOperations<T> i : supportedBinOps) {
                if (i.priority() <= priority && take(i.name())) {
                    testAlphanumericTagAmbiguity(i.name());
                    return i;
                }
            }
            return null;
        }

        private void testAlphanumericTagAmbiguity(final String tag) throws ParseException {
            if (isAlphanumericCharType(getCharType())
                    && isAlphanumericCharType(Character.getType(tag.charAt(tag.length() - 1)))) {
                throw new InvalidOperatorException(source, "illegal characters after " + tag);
            }
        }

        private boolean isAlphanumericCharType(final int type) {
            return type == Character.UPPERCASE_LETTER
                    || type == Character.LOWERCASE_LETTER
                    || type == Character.DECIMAL_DIGIT_NUMBER;
        }

        private SupportedUnaryOperations<T> takeUnaryOperator() throws ParseException {
            for (final Map.Entry<String, SupportedUnaryOperations<T>> i : supportedUnaryOps.entrySet()) {
                if (take(i.getKey())) {
                    testAlphanumericTagAmbiguity(i.getKey());
                    return i.getValue();
                }
            }
            return null;
        }

        record SupportedBinaryOperations<L extends ExpressionNumber<L, ?>>(
                String name,
                BiFunction<PriorityExpression<L>, PriorityExpression<L>, PriorityExpression<L>> expFactory,
                int priority
        ) {
        }

        record SupportedUnaryOperations<L extends ExpressionNumber<L, ?>>(
                Function<PriorityExpression<L>, PriorityExpression<L>> expFactory,
                int priority
        ) {
        }
    }
}
