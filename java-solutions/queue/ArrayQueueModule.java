package queue;

/*
Model: a_[1]..a_[n]
Inv: for i=1..n: a[i] != null
     n >= 0

immutable(r): for i=1..r: a'[i] == a[i]
shift(dir, size): n'=n+(dir*size) && for i=max(1, dir*size)..n+(dir*size): a'[i] = a[i + (-dir * size)]
*/
public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int start = elements.length - 1, end = elements.length - 1;

    // Pred: element != null
    // Post: n' = n + 1 && a[n] = element && immutable(n)
    public static void enqueue(Object element) {
        assert element != null;

        elements[end] = element;
        end--;
        if(end < 0){
            end = elements.length - 1;
        }

        ensureCapacity();
    }

    private static void ensureCapacity() {
        if(end == start){
            Object[] newElements = new Object[elements.length * 2 + 1];
            System.arraycopy(elements, 0, newElements, 0, start + 1);
            int rightSegLength = elements.length - (end + 1);
            System.arraycopy(elements, end + 1, newElements,
                    newElements.length - rightSegLength, rightSegLength);
            elements = newElements;
            end = newElements.length - rightSegLength - 1;
        }
    }

    // Pred: n >= 1
    // Post: shift(-1, 1) && R = a[1]
    public static Object dequeue() {
        assert !isEmpty();

        Object value = elements[start];
        elements[start] = null;
        start--;
        if(start < 0){
            start = elements.length - 1;
        }

        return value;
    }

    // Pred: n >= 1
    // Post: immutable(n) && n' = n && R = a[1]
    public static Object element() {
        assert !isEmpty();

        return elements[start];
    }

    // Pred: true
    // Post: immutable(n) && n' = n && R = n
    public static int size() {
        if(end > start){
            return (start + 1) + (elements.length - end - 1);
        }
        return start - end;
    }

    // Pred: true
    // Post: immutable(n) && n' = n && R = (n >= 1)
    public static boolean isEmpty() {
        return start == end;
    }

    // Pred: true
    // Post: n' = 0
    public static void clear() {
        elements = new Object[5];
        start = elements.length - 1;
        end = elements.length - 1;
    }

    // Pred: element != null
    // Post: shift(1, 1) && a[1] = element
    public static void push(Object element) {
        assert element != null;

        start++;
        start %= elements.length;
        ensureCapacity();

        elements[start] = element;
    }

    // Pred: n >= 1
    // Post: n' = n - 1 && immutable(n') && R = a[n]
    public static Object remove() {
        assert !isEmpty();

        end++;
        end %= elements.length;

        Object value = elements[end];
        elements[end] = null;

        return value;
    }

    // Pred: n >= 1
    // Post: immutable(n) && n' = n && R = a[n]
    public static Object peek() {
        assert !isEmpty();

        return elements[(end  +  1) % elements.length];
    }

    // Pred: element not null
    // Post: X={x \in a | x.equals(element) == true} & R=|X|
    public static int count(Object element){
        assert element != null;
        int ans = 0;
        for (Object x : elements) {
            if(x != null && x.equals(element)) {
                ans++;
            }
        }
        return ans;
    }
}
