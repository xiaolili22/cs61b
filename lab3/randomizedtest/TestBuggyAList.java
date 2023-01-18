package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> L1 = new AListNoResizing<>();
      BuggyAList<Integer> L2 = new BuggyAList<>();

      L1.addLast(8);
      L1.addLast(5);
      L1.addLast(9);

      L2.addLast(8);
      L2.addLast(5);
      L2.addLast(9);

      assertEquals(L1.size(), L2.size());
      assertEquals(L1.removeLast(), L2.removeLast());
      assertEquals(L1.removeLast(), L2.removeLast());
      assertEquals(L1.removeLast(), L2.removeLast());
    }

    @Test
    public void randomizedTest() {
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> buggy = new BuggyAList<>();

      int N = 5000;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 4);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 100);
          L.addLast(randVal);
          buggy.addLast(randVal);
        } else if (operationNumber == 1) {
          // getLast
          if (L.size() == 0 || buggy.size() == 0) {
            continue;
          }
          assertEquals(L.getLast(), buggy.getLast());
        } else if (operationNumber == 2) {
          // removeLast
          if (L.size() == 0 || buggy.size() == 0) {
            continue;
          }
          assertEquals(L.removeLast(), buggy.removeLast());
        } else if (operationNumber == 3) {
          // size
          assertEquals(L.size(), buggy.size());
        }
      }
    }
}
