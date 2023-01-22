package deque;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.lang.reflect.Array;
import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>, Iterable<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = items.length / 2;
        nextLast = nextFirst + 1;
    }

    /** Helper method to find the previous index of current one. */
    private int minusOne(int index) {
        if (index - 1 < 0) {
            return index - 1 + items.length;
        }
        return index - 1;
    }

    /** Helper method to find the next index of current one.
     * index is the current index, n is the current array's length. */
    private int plusOne(int index, Item[] a) {
        if (index + 1 >= a.length) {
            return index + 1 - a.length;
        }
        return index + 1;
    }

    @Override
    public void addFirst(Item item) {
        if (size == items.length) {
            resize((int) (size * 1.5));
        }
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size = size + 1;
    }

    @Override
    public void addLast(Item item) {
        if (size == items.length) {
            resize((int) (size * 1.5));
        }
        items[nextLast] = item;
        nextLast = plusOne(nextLast, items);
        size = size + 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int ref = plusOne(nextFirst, items);
        for (int i = 0; i < size; i += 1) {
            System.out.print(items[ref] + " ");
            ref = plusOne(ref, items);
        }
        System.out.println();
    }

    @Override
    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        /** For array of length 16 or more,
         * check the usage of the array,
         * if below 25%, resize down to half of the original length. */
        if (items.length > 16 && size - 1 < items.length * 0.25 ) {
            resize(items.length / 2);
        }
        /** Reset the original first to be null. */
        Item returnItem = items[plusOne(nextFirst, items)];
        /** Null out deleted items. */
        items[plusOne(nextFirst, items)] = null;
        nextFirst = plusOne(nextFirst, items);
        size = size - 1;
        return returnItem;
    }

    @Override
    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        if (items.length > 16 && size - 1 < items.length * 0.25 ) {
            resize(items.length / 2);
        }
        Item returnItem = items[minusOne(nextLast)];
        items[minusOne(nextLast)] = null;
        nextLast = minusOne(nextLast);
        size = size - 1;
        return returnItem;
    }

    @Override
    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int ref = (plusOne(nextFirst, items) + index) % items.length;
        return items[ref];
    }
    private void resize(int capacity) {
        Item[] resizedArray = (Item[]) new Object[capacity];
        int resizedNextFirst = resizedArray.length / 2;
        int resizedNextLast = resizedNextFirst + 1;
        /** Copy items in original array to the new array. */
        int resizedRef = plusOne(resizedNextFirst, resizedArray);
        int ref = plusOne(nextFirst, items);
        for (int i = 0; i < size; i += 1) {
            resizedArray[resizedRef] = items[ref];
            ref = plusOne(ref, items);
            resizedRef = plusOne(resizedRef, resizedArray);
            resizedNextLast =plusOne(resizedNextLast, resizedArray);
        }

        nextFirst = resizedNextFirst;
        nextLast = resizedNextLast;
        items = resizedArray;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque ad1)) {
            return false;
        }
        if (ad1.size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i += 1) {
            if (ad1.get(i) != get(i)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<Item> {
        private int wizPos;
        public ArrayDequeIterator() {
            wizPos = 0;
        }
        public boolean hasNext() {
            return wizPos < size;
        }
        public Item next() {
            Item returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(16);
        ad.addFirst(14);
        ad.addLast(59);
        System.out.println(ad.get(1));

        for (int i : ad) {
            System.out.println(i);
        }
    }

}