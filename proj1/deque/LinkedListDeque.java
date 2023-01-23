package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(T i, Node p, Node n) {
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
    public void addFirst(T x) {
        Node node = new Node(x, sentinel, sentinel.next);
        sentinel.next.prev = node;
        sentinel.next = node;
        size = size + 1;
    }

    @Override
    public void addLast(T x) {
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
    public T removeFirst() {
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
    public T removeLast() {
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
    public T get(int index) {
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
    public T getRecursive(int index) {
        return getRecursiveHelper(index, 0, sentinel.next);
    }
    private T getRecursiveHelper(int index, int n, Node curr) {
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
        /** LinkedListDeque and ArrayDeques with the same elements should be equal. */
        if (o instanceof Deque) {
            Deque<?> anotherD = (Deque<?>) o;
            if (anotherD.size() != size) {
                return false;
            }
            for (int i = 0; i < size; i += 1) {
                if (!Objects.deepEquals(anotherD.get(i), get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
        private int wizPos;
        LLDequeIterator() {
            wizPos = 0;
        }
        public boolean hasNext() {
            return wizPos < size;
        }
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }
}

