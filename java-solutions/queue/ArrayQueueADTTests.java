package queue;

public class ArrayQueueADTTests {
    public static void fill(ArrayQueueADT queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(queue, prefix + i);
        }
    }

    public static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(
                    ArrayQueueADT.size(queue) + " " +
                            ArrayQueueADT.element(queue) + " " +
                            ArrayQueueADT.dequeue(queue)
            );
        }
        System.out.println();
    }

    public static void fillBack(ArrayQueueADT queue, String prefix) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.push(queue, prefix + i);
        }
    }

    public static void dumpBack(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(
                    ArrayQueueADT.size(queue) + " " +
                            ArrayQueueADT.peek(queue) + " " +
                            ArrayQueueADT.remove(queue)
            );
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayQueueADT queue1 = new ArrayQueueADT();
        ArrayQueueADT queue2 = new ArrayQueueADT();

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

        System.out.println(ArrayQueueADT.count(queue2,2) + " " + ArrayQueueADT.count(queue2,"s_2_2"));
        System.out.println(ArrayQueueADT.count(queue1,2) + " " + ArrayQueueADT.count(queue1,"s_1_2"));

        dumpBack(queue1);
        dumpBack(queue2);

        fill(queue1, "s_1_");
        fillBack(queue1, "s_1_");
        fill(queue2, "s_2_");
        fillBack(queue2, "s_2_");

        ArrayQueueADT.clear(queue1);
        System.out.println(ArrayQueueADT.size(queue1));
        dumpBack(queue1);
    }
}
