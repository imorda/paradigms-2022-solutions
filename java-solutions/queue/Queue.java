package queue;

import java.util.function.Function;
import java.util.function.Predicate;

/*
Model: a_[1]..a_[n]
Inv: for i=1..n: a[i] != null
     n >= 0

immutable(r): for i=1..r: a'[i] == a[i]
shift(dir, size): n'=n+(dir*size) && for i=max(1, dir*size)..n+(dir*size): a'[i] = a[i + (-dir * size)]
*/
public interface Queue {
    // Pred: element != null
    // Post: n' = n + 1 && a[n] = element && immutable(n)
    void enqueue(Object element);

    // Pred: n >= 1
    // Post: shift(-1, 1) && R = a[1]
    Object dequeue();

    // Pred: n >= 1
    // Post: immutable(n) && n' = n && R = a[1]
    Object element();

    // Pred: true
    // Post: immutable(n) && n' = n && R = n
    int size();

    // Pred: true
    // Post: immutable(n) && n' = n && R = (n == 0)
    boolean isEmpty();

    // Pred: true
    // Post: n' = 0
    void clear();

    // Pred: func not null
    // Post: immutable(n) && n' = n && R = (for i=1..n: func(a[i]))
    Queue map(Function<Object, Object> func);

    // Pred: pred not null
    // Post: immutable(n) && n' = n && R = { a | pred(a) == true } && for i=1..
    Queue filter(Predicate<Object> pred);
}
