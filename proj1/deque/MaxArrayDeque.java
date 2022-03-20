package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> Cmp;

    public MaxArrayDeque(Comparator<T> c) {
        this.Cmp = c;
    }

    public T max() {
        return max(this.Cmp);
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
