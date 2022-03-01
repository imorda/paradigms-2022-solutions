package queue;

public abstract class AbstractQueue implements Queue {
    private int size = 0;

    @Override
    public void enqueue(Object element) {
        assert element != null;
        size++;
        enqueueImpl(element);
    }

    @Override
    public Object dequeue() {
        assert !isEmpty();
        size--;
        return dequeueImpl();
    }

    @Override
    public Object element() {
        assert !isEmpty();
        return elementImpl();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;
        clearImpl();
    }

    abstract void clearImpl();

    abstract Object elementImpl();

    abstract Object dequeueImpl();

    abstract void enqueueImpl(Object element);
}
