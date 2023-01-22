package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

public class DequeRandomizedTest {

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();

        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                ad1.addLast(randVal);
                lld1.addLast(randVal);
            } else if (operationNumber == 1) {
                // get
                if (ad1.size() == 0 || lld1.size() == 0) {
                    continue;
                }
                int randIndex = StdRandom.uniform(0, ad1.size());
                assertEquals(ad1.get(randIndex), lld1.get(randIndex));
            } else if (operationNumber == 2) {
                // removeLast
                if (ad1.size() == 0 || lld1.size() == 0) {
                    continue;
                }
                assertEquals(ad1.removeLast(), lld1.removeLast());
            } else if (operationNumber == 3) {
                // size
                assertEquals(ad1.size(), lld1.size());
            }
        }
    }
}
