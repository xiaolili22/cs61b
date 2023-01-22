package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Deque<Item>, Iterable<Item> {
    private class Node {
        public Item item;
        public Node prev;
        public Node next;

        public Node(Item i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    /** The first item if exists is at sentinel.next. */
    private Node sentinel;
    private int size;

    /** Create an empty LinkedListDeque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        /** Make the sentinel circular */
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /** Ad x to the front to the list. */
    @Override
    public void addFirst(Item x) {
        Node node = new Node(x, sentinel, sentinel.next);
        sentinel.next.prev = node;
        sentinel.next = node;
        size = size + 1;
    }

    @Override
    public void addLast(Item x) {
        Node node = new Node(x, sentinel.prev, sentinel);
        sentinel.prev.next = node;
        sentinel.prev = node;
        size = size + 1;
    }

    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last. */
    @Override
    public void printDeque() {
        /** Use size to track the iteration.
         * In case in the future to support adding node with null item. */
        Node node = sentinel.next;
        for (int i = 0; i < size; i += 1) {
            System.out.print(node.item + " ");
            node = node.next;
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. */
    @Override
    public Item removeFirst() {
        /** If nothing exists, returns null. */
        if (size == 0) {
            return null;
        }

        Node node = sentinel.next;
        sentinel.next = node.next;
        node.next.prev = sentinel;
        size = size - 1;
        return node.item;
    }

    @Override
    public Item removeLast() {
        if (size == 0) {
            return null;
        }

        Node node = sentinel.prev;
        sentinel.prev = node.prev;
        node.prev.next = sentinel;
        size = size - 1;
        return node.item;
    }

    @Override
    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node node = sentinel.next;
        for (int i = 0; i < index; i += 1) {
            node = node.next;
        }
        return node.item;
    }

     /** Same as get, but uses recursion.
      * User helper method to track the index. */
     public Item getRecursive(int index) {
         return getRecursiveHelper(index, 0, sentinel.next);
     }
     private Item getRecursiveHelper(int index, int n, Node curr) {
         if (index < 0 || index >= size) {
             return null;
         }
         if (index == n) {
             return curr.item;
         }
         return getRecursiveHelper(index, n + 1, curr.next);
     }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LinkedListDeque) {
            LinkedListDeque<?> anotherLld = (LinkedListDeque<?>) o;
            if (anotherLld.size() != size()) {
                return false;
            }
            for (int i = 0; i < size(); i += 1) {
                if (anotherLld.get(i) != get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Iterator<Item> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<Item> {
         private int wizPos;
         public LLDequeIterator() {
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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(15);
        lld1.addLast(29);
        System.out.println(lld1.getRecursive(0));
        lld1.printDeque();

        LinkedListDeque<String> lld2 = new LinkedListDeque<>();
        lld2.addFirst("Beautiful");
        lld2.addLast("day!");
        for (String i : lld2) {
            System.out.println(i);
        }

    }
}