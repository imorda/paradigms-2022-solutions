package expression.generic;

import expression.exceptions.ParseException;

public class GenericTabulator implements Tabulator {
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.err.println("Wrong number of arguments passed. Usage: <-number type> expression");
            return;
        }
        final String mode = args[0].substring(1);
        final StringBuilder expression = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            expression.append(args[i]);
        }

        try {
            final Object[][][] ans = new GenericTabulator().tabulate(mode, expression.toString(),
                    -2, 2, -2, 2, -2, 2);

            for (final Object[][] i : ans) {
                for (final Object[] j : i) {
                    for (final Object k : j) {
                        System.out.print(k);
                        System.out.print(' ');
                    }
                    System.out.println();
                }
                for (int k = 0; k < i[0].length; k++) {
                    System.out.print("--");
                }
                System.out.println();
            }
        } catch (final ParseException e) {
            System.err.println("Invalid expression passed");
        } catch (final IllegalStateException e) {
            System.err.println("Invalid number mode");
        }
    }

    @Override
    public Object[][][] tabulate(
            final String mode, final String expression, final int x1, final int x2, final int y1, final int y2,
            final int z1, final int z2) throws ParseException, IllegalStateException {
        final ExprIntEvaluator<? extends ExpressionNumber<?, ?>> evaluator =
                ExpressionModeSelector.createEvaluatorWithMode(mode, expression);

        final Object[][][] ans = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        ans[i - x1][j - y1][k - z1] = evaluator.evaluate(i, j, k).getValue();
                    } catch (final ArithmeticException e) {
                        ans[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }

        return ans;
    }
}
