package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public static class numComparator implements Comparator<Integer> {
        public int compare(Integer i1, Integer i2) {
            return i1.compareTo(i2);
        }
    }
    public static class strComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }

    public static class arrayLenComparator implements Comparator<int[]> {
        public int compare(int[] a1, int[] a2) {
            if (a1.length > a2.length) {
                return 1;
            } else if (a1.length < a2.length) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Test
    public void maxNumTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new numComparator());
        mad1.addFirst(6);
        mad1.addFirst(19);
        mad1.addLast(35);
        assertEquals(35, (int) mad1.max());
    }

    @Test
    public void maxStrTest() {
        MaxArrayDeque<String> mad1 = new MaxArrayDeque<>(new strComparator());
        mad1.addLast("Hello");
        mad1.addLast("how");
        mad1.addLast("are");
        mad1.addLast("you doing");
        mad1.addLast("today?");
        assertEquals("you doing", mad1.max());
    }

    @Test
    public void maxArrayLenTest() {
        MaxArrayDeque<int[]> mad1 = new MaxArrayDeque<>(new arrayLenComparator());
        mad1.addLast(new int[]{1, 2, 3, 4});
        mad1.addLast(new int[]{1, 2, 3, 4, 5});
        mad1.addLast(new int[]{1, 2});
        assertEquals(mad1.max(), mad1.get(1));
    }

    @Test
    public void equalTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new numComparator());
        mad1.addFirst(6);
        mad1.addFirst(19);
        mad1.addLast(35);

        MaxArrayDeque<Integer> mad2 = new MaxArrayDeque<>(new numComparator());
        mad2.addFirst(6);
        mad2.addFirst(19);
        mad2.addLast(35);
        assertTrue(mad1.equals(mad2));

    }

}