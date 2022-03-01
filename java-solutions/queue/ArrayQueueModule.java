package queue;

/*
Model: a_[1]..a_[end]
Inv: for i=1..end: a[i] != null

immutable(r): for i=1..r: a'[i] == a[i]
shift(dir, size): end'=end+(dir*size) && for i=max(1, dir*size)..end+(dir*size): a'[i] = a[i + (-dir * size)]
*/
public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int start = elements.length - 1, end = elements.length - 1;

    // Pred: element != null
    // Post: end' = end + 1 && a[end] = element && immutable(end)
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

    // Pred: end >= 1
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

    // Pred: end >= 1
    // Post: immutable(end) && end' = end && R = a[1]
    public static Object element() {
        assert !isEmpty();

        return elements[start];
    }

    // Pred: true
    // Post: immutable(end) && end' = end && R = end
    public static int size() {
        if(end > start){
            return (start + 1) + (elements.length - end - 1);
        }
        return start - end;
    }

    // Pred: true
    // Post: immutable(end) && end' = end && R = (end >= 1)
    public static boolean isEmpty() {
        return start == end;
    }

    // Pred: true
    // Post: end' = 0
    public static void clear() {
        elements = new Object[5];
        start = elements.length - 1;
        end = elements.length - 1;
    }

    // Pred: element != null
    // Post: shift(1, 1) && a[1] = element
    public static void push(Object element) {
        assert element != null;

        elements[start] = element;
        start++;
        start %= elements.length;

        ensureCapacity();
    }

    // Pred: end >= 1
    // Post: end' = end - 1 && immutable(end') && R = a[end]
    public static Object remove() {
        assert !isEmpty();

        Object value = elements[end];
        elements[end] = null;
        end++;
        end %= elements.length;

        return value;
    }

    // Pred: end >= 1
    // Post: immutable(end) && end' = end && R = a[end]
    public static Object peek() {
        assert !isEmpty();

        return elements[end];
    }

    // Pred: element not null
    // Post: for i=1..end count of elements that meet "a[i].equals(element)"
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
