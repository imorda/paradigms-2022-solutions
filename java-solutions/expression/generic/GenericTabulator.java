package expression.generic;

import expression.exceptions.ParseException;

public class GenericTabulator implements Tabulator {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Wrong number of arguments passed. Usage: <-number type> expression");
            return;
        }
        String mode = args[0].substring(1);
        StringBuilder expression = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            expression.append(args[i]);
        }

        try {
            Object[][][] ans = new GenericTabulator().tabulate(mode, expression.toString(),
                    -2, 2, -2, 2, -2, 2);

            for (Object[][] i : ans) {
                for (Object[] j : i) {
                    for (Object k : j) {
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
        } catch (ParseException e) {
            System.err.println("Invalid expression passed");
        } catch (IllegalStateException e) {
            System.err.println("Invalid number mode");
        }
    }

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2,
                                 int z1, int z2) throws ParseException, IllegalStateException {
        ExprIntEvaluator<? extends ExpressionNumber<?, ?>> evaluator =
                ExpressionModeSelector.createEvaluatorWithMode(mode, expression);

        Object[][][] ans = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        ans[i - x1][j - y1][k - z1] = evaluator.evaluate(i, j, k).getValue();
                    } catch (ArithmeticException e) {
                        ans[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }

        return ans;
    }
}
