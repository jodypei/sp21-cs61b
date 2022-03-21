package deque;

import afu.org.checkerframework.checker.igj.qual.I;
import jh61b.junit.In;
import org.junit.Test;
import java.util.Comparator;
import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    public static class IntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }
    @Test
    public void nullTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator());

        assertEquals("Expected Null.", mad1.max(), null);
    }

    @Test
    public void maxTest() {
        MaxArrayDeque<Integer> mad2 = new MaxArrayDeque<>(new IntComparator());

        mad2.addLast(0);
        mad2.addLast(-3);
        mad2.addLast(5);
        mad2.addLast(999);
        mad2.addLast(54);

        int max = mad2.max();
        assertEquals("Expected 999", 999, max);
    }
}
