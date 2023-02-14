package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable <K>, V> implements Map61B<K, V> {
    /** Root of BSTMap. */
    private BSTNode root;

    /** Represents one node in BSTMap that stores key-value pairs. */
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;

        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    /** Initializes an empty map. */
    public BSTMap() {
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        root = null;
    }

    /** Checks if the map contains the given key. */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode n, K key) {
        if (n == null) {
            return false;
        }

        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return containsKey(n.left, key);
        } else if (cmp > 0) {
            return containsKey(n.right, key);
        } else {
            return true;
        }
    }

    /** Returns the value associated with the given key. */
    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode n, K key) {
        if (n == null) {
            return null;
        }

        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            return get(n.left, key);
        } else if (cmp > 0) {
            return get(n.right, key);
        } else {
            return n.value;
        }
    }

    /** Returns number of key-value pairs in this map. */
    @Override
    public int size() {
        return size(root);
    }

    /** Returns number of key-value pairs in BSTMap rooted at n. */
    private int size(BSTNode n) {
        if (n == null) {
            return 0;
        }
        return n.size;
    }

    /** Inserts the specified key-value pair into the map.
     *  Overwrites the old value if already contains the specified key. */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode n, K key, V value) {
        if (n == null) {
            return new BSTNode(key, value, 1);
        }

        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = put(n.left, key, value);
        } else if (cmp > 0) {
            n.right = put(n.right, key, value);
        } else {
            n.value = value;
        }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode n) {
        /** Prints out BSTMap in order of increasing Key.
         *  Uses in-order traversal. */
        if (n == null) {
            return;
        }
        printInOrder(n.left);
        System.out.println("Key: " + n.key + " Value: " + n.value);
        printInOrder(n.right);
    }
}