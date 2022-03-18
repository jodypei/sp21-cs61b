package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import net.sf.saxon.om.Item;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 * @author Guojian Chen
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AList<Integer> correct = new AList<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        for (int tmp = 0; tmp < 10; tmp++) {
            correct.addLast(tmp);
            buggy.addLast(tmp);
        }

        assertEquals(correct.size(), buggy.size());

        for (int rm = 0; rm < 10; rm++) {
            assertEquals(correct.removeLast(), buggy.removeLast());
        }
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BugL = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BugL.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int BugSize = BugL.size();
                System.out.println("Expected size: " + size);
                assertEquals(size, BugSize);

            } else if (L.size() > 0 && BugL.size() > 0) {
                // getLast & removeLast
                if (operationNumber == 2) {
                    int Last = L.getLast();
                    int BugLast = BugL.getLast();
                    System.out.println("Expected Last: " + Last);
                    assertEquals(Last, BugLast);

                    L.removeLast();
                    BugL.removeLast();
                    System.out.print("Last of List & BuggyList removed!");
                }
            }
        }
    }
}
