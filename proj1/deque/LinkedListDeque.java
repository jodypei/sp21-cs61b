package deque;

/** Linked list based Deque.
 *  @author 陈国检
 */
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class DequeNode {
        private T item;
        private DequeNode prev;
        private DequeNode next;

        DequeNode(T i, DequeNode p, DequeNode n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    /* 循环哨兵结点，Front.prev和Rear.next都是该结点 */
    private DequeNode sentinel;
    private int size;

    /** 创建一个用于计时测试的LinkedListDeque. */
    public LinkedListDeque() {
        size = 0;
        sentinel = new DequeNode(null, null, null);
        sentinel.prev = sentinel.next = sentinel;
    }

    /**　在队头插入元素x　*/
    @Override
    public void addFirst(T x) {
        size += 1;
        DequeNode newNode = new DequeNode(x, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
    }

    /**　在队尾插入元素x　*/
    @Override
    public void addLast(T x) {
        DequeNode newNode = new DequeNode(x, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /*
    @Override
    public boolean isEmpty() {
        return (sentinel.next == sentinel) && (sentinel.prev == sentinel);
    }
    */

    /** 返回队列的大小 */
    @Override
    public int size() {
        return size;
    }

    /** 从头到尾打印双端队列中的item */
    @Override
    public void printDeque() {
        DequeNode curNode = sentinel.next;
        while (curNode.next != sentinel) {
            System.out.println(curNode.item + " ");
            curNode = curNode.next;
        }
        System.out.println("\n");
    }

    /** 删除双端队列头，并返回新的Front */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        DequeNode first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        return first.item;

    }

    /** 删除双端队列尾，并返回新的Rear */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        DequeNode last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        return last.item;
    }

    /** 迭代获取队列中第index个item */
    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        } else {
            DequeNode cur = sentinel;
            int count = 0;

            /* 迭代到index处 */
            while (count <= index) {
                cur = cur.next;
                count += 1;
            }

            return cur.item;
        }
    }

    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        } else {
            return getRecursiveHelper(index, sentinel.next);
        }
    }

    private T getRecursiveHelper(int index, DequeNode curNode) {
        if (index == 0) {
            return curNode.item;
        }
        return getRecursiveHelper(index - 1, curNode.next);
    }

    /** 后面将要实现的Deque对象是可迭代的，所以我们需要提供这个方法来返回一个迭代器。*/
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    public class LinkedListIterator implements Iterator<T> {
        private DequeNode cur;
        public LinkedListIterator() {
            cur = sentinel.next;
        }
        public boolean hasNext() {
            return cur != sentinel;
        }
        public T next() {
            T returnItem = (T) cur.item;
            cur = cur.next;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        if (o instanceof ArrayDeque) {
            Deque deckQ = (ArrayDeque) o;
            return equalsHelper(deckQ);
        }
        if (o instanceof LinkedListDeque) {
            Deque deckQ = (LinkedListDeque) o;
            return equalsHelper(deckQ);
        }
        return false;
    }

    private boolean equalsHelper(Deque<?> Q) {
        if (Q.size() != size()) {
            return false;
        }

        DequeNode curNode = sentinel.next;
        for (int i = 0; i < size; i += 1) {
            boolean equals = curNode.item.equals(Q.get(i));
            if (!equals) {
                return false;
            }
            curNode = curNode.next;
        }
        return true;
    }
}
