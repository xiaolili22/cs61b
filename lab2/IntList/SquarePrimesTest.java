package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes1() {
        IntList lst = IntList.of(4, 1, 23, 101);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 1 -> 529 -> 10201", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes2() {
        IntList lst = IntList.of(4, 8, 0, 120, 999);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 8 -> 0 -> 120 -> 999", lst.toString());
        assertFalse(changed);
    }

}
