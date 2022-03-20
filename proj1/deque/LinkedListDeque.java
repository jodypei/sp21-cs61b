package deque;

/** Linked list based Deque.
 *  @author 陈国检
 */
public class LinkedListDeque<T> {
    private class DequeNode {
        public T item;
        public DequeNode prev;
        public DequeNode next;

        public DequeNode (T i, DequeNode p, DequeNode n) {
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
        sentinel = new DequeNode(null,null, null);
        sentinel.prev = sentinel.next = sentinel;
    }

    /**　在队头插入元素x　*/
    public void addFirst(T x) {
        size += 1;
        DequeNode newNode = new DequeNode(x, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
    }

    /**　在队尾插入元素x　*/
    public void addLast(T x) {
        DequeNode newNode = new DequeNode(x, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /** 队空返回true，反之false */
    public boolean isEmpty() {
        return (sentinel.next == sentinel) && (sentinel.prev == sentinel);
    }

    /** 返回队列的大小 */
    public int size () { return size; }

    /** 从头到尾打印双端队列中的item */
    public void printDeque() {
        DequeNode curNode = sentinel.next;
        while (curNode.next != sentinel) {
            System.out.println(curNode.item + " ");
            curNode = curNode.next;
        }
        System.out.println("\n");
    }

    /** 删除双端队列头，并返回新的Front */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        DequeNode First = sentinel.next;
        sentinel.next = First.next;
        First.next.prev = sentinel;
        return First.item;

    }

    /** 删除双端队列尾，并返回新的Rear */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size -= 1;
        DequeNode Last = sentinel.prev;
        sentinel.prev = Last.prev;
        Last.prev.next = sentinel;
        return Last.item;
    }

    /** 迭代获取给定index处的item */
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        } else {
            DequeNode cur = sentinel;
            int count = 0;

            /* 迭代到index处 */
            while (count < index) {
                cur = cur.next;
                count += 1;
            }

            return cur.item;
        }
    }
}