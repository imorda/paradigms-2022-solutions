package search;

public class BinarySearchMin {
    // Pre:  a initialized, \exists x=0..a.length : \forall i=0..x-1 a[x] - a[i] <= 0, \forall i=x..a.length-1  a[i] - a[x] > 0,
    //       l >= -1 exclusive search left border, a[l + 1] - a[l] <= 0 (or undefined),
    //       r < a.length inclusive search right border, a[r + 1] - a[r] > 0 (or undefined),
    //       r > l
    //       a elements - integer-formatted decimal numeric string
    // Post: \forall i=0..R-1  a[R] - a[i] <= 0, \forall i=R..a.length-1  a[i] - a[R] > 0, a[R] exists.
    public static int binSearchMinIter(final String[] a, int l, int r) {
        // Monoton: \exists x : a[x+1] - a[x] <= 0 \implies \forall i=0..x a[i+1] - a[i] < 0
        //          \exists x : a[x+1] - a[x] > 0 \implies \forall i=x..a.length-2 a[i+1] - a[i] < 0

        // Inv: r > l, a[l + 1] - a[l] <= 0 (or undefined),
        //      a[r + 1] - a[r] > 0 (or undefined)
        while(r - l > 1){
            // Inv && (r - l > 1)
            int m = l + (r - l) / 2;
            // (r - l > 1) -> ((r - l) / 2 >= 1) -> m > l,
            // (l + (r - l) / 2) < (l + (r - l)) = r -> m < r.

            // Inv && (r - l > 1) && l < m < r
            if(Integer.parseInt(a[m + 1]) - Integer.parseInt(a[m]) > 0){ //a[m+1] defined, because m < r, r is inclusive
                // Inv && a[m + 1] - a[m] > 0
                r = m;
                // r > l, a[r + 1] - a[r] > 0 => Inv satisfied
            } else {
                // Inv && a[m + 1] - a[m] <= 0
                l = m;
                // r > l, a[l + 1] - a[l] <= 0 => Inv satisfied
            }
            // (l < m < r) -> r' - l' < r-l -> loop is NOT endless.
        }
        // r > l, a[l + 1] - a[l] <= 0 (or undefined) && Monoton, (r - l <= 1),
        // a[r + 1] - a[r] > 0 -> (r != l)) && (r > l) ->
        // -> (r - l = 1) -> a[r] - a[r - 1] > target && Monoton -> \forall x \in [0, r - 1]
        //      a[x + 1] - a[x] > 0
        // Equiv: \forall i=0..R-1  a[R] - a[i] <= 0, \forall i=R..a.length-1  a[i] - a[R] > 0, a[R] exists.
        return r;
    }

    // Pre:  \exists x=0..a.length : \forall i=0..x-1 a[x] <= a[i], \forall i=x..a.length-1  a[i] > a[x],
    //  Let a[-1] = +inf
    //       l >= -1 exclusive search left border, a[l + 1] <= a[l] (or undefined),
    //       r < a.length inclusive search right border, a[r + 1] > a[r] (or undefined),
    //       r > l
    //       a elements - integer-formatted decimal numeric string
    // Post: \forall i=0..R-1  a[R] <= a[i], \forall i=R..a.length-1  a[i] > a[R], a[R] exists.
    public static int binSearchMinRec(final String[] a, final int l, final int r) {
        // Inv: r > l, a[l + 1] <= a[l] (or undefined),
        //    a[r + 1] > a[r] (or undefined)
        // Monotone: \exists x : a[x+1] - a[x] <= 0 \implies \forall i=0..x a[i+1] - a[i] < 0
        //    \exists x : a[x+1] - a[x] > 0 \implies \forall i=x..a.length-2 a[i+1] - a[i] < 0
        if (r - l <= 1) {
            // InvA && (r - l <= 1) -> l != r -> r - l = 1 ->
            // a[r] - a[r - 1] > target && Monoton -> \forall x \in [0, r - 1]
            //      a[x + 1] - a[x] > 0
            // Inv satisfies Monoton
            // Equiv: \forall i=0..R-1  a[R] - a[i] <= 0, \forall i=R..a.length-1  a[i] - a[R] > 0, a[R] exists.
            return r;
        } else {
            // Inv

            int m = l + (r - l) / 2;
            // (r - l > 1) -> ((r - l) / 2 >= 1) -> m > l,
            // (l + (r - l) / 2) < (l + (r - l)) = r -> m < r.

            // Inv && (r - l > 1) && l < m < r
            if (Integer.parseInt(a[m + 1]) > Integer.parseInt(a[m])){
                // m - l < r - l -> recursion is NOT endless
                // a[m + 1] > a[m] > 0 -> a[r' + 1] > a[r'] -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(a, l, m);
            } else {
                // m - l < r - l -> recursion is NOT endless
                // a[m + 1] - a[m] > 0 -> a[l' + 1] - a[l'] <= 0 -> this function call
                //      meets "Pre" requirements
                return binSearchMinRec(a, m, r);
            }
        }
    }

    // Pre: args: string of integer-formatted decimal numeric strings,
    //      \exists x=0..args.length : \forall i=0..x-1 args[x] <= args[i], \forall i=x..args.length-1  args[i] > args[x]
    // Post: args[x] >> stdout
    public static void main(String[] args){
        if(args.length < 1){
            System.err.println("Not enough arguments passed. " +
                    "Usage: Space-separated a elements as arguments");
            return;
        }  // Assume everything below as "else" block
        // args.length >= 1

        // (l = -1 >= -1, args[-1] undefined) && (r = args.length <= args.length, args[args.length] undefined)
        // args.length >= 0 > -1 -> r > l
        int result = binSearchMinIter(args, -1, args.length - 1);
        // \forall i=0..result-1  args[result] - args[i] <= 0, \forall i=result..args.length-1  args[i] - args[result] > 0, args[result] exists
        System.out.println(args[result]);
    }
}
