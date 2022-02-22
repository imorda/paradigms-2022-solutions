package queue;

public class ArrayQueueModuleTests {
    public static void fill() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void fillBack() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.push(i);
        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                    ArrayQueueModule.size() + " " +
                            ArrayQueueModule.element() + " " +
                            ArrayQueueModule.dequeue()
            );
        }
    }

    public static void dumpBack() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                    ArrayQueueModule.size() + " " +
                            ArrayQueueModule.peek() + " " +
                            ArrayQueueModule.remove()
            );
        }
    }

    public static void main(String[] args) {
        fill();
        fillBack();
        dump();

        fill();
        fillBack();

        ArrayQueueModule.count(2);
        ArrayQueueModule.count("2");

        dumpBack();

        fill();
        fillBack();

        ArrayQueueModule.clear();
        System.out.println(ArrayQueueModule.size());
        dump();
    }
}
