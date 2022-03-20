package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    public T max() {
        return max(this.comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        int maxPos = 0;
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), get(maxPos)) > 0) {
                maxPos = i;
            }
        }
        return get(maxPos);
    }
}
