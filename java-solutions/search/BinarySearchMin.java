package search;

public class BinarySearchMin {

    // Pre:  array initialized, elements sorted in descending order up to some point strict ascending after,
    //       lBorder >= -1 exclusive search left border, array[lBorder + 1] - array[lBorder] <= 0 (or undefined),
    //       rBorder < array.length inclusive search right border, array[rBorder + 1] - array[rBorder] > 0 (or undefined),
    //       rBorder > lBorder
    // Post: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined), array[R] exists.
    public static int binSearchMinIter(final int[] array, int lBorder, int rBorder) {
        // Inv: rBorder > lBorder, array[lBorder + 1] - array[lBorder] <= 0 (or undefined),
        //      array[rBorder + 1] - array[rBorder] > 0 (or undefined)
        while(rBorder - lBorder > 1){
            // Inv && (rBorder - lBorder > 1)
            int middle = lBorder + (rBorder - lBorder) / 2;
            // (rBorder - lBorder > 1) -> ((rBorder - lBorder) / 2 >= 1) -> middle > lBorder,
            // (lBorder + (rBorder - lBorder) / 2) < (lBorder + (rBorder - lBorder)) = rBorder -> middle < rBorder.

            // Inv && (rBorder - lBorder > 1) && lBorder < middle < rBorder
            if(array[middle + 1] - array[middle] > 0){ //array[middle+1] defined, because middle < rBorder, rBorder is inclusive
                // Inv && array[middle + 1] - array[middle] > 0
                rBorder = middle;
                // rBorder > lBorder, array[rBorder + 1] - array[rBorder] > 0 => Inv satisfied
            } else {
                // Inv && array[middle + 1] - array[middle] <= 0
                lBorder = middle;
                // rBorder > lBorder, array[lBorder + 1] - array[lBorder] <= 0 => Inv satisfied
            }
            // (lBorder < middle < rBorder) -> rBorder' - lBorder' < rBorder-lBorder -> loop is NOT endless.
        }
        // rBorder > lBorder, array[lBorder + 1] - array[lBorder] <= 0 (or undefined), (rBorder - lBorder <= 1),
        // array[rBorder + 1] - array[rBorder] > 0 -> (rBorder != lBorder)) && (rBorder > lBorder) ->
        // -> (rBorder - lBorder = 1) -> array[rBorder] - array[rBorder - 1] > target -> \forall x \in [0, rBorder - 1]
        //      array[x + 1] - array[x] > 0
        // Equiv: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined)
        return rBorder;
    }

    // Pre:  array initialized, elements sorted in descending order up to some point strict ascending after,
    //       lBorder >= -1 exclusive search left border, array[lBorder + 1] - array[lBorder] <= 0 (or undefined),
    //       rBorder < array.length inclusive search right border, array[rBorder + 1] - array[rBorder] > 0 (or undefined),
    //       rBorder > lBorder
    // Post: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined), array[R] exists.
    public static int binSearchMinRec(final int[] array, final int lBorder, final int rBorder) {
        // A: rBorder > lBorder, array[lBorder + 1] - array[lBorder] <= 0 (or undefined),
        //    array[rBorder + 1] - array[rBorder] > 0 (or undefined)
        if(rBorder - lBorder <= 1){
            // A && (rBorder - lBorder <= 1) -> lBorder != rBorder -> rBorder - lBorder = 1 ->
            // array[rBorder] - array[rBorder - 1] > target -> \forall x \in [0, rBorder - 1]  array[x + 1] - array[x] > 0
            // Equiv: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined)
            return rBorder;
        } else {
            // A

            int middle = lBorder + (rBorder - lBorder) / 2;
            // (rBorder - lBorder > 1) -> ((rBorder - lBorder) / 2 >= 1) -> middle > lBorder,
            // (lBorder + (rBorder - lBorder) / 2) < (lBorder + (rBorder - lBorder)) = rBorder -> middle < rBorder.

            // A && (rBorder - lBorder > 1) && lBorder < middle < rBorder
            if (array[middle + 1] - array[middle] > 0) {
                // middle - lBorder < rBorder - lBorder -> recursion is NOT endless
                // array[middle + 1] - array[middle] > 0 -> array[rBorder' + 1] - array[rBorder'] > 0 -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(array, lBorder, middle);
            } else {
                // middle - lBorder < rBorder - lBorder -> recursion is NOT endless
                // array[middle + 1] - array[middle] > 0 -> array[lBorder' + 1] - array[lBorder'] <= 0 -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(array, middle, rBorder);
            }
        }
    }

    // Pre: 'array' of int numbers passed as args. 'array' must be sorted in strict descending order up to some point,
    //      strict ascending order after.
    // Post: index of min element in array is printed to stdout
    public static void main(String[] args) {
        // Pre validation
        if(args.length < 1){
            System.err.println("Not enough arguments passed. " +
                    "Usage: Space-separated array elements as arguments");
            return;
        }  // Assume everything below as "else" block
        // args.length >= 1

        // Array initialization (args.length >= 1 -> args.length - 1 >= 0)
        int[] array = new int[args.length];

        try{
            for (int i = 0; i < args.length; i++) {
                array[i] = Integer.parseInt(args[i]);
            }
        } catch (NumberFormatException e){
            System.err.println("Invalid numbers passed: " + e.getMessage());
            return;
        }
        // Input is parsed as 'target' and 'array' as ints
        // 'array' is sorted in strict descending order up to some point strict ascending order after. (by the task definition)

        // (lBorder = -1 >= -1, array[-1] undefined) && (rBorder = array.length <= array.length, array[array.length] undefined) ->
        //     -> search is applied to the whole array
        // min array.length = 0, 0 > -1 -> rBorder > lBorder
        int result = binSearchMinIter(array, -1, array.length - 1);
        // \forall \eps \in N : result-\eps >= 0    array[result-\eps] > target, array[result] <= target (or undefined) -> result is an answer to print
        System.out.println(array[result]);
    }
}
