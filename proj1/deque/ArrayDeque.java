package deque;

/** Array based Deque.
 *  @author 陈国检
 */
public class ArrayDeque<T> {
    /**
     * @param nextFirst: 下一个队头位置索引
     * @param nextLast: 下一个队尾位置索引
     */
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** 创建一个空列表，初始长度为8. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    /** 在nextFirst位置插入元素 */
    public void addFirst(T x) {
        checkFull();

        size += 1;
        items[nextFirst] = x;
        nextFirst = stepBackward(nextLast);
    }

    /** 在nextLast位置插入元素 */
    public void addLast(T x) {
        checkFull();

        size += 1;
        items[nextLast] = x;
        nextLast = stepForward(nextLast);
    }

    /** 确认nextFirst */
    private int stepBackward(int param) {
        return (param + items.length - 1) % items.length;
    }

    /** 确认nextLast */
    private int stepForward(int param) {
        return (param + 1) % items.length;
    }

    /** 判空 */
    public boolean isEmpty() {
        return size() == 0;
    }

    /** 返回数组的大小 */
    public int size() { return size; }

    /** 数组长度不够就增加空间 */
    private void checkFull() {
        if (size == items.length) {
            resize(size * 2);
        }
    }

    /** 从头到尾输出双端队列元素 */
    public void printDeque() {
        int curElem = stepForward(nextFirst);
        for (int i = 0; i < size; i += 1) {
            System.out.println(items[curElem] + " ");
            curElem = stepForward(curElem);
        }
        System.out.println("\n");
    }

    /** 调整底层数组的大小到合适的容量. */
    private void resize(int capacity) {
        T[] tempArray = (T[]) new Object[capacity];

        int curElem = stepForward(nextFirst);
        for (int i = 0; i < size; i += 1) {
            tempArray[i] = items[curElem];
            curElem = stepForward(curElem);
        }

        nextFirst = capacity - 1; /* 新的队头应该在数组尾部 */
        nextLast = size;
        items = tempArray;
    }

    /** 获取双端队列尾部元素  */
    private T getLast() {
        int curLast = stepBackward(nextLast);
        return items[curLast];
    }

    /** 获取双端队列尾部元素  */
    private T getFirst() {
        int curFirst = stepForward(nextFirst);
        return items[curFirst];
    }

    /** 删除双端队列队头的元素，并返回所删除的元素 */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (size < items.length / 4)) {
            resize(items.length / 4);
        }

        nextFirst = stepForward(nextFirst);
        T first = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return first;
    }

    /** 删除双端队列队尾的元素，并返回所删除的元素 */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if ((items.length >= 16) && (size < items.length / 4)) {
            resize(items.length / 4);
        }

        nextLast = stepBackward(nextLast);
        T last = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return last;
    }

    /**
     * 存取数组中下标为i的元素
     * @param index: 取值范围:0 ~ size-1, 0表示队头元素, 1表示第二个元素, 依次类推
     */
    public T get(int index) {
        return items[(nextFirst + index + 1) % items.length];
    }
}
