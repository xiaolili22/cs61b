package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    /** Hash table size. */
    private int tableSize;
    /** Number of key-value pairs. */
    private int size;
    /** loadFactor = size/tableSize. */
    private final double maxLoadFactor;
    /** Defaults */
    private static final int INITIAL_SIZE = 16;
    private static final double MAX_LOAD = 0.75;

    /** Constructors */
    public MyHashMap() {
        this(INITIAL_SIZE, MAX_LOAD);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, MAX_LOAD);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize < 1 || maxLoad <= 0.0) {
            throw new IllegalArgumentException();
        }
        buckets = createTable(initialSize);
        tableSize = initialSize;
        maxLoadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            table[i] = createBucket();
        }
        return table;
    }

    /** Resizes the hash table to the given table size,
     *  rehashing all the keys. */
    private void resize(int newSize) {
        MyHashMap<K, V> temp = new MyHashMap<>(newSize);
        for (int i = 0; i < tableSize; i += 1) {
            for (Node n : buckets[i]) {
                int j = temp.hash(n.key);
                temp.buckets[j].add(n);
            }
        }
        this.buckets = temp.buckets;
        this.tableSize = temp.tableSize;
    }

    /** Hash function for keys - returns value between 0 and size - 1. */
    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % tableSize;
    }

    @Override
    public void clear() {
        buckets = createTable(INITIAL_SIZE);
        tableSize = INITIAL_SIZE;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /** Helper method to get the node with a given key. */
    private Node getNode(K key) {
        int i = hash(key);
        if (i < 0 || i >= tableSize) {
            return null;
        }
        for (Node n : buckets[i]) {
            if (n.key.equals(key)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        Node n = getNode(key);
        if (n == null) {
            return null;
        }
        return n.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int i = hash(key);
        Node n = getNode(key);
        if (n == null) {
            buckets[i].add(createNode(key, value));
            size += 1;
        } else {
            n.value = value;
        }
        double loadFactor = size / tableSize;
        if (loadFactor > maxLoadFactor) {
            resize(tableSize * 2);
        }
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<>();
        for (K key : this) {
            keys.add(key);
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        Node n = getNode(key);
        if (n == null) {
            return null;
        }
        return remove(key, n.value);
    }

    @Override
    public V remove(K key, V value) {
        Node n = getNode(key);
        if (n == null || !n.value.equals(value)) {
            return null;
        }
        int i = hash(key);
        buckets[i].remove(n);
        size -= 1;
        return n.value;
    }

    @Override
    public Iterator<K> iterator() {
        return new KeyItr();
    }

    private class KeyItr implements Iterator<K> {
        int num; // tracks the # of nodes iterated
        int i = 0;
        Iterator<Node> currBucketItr = buckets[0].iterator();

        @Override
        public boolean hasNext() {
            return num < size;
        }

        @Override
        public K next() {
            while (!currBucketItr.hasNext()) {
                i += 1;
                currBucketItr = buckets[i].iterator();
            }
            num += 1;
            return currBucketItr.next().key;
        }
    }
}


