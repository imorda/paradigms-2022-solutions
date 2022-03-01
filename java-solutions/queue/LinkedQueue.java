package queue;

public class LinkedQueue extends AbstractQueue {
    private Node start;
    private Node end;

    @Override
    public void enqueueImpl(Object element) {
        Node current = new Node(element, end, null);
        if(start == null || end == null){
            start = current;
        } else {
            end.next = current;
        }
        end = current;
    }

    @Override
    public Object dequeueImpl() {
        Node current = start;
        start = current.next;
        if(start == null){
            end = null;
        } else {
            start.prev = null;
        }
        return current.value;
    }

    @Override
    public Object elementImpl() {
        return start.value;
    }

    @Override
    public void clearImpl() {
        start = null;
        end = null;
    }

    private static class Node {
        private final Object value;
        private Node prev;
        private Node next;

        public Node(Object value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
