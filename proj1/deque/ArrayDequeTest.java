package deque;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic array tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that array list is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeque with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  ad1 = new ArrayDeque<>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigArrayDequeTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    /** Add elements to the deque, check if get method returns the correct item. */
    public void getTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        assertEquals("Empty deque, get from index 0 should return null", ad1.get(0), null);

        ad1.addFirst(8);
        ad1.addFirst(3);
        ad1.addLast(15);
        ad1.addLast(19);
        assertEquals("Get from index 0 should return 3", (int) ad1.get(0), 3);
        assertEquals("Get from index 1 should return 8", (int) ad1.get(1), 8);
        assertEquals("Get from index 2 should return 15", (int) ad1.get(2), 15);
        assertEquals("Not valid index, get from index 100 should return null", ad1.get(100), null);
    }

    @Test
    public void testResize() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        int i;
        for (i = 0; i < 32; i += 1) {
            ad1.addLast(i);
        }
        for (i = 0; i < 30; i += 1) {
            ad1.removeLast();
        }
        for (i = 0; i < 32; i += 1) {
            ad1.addFirst(i + 100);
        }
        for (i = 0; i < 30; i += 1) {
            ad1.removeFirst();
        }
    }

    @Test
    public void testIsEqual() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        assertEquals(ad1, ad2);

        ad1.addFirst(1);
        ad1.addFirst(2);
        assertNotEquals(ad1, ad2);

        ad2.addFirst(1);
        assertNotEquals(ad1, ad2);
        ad2.addFirst(2);
        assertEquals(ad1, ad2);

        ArrayDeque<Integer> ad5 = ad1;
        assertEquals(ad1, ad5);
    }

    @Test
    public void equalWithLinkedListDeque() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        assertEquals(ad1, lld1);

        ad1.addFirst(1);
        ad1.addFirst(2);
        assertNotEquals(ad1, lld1);

        lld1.addFirst(1);
        lld1.addFirst(2);
        assertEquals(ad1, lld1);

        ArrayDeque<int[]> ad3 = new ArrayDeque<>();
        ArrayDeque<int[]> ad4 = new ArrayDeque<>();
        ad3.addFirst(new int[]{1, 2, 3});
        ad3.addLast(new int[]{4, 5, 6});
        ad4.addFirst(new int[]{1, 2, 3});
        ad4.addLast(new int[]{4, 5, 6});
        assertEquals(ad3, ad4);
    }
}
