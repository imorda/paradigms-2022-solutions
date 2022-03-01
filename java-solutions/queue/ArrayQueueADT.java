package queue;

/*
Model: a_[1]..a_[end]
Inv: for i=1..end: a[i] != null

immutable(r): for i=1..r: a'[i] == a[i]
shift(dir, size): end'=end+(dir*size) && for i=max(1, dir*size)..end+(dir*size): a'[i] = a[i + (-dir * size)]
*/
public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int start = elements.length - 1, end = elements.length - 1;

    // Pred: element != null
    //      data not null
    // Post: end' = end + 1 && a[end] = element && immutable(end)
    public static void enqueue(ArrayQueueADT data, Object element) {
        assert element != null;

        data.elements[data.end] = element;
        data.end--;
        if(data.end < 0){
            data.end = data.elements.length - 1;
        }

        ensureCapacity(data);
    }

    private static void ensureCapacity(ArrayQueueADT data) {
        if(data.end == data.start){
            Object[] newElements = new Object[data.elements.length * 2 + 1];
            System.arraycopy(data.elements, 0, newElements, 0, data.start + 1);
            int rightSegLength = data.elements.length - (data.end + 1);
            System.arraycopy(data.elements, data.end + 1, newElements,
                    newElements.length - rightSegLength, rightSegLength);
            data.elements = newElements;
            data.end = newElements.length - rightSegLength - 1;
        }
    }

    // Pred: end >= 1
    //      data not null
    // Post: shift(-1, 1) && R = a[1]
    public static Object dequeue(ArrayQueueADT data) {
        assert !isEmpty(data);

        Object value = data.elements[data.start];
        data.elements[data.start] = null;
        data.start--;
        if(data.start < 0){
            data.start = data.elements.length - 1;
        }

        return value;
    }

    // Pred: end >= 1
    //      data not null
    // Post: immutable(end) && end' = end && R = a[1]
    public static Object element(ArrayQueueADT data) {
        assert !isEmpty(data);

        return data.elements[data.start];
    }

    // Pred: true
    //      data not null
    // Post: immutable(end) && end' = end && R = end
    public static int size(ArrayQueueADT data) {
        if(data.end > data.start){
            return (data.start + 1) + (data.elements.length - data.end - 1);
        }
        return data.start - data.end;
    }

    // Pred: true
    //      data not null
    // Post: immutable(end) && end' = end && R = (end >= 1)
    public static boolean isEmpty(ArrayQueueADT data) {
        return data.start == data.end;
    }

    // Pred: true
    //      data not null
    // Post: end' = 0
    public static void clear(ArrayQueueADT data) {
        data.elements = new Object[5];
        data.start = data.elements.length - 1;
        data.end = data.elements.length - 1;
    }

    // Pred: element != null
    //      data not null
    // Post: shift(1, 1) && a[1] = element
    public static void push(ArrayQueueADT data, Object element) {
        assert element != null;

        data.elements[data.start] = element;
        data.start++;
        data.start %= data.elements.length;

        ensureCapacity(data);
    }

    // Pred: end >= 1
    //      data not null
    // Post: end' = end - 1 && immutable(end') && R = a[end]
    public static Object remove(ArrayQueueADT data) {
        assert !isEmpty(data);

        Object value = data.elements[data.end];
        data.elements[data.end] = null;
        data.end++;
        data.end %= data.elements.length;

        return value;
    }

    // Pred: end >= 1
    //      data not null
    // Post: immutable(end) && end' = end && R = a[end]
    public static Object peek(ArrayQueueADT data) {
        assert !isEmpty(data);

        return data.elements[data.end];
    }

    // Pred: element not null
    //      data not null
    // Post: for i=1..end count of elements that meet "a[i].equals(element)"
    public static int count(ArrayQueueADT data, Object element){
        assert element != null;
        int ans = 0;
        for (Object x : data.elements) {
            if(x != null && x.equals(element)) {
                ans++;
            }
        }
        return ans;
    }
}
