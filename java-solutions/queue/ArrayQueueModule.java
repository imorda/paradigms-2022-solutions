package queue;

/*
Model: a[start]..a[end]
Inv: for i=start..end: a[i] != null

immutable(r, l): for i=l..r: a'[i] == a[i]
*/
public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int start = elements.length - 1, end = elements.length - 1;

    // Pred: element != null
    // Post: end' = end + 1 && start' = start && a[end] = element && immutable(start, end)
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

    // Pred: end >= start
    // Post: end' = end && start' = start - 1 && immutable(start', end) && R = a[start]
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

    // Pred: end >= start
    // Post: immutable(start, end) && start' = start && end' = end && R = a[start]
    public static Object element() {
        assert !isEmpty();

        return elements[start];
    }

    // Pred: true
    // Post: immutable(start, end) && start' = start && end' = end && R = end - start + 1
    public static int size() {
        if(end > start){
            return (start + 1) + (elements.length - end - 1);
        }
        return start - end;
    }

    // Pred: true
    // Post: immutable(start, end) && start' = start && end' = end && R = (end - start + 1 == 0)
    public static boolean isEmpty() {
        return start == end;
    }

    // Pred: true
    // Post: start < end
    public static void clear() {
        elements = new Object[5];
        start = elements.length - 1;
        end = elements.length - 1;
    }

    // Pred: element != null
    // Post: start' = start + 1 && end' = end && a[start'] = element && immutable(start, end)
    public static void push(Object element) {
        assert element != null;

        elements[start] = element;
        start++;
        start %= elements.length;

        ensureCapacity();
    }

    // Pred: end >= start
    // Post: end' = end + 1 && start' = start && immutable(start, end') && R = a[end]
    public static Object remove() {
        assert !isEmpty();

        Object value = elements[end];
        elements[end] = null;
        end++;
        end %= elements.length;

        return value;
    }

    // Pred: end >= start
    // Post: immutable(start, end) && start' = start && end' = end && R = a[end]
    public static Object peek() {
        assert !isEmpty();

        return elements[end];
    }

    // Pred: element not null
    // Post: for i=start..end count of elements that meet "a[i].equals(element)"
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
