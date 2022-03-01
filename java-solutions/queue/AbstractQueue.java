package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

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

    @Override
    public Queue map(Function<Object, Object> func) {
        assert func != null;
        Queue ret = emptyClone();
        for (int i = 0; i < this.size(); i++) {
            Object element = this.dequeue();
            ret.enqueue(func.apply(element));
            this.enqueue(element);
        }
        return ret;
    }

    @Override
    public Queue filter(Predicate<Object> pred) {
        assert pred != null;
        Queue ret = emptyClone();
        for (int i = 0; i < this.size(); i++) {
            Object element = this.dequeue();
            if (pred.test(element)) {
                ret.enqueue(element);
            }
            this.enqueue(element);
        }
        return ret;
    }

    abstract Queue emptyClone();

    abstract void clearImpl();

    abstract Object elementImpl();

    abstract Object dequeueImpl();

    abstract void enqueueImpl(Object element);
}
