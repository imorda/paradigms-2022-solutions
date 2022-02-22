package queue;

public class ArrayQueueTests {
    public static void fill(ArrayQueue queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(prefix + i);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    queue.size() + " " +
                            queue.element() + " " +
                            queue.dequeue()
            );
        }
        System.out.println();
    }

    public static void fillBack(ArrayQueue queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            queue.push(prefix + i);
        }
    }

    public static void dumpBack(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(
                    queue.size() + " " +
                            queue.peek() + " " +
                            queue.remove()
            );
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayQueue queue1 = new ArrayQueue();
        ArrayQueue queue2 = new ArrayQueue();

        fill(queue1, "s_1_");
        fillBack(queue1, "s_1_");
        fill(queue2, "s_2_");
        fillBack(queue2, "s_2_");
        dump(queue2);
        dump(queue1);

        fill(queue1, "s_1_");
        fillBack(queue1, "s_1_");
        fill(queue2, "s_2_");
        fillBack(queue2, "s_2_");

        System.out.println(queue2.count(2) + " " + queue2.count("s_2_2"));
        System.out.println(queue1.count(2) + " " + queue1.count("s_2_2"));

        dumpBack(queue1);
        dumpBack(queue2);

        fill(queue1, "s_1_");
        fillBack(queue1, "s_1_");
        fill(queue2, "s_2_");
        fillBack(queue2, "s_2_");

        queue1.clear();
        System.out.println(queue1.size());
        dumpBack(queue1);
    }
}
