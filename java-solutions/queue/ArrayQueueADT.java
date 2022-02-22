package queue;

/*
Model: a[start]..a[end]
Inv: for i=start..end: a[i] != null

immutable(l, r): for i=l..r: a'[i] == a[i]
*/
public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int start = elements.length - 1, end = elements.length - 1;

    // Pred: element != null
    // Post: end' = end + 1 && start' = start && a[end'] = element && immutable(start, end)
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

    // Pred: end >= start
    // Post: end' = end && start' = start + 1 && immutable(start', end) && R = a[start]
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

    // Pred: end >= start
    // Post: immutable(start, end) && start' = start && end' = end && R = a[start]
    public static Object element(ArrayQueueADT data) {
        assert !isEmpty(data);

        return data.elements[data.start];
    }

    // Pred: true
    // Post: immutable(start, end) && start' = start && end' = end && R = end - start + 1
    public static int size(ArrayQueueADT data) {
        if(data.end > data.start){
            return (data.start + 1) + (data.elements.length - data.end - 1);
        }
        return data.start - data.end;
    }

    // Pred: true
    // Post: immutable(start, end) && start' = start && end' = end && R = (end - start + 1 == 0)
    public static boolean isEmpty(ArrayQueueADT data) {
        return data.start == data.end;
    }

    // Pred: true
    // Post: start < end
    public static void clear(ArrayQueueADT data) {
        data.elements = new Object[5];
        data.start = data.elements.length - 1;
        data.end = data.elements.length - 1;
    }
}
