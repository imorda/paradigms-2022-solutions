package search;

public class BinarySearchMin {

    // Pre:  array initialized, \exists x=0..array.length : \forall i=0..x-1 array[x] - array[i] <= 0, \forall i=x..array.length-1  array[i] - array[x] > 0,
    //       l >= -1 exclusive search left border, array[l + 1] - array[l] <= 0 (or undefined),
    //       r < array.length inclusive search right border, array[r + 1] - array[r] > 0 (or undefined),
    //       r > l
    // Post: \forall i=0..R-1  array[R] - array[i] <= 0, \forall i=R..array.length-1  array[i] - array[R] > 0, array[R] exists.
    public static int binSearchMinIter(final int[] array, int l, int r) {
        // B: \exists x : array[x+1] - array[x] <= 0 \implies \forall i=0..x array[i+1] - array[i] < 0
        //    \exists x : array[x+1] - array[x] > 0 \implies \forall i=x..array.length-2 array[i+1] - array[i] < 0

        // Inv: r > l, array[l + 1] - array[l] <= 0 (or undefined),
        //      array[r + 1] - array[r] > 0 (or undefined)
        while(r - l > 1){
            // Inv && (r - l > 1)
            int middle = l + (r - l) / 2;
            // (r - l > 1) -> ((r - l) / 2 >= 1) -> middle > l,
            // (l + (r - l) / 2) < (l + (r - l)) = r -> middle < r.

            // Inv && (r - l > 1) && l < middle < r
            if(array[middle + 1] - array[middle] > 0){ //array[middle+1] defined, because middle < r, r is inclusive
                // Inv && array[middle + 1] - array[middle] > 0
                r = middle;
                // r > l, array[r + 1] - array[r] > 0 => Inv satisfied
            } else {
                // Inv && array[middle + 1] - array[middle] <= 0
                l = middle;
                // r > l, array[l + 1] - array[l] <= 0 => Inv satisfied
            }
            // (l < middle < r) -> r' - l' < r-l -> loop is NOT endless.
        }
        // r > l, array[l + 1] - array[l] <= 0 (or undefined) && B, (r - l <= 1),
        // array[r + 1] - array[r] > 0 -> (r != l)) && (r > l) ->
        // -> (r - l = 1) -> array[r] - array[r - 1] > target && B -> \forall x \in [0, r - 1]
        //      array[x + 1] - array[x] > 0
        // Equiv: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined)
        return r;
    }

    // Pre:  array initialized, \exists x=0..array.length : \forall i=0..x-1 array[x] - array[i] <= 0, \forall i=x..array.length-1  array[i] - array[x] > 0,
    //       l >= -1 exclusive search left border, array[l + 1] - array[l] <= 0 (or undefined),
    //       r < array.length inclusive search right border, array[r + 1] - array[r] > 0 (or undefined),
    //       r > l
    // Post: \forall i=0..R-1  array[R] - array[i] <= 0, \forall i=R..array.length-1  array[i] - array[R] > 0, array[R] exists.
    public static int binSearchMinRec(final int[] array, final int l, final int r) {
        // A: r > l, array[l + 1] - array[l] <= 0 (or undefined),
        //    array[r + 1] - array[r] > 0 (or undefined)
        // B: \exists x : array[x+1] - array[x] <= 0 \implies \forall i=0..x array[i+1] - array[i] < 0
        //    \exists x : array[x+1] - array[x] > 0 \implies \forall i=x..array.length-2 array[i+1] - array[i] < 0
        if (r - l <= 1){
            // A && (r - l <= 1) -> l != r -> r - l = 1 ->
            // array[r] - array[r - 1] > target && B -> \forall x \in [0, r - 1]
            //      array[x + 1] - array[x] > 0
            // A satisfies B
            // Equiv: \forall \eps > 0 \in N : R-\eps >= 0  array[R] - array[R-\eps] <= 0, array[R+1] - array[R] > 0 (or undefined)
            return r;
        } else {
            // A

            int middle = l + (r - l) / 2;
            // (r - l > 1) -> ((r - l) / 2 >= 1) -> middle > l,
            // (l + (r - l) / 2) < (l + (r - l)) = r -> middle < r.

            // A && (r - l > 1) && l < middle < r
            if (array[middle + 1] - array[middle] > 0) {
                // middle - l < r - l -> recursion is NOT endless
                // array[middle + 1] - array[middle] > 0 -> array[r' + 1] - array[r'] > 0 -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(array, l, middle);
            } else {
                // middle - l < r - l -> recursion is NOT endless
                // array[middle + 1] - array[middle] > 0 -> array[l' + 1] - array[l'] <= 0 -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(array, middle, r);
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

        // (l = -1 >= -1, array[-1] undefined) && (r = array.length <= array.length, array[array.length] undefined) ->
        //     -> search is applied to the whole array
        // min array.length = 0, 0 > -1 -> r > l
        int result = binSearchMinIter(array, -1, array.length - 1);
        // \forall \eps \in N : result-\eps >= 0    array[result-\eps] > target, array[result] <= target (or undefined) -> result is an answer to print
        System.out.println(array[result]);
    }
}
