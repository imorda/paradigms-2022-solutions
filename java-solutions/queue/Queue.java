package queue;

/*
Model: a_[1]..a_[end]
Inv: for i=1..end: a[i] != null

immutable(r): for i=1..r: a'[i] == a[i]
shift(dir, size): end'=end+(dir*size) && for i=max(1, dir*size)..end+(dir*size): a'[i] = a[i + (-dir * size)]
*/
public interface Queue {
    // Pred: element != null
    // Post: end' = end + 1 && a[end] = element && immutable(end)
    void enqueue(Object element);

    // Pred: end >= 1
    // Post: shift(-1, 1) && R = a[1]
    Object dequeue();

    // Pred: end >= 1
    // Post: immutable(end) && end' = end && R = a[1]
    Object element();

    // Pred: true
    // Post: immutable(end) && end' = end && R = end
    int size();

    // Pred: true
    // Post: immutable(end) && end' = end && R = (end == 0)
    boolean isEmpty();

    // Pred: true
    // Post: end' = 0
    void clear();
}
