package queue;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[5];
    private int start = elements.length - 1, end = elements.length - 1;

    @Override
    public void enqueueImpl(Object element) {
        elements[end] = element;
        end--;
        if (end < 0) {
            end = elements.length - 1;
        }

        ensureCapacity();
    }


    @Override
    public Object dequeueImpl() {
        Object value = elements[start];
        elements[start] = null;
        start--;
        if (start < 0) {
            start = elements.length - 1;
        }

        return value;
    }

    @Override
    public Object elementImpl() {
        return elements[start];
    }

    @Override
    public void clearImpl() {
        elements = new Object[5];
        start = elements.length - 1;
        end = elements.length - 1;
    }

    public void push(Object element) {
        assert element != null;

        size++;
        start++;
        start %= elements.length;
        elements[start] = element;

        ensureCapacity();
    }

    public Object remove() {
        assert !isEmpty();

        size--;
        end++;
        end %= elements.length;
        Object value = elements[end];
        elements[end] = null;

        return value;
    }

    public Object peek() {
        assert !isEmpty();

        return elements[(end + 1) % elements.length];
    }

    public int count(Object element) {
        assert element != null;
        int ans = 0;
        for (Object x : elements) {
            if (x != null && x.equals(element)) {
                ans++;
            }
        }
        return ans;
    }

    @Override
    Queue emptyClone() {
        return new ArrayQueue();
    }

    private void ensureCapacity() {
        if (end == start) {
            Object[] newElements = new Object[elements.length * 2 + 1];
            System.arraycopy(elements, 0, newElements, 0, start + 1);
            int rightSegLength = elements.length - (end + 1);
            System.arraycopy(elements, end + 1, newElements,
                    newElements.length - rightSegLength, rightSegLength);
            elements = newElements;
            end = newElements.length - rightSegLength - 1;
        }
    }
}
