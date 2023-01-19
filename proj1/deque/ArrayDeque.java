package deque;

public class ArrayDeque<Item> {
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

    /** Helper method to find the next index of current one. */
    private int plusOne(int index) {
        if (index + 1 >= items.length) {
            return index + 1 - items.length;
        }
        return index + 1;
    }

    public void addFirst(Item item) {
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size = size + 1;
    }

    public void addLast(Item item) {
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size = size + 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int ref = plusOne(nextFirst);
        for (int i = 0; i < size; i += 1) {
            System.out.print(items[ref] + " ");
            ref = plusOne(ref);
        }
        System.out.println();
    }

    public Item removeFirst() {
        if (size == 0) {
            return null;
        }
        /** Reset the original first to be null. */
        Item returnItem = items[plusOne(nextFirst)];
        /** Null out deleted items. */
        items[plusOne(nextFirst)] = null;
        nextFirst = plusOne(nextFirst);
        size = size - 1;
        return returnItem;
    }

    public Item removeLast() {
        if (size == 0) {
            return null;
        }
        Item returnItem = items[minusOne(nextLast)];
        items[minusOne(nextLast)] = null;
        nextLast = minusOne(nextLast);
        size = size - 1;
        return returnItem;
    }

    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int ref = (plusOne(nextFirst) + index) % items.length;
        return items[ref];
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(6);
        ad.addFirst(4);
        ad.addLast(9);
        ad.printDeque();
        System.out.println(ad.get(1));
        ad.removeLast();
        ad.printDeque();
        System.out.println(ad.get(1));

    }



}