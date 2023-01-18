package deque;

public class LinkedListDeque<Item> implements Deque<Item> {
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

        public LinkedListDeque(Item x) {
            sentinel = new Node(null, null, null);
            Node node = new Node(x, sentinel, sentinel);
            sentinel.next = node;
            sentinel.prev = node;
            size = 1;
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
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public int size() {
            return size;
        }

        /** Prints the items in the deque from first to last. */
        @Override
        public void printDeque() {
            for (Node node = sentinel.next; node.item != null; node = node.next) {
                System.out.print(node.item + " ");
            }
            System.out.println();
        }

        /** Removes and returns the item at the front of the deque. */
        @Override
        public Item removeFirst() {
            Node node = sentinel.next;
            /** If nothing exists, returns null. Why node == null not working? */
            if (node.item == null) {
                return null;
            }
            sentinel.next = node.next;
            node.next.prev = sentinel;
            size = size - 1;
            Item returnItem = node.item;
            node.prev = null;
            node.next = null;
            node.item = null;
            return returnItem;
        }

        @Override
        public Item removeLast() {
            Node node = sentinel.prev;
            if (node.item == null) {
                return null;
            }
            sentinel.prev = node.prev;
            node.prev.next = sentinel;
            size = size - 1;
            Item returnItem = node.item;
            node.prev = null;
            node.next = null;
            node.item = null;
            return returnItem;
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

         /** Same as get, but uses recursion. */
         /**
         public Item getRecursive(int index) {
            if (index < 0 || index >= size) {
                return null;
            }
         }
         */

        public static void main(String[] args) {
            LinkedListDeque<Integer> lld = new LinkedListDeque<>();
            System.out.println("removeFirst on empty list " + lld.removeFirst());
            lld.addFirst(15);
            lld.addLast(22);
            lld.addLast(29);
            lld.printDeque();
            System.out.println("The item on index 2 is " + lld.get(2));
            int a = lld.removeFirst();
            System.out.println("removeFirst " + a);
            lld.printDeque();
            System.out.println("The item on index 200 is " + lld.get(200));
        }
}