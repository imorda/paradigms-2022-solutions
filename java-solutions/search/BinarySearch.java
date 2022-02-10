package search;

public class BinarySearch {

    // Pre:  array initialized, elements sorted in descending order,
    //       lBorder >= -1 exclusive search left border, array[lBorder] > target (or undefined),
    //       rBorder <= array.length exclusive search right border, array[rBorder] <= target (or undefined),
    //       rBorder > lBorder
    // Post: \forall \eps \in N : R-\eps >= 0    array[R-\eps] > target, array[R] <= target (or undefined)
    public static int binSearchIter(final int target, final int[] array, int lBorder, int rBorder) {
        // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target
        while(rBorder - lBorder > 1){
            // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target, (rBorder - lBorder > 1)
            int middle = lBorder + (rBorder - lBorder) / 2;
            // (rBorder - lBorder > 1) -> ((rBorder - lBorder) / 2 >= 1) -> middle > lBorder,
            // (lBorder + (rBorder - lBorder) / 2) < (lBorder + (rBorder - lBorder)) = rBorder -> middle < rBorder.

            // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target, (rBorder - lBorder > 1), lBorder < middle < rBorder
            if(array[middle] <= target){
                rBorder = middle;
                // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target
            } else {
                lBorder = middle;
                // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target
            }
            // (lBorder < middle < rBorder) -> rBorder' - lBorder' < rBorder-lBorder -> loop is NOT endless.
        }
        // (array[lBorder] > target), (array[rBorder] <= target), (rBorder - lBorder <= 1), (rBorder > lBorder)
        // ((array[lBorder] > target) && (array[rBorder] <= target) -> (rBorder != lBorder)) && (rBorder > lBorder) ->
        //     -> (rBorder - lBorder = 1) -> array[rBorder - 1] > target -> \forall x \in [0, rBorder - 1]    array[x] > target
        // Equiv: \forall \eps \in N : rBorder-\eps >= 0    array[rBorder-\eps] > target, array[rBorder] <= target (or undefined)
        return rBorder;
    }

    // Pre:  array initialized, elements sorted in descending order,
    //       lBorder >= -1 exclusive search left border, array[lBorder] > target,
    //       rBorder <= array.length exclusive search right border, array[rBorder] <= target,
    //       rBorder > lBorder
    // Post: \forall \eps \in N : R-\eps >= 0    array[R-\eps] > target, array[R] <= target (or undefined)
    public static int binSearchRec(final int target, final int[] array, final int lBorder, final int rBorder) {
        // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target
        if(rBorder - lBorder <= 1){
            // (rBorder > lBorder && array[lBorder] > target && array[rBorder] <= target && rBorder - lBorder <= 1) ->
            //     -> rBorder - lBorder = 1 -> array[rBorder - 1] > target -> \forall x \in [0, rBorder - 1]    array[x] > target
            // Equiv: \forall \eps \in N : rBorder-\eps >= 0    array[rBorder-\eps] > target, array[rBorder] <= target (or undefined)
            return rBorder;
        } else {
            // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target, rBorder - lBorder > 1

            int middle = lBorder + (rBorder - lBorder) / 2;
            // (rBorder - lBorder > 1) -> ((rBorder - lBorder) / 2 >= 1) -> middle > lBorder,
            // (lBorder + (rBorder - lBorder) / 2) < (lBorder + (rBorder - lBorder)) = rBorder -> middle < rBorder.

            // rBorder > lBorder, array[lBorder] > target, array[rBorder] <= target, (rBorder - lBorder > 1), lBorder < middle < rBorder
            if (array[middle] <= target) {
                // middle - lBorder < rBorder - lBorder -> recursion is NOT endless
                // array[middle] <= target -> array[rBorder'] <= target -> this function call meets "Pre" requirements
                return binSearchRec(target, array, lBorder, middle);
            } else {
                // middle - lBorder < rBorder - lBorder -> recursion is NOT endless
                // array[middle] <= target -> array[rBorder'] <= target -> this function call meets "Pre" requirements
                return binSearchRec(target, array, middle, rBorder);
            }
        }
    }

    // Pre: 'x' target int number to search, 'array' of int numbers passed as args. 'array' must be sorted in descending order
    // Post: min index that satisfies "array[index] >= x" is printed to stdout
    public static void main(String[] args) {
        // Pre validation
        if(args.length < 1){
            System.err.println("Not enough arguments passed. " +
                    "Usage: target value as first argument, array elements after");
            return;
        }  // Assume everything below as "else" block
        // args.length >= 1

        int target;

        // Array initialization (args.length >= 1 -> args.length - 1 >= 0)
        int[] array = new int[args.length - 1];

        try{
            target = Integer.parseInt(args[0]);
            for (int i = 0; i < args.length - 1; i++) {
                array[i] = Integer.parseInt(args[i + 1]);
            }
        } catch (NumberFormatException e){
            System.err.println("Invalid numbers passed: " + e.getMessage());
            return;
        }
        // Input is parsed as 'target' and 'array' as ints
        // 'array' is sorted in descending order

        // array initialized, elements sorted in descending order (as pre),
        // (lBorder = -1 >= -1, array[-1] undefined) && (rBorder = array.length <= array.length, array[array.length] undefined) ->
        //     -> search is applied to the whole array
        // min array.length = 0, 0 > -1 -> rBorder > lBorder
        int result = binSearchIter(target, array, -1, array.length);
        // \forall \eps \in N : result-\eps >= 0    array[result-\eps] > target, array[result] <= target (or undefined) -> result is an answer to print
        System.out.println(result);
    }
}
