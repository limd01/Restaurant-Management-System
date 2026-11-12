import java.util.NoSuchElementException;

public class DLinkedDeque<T> {

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DLinkedDeque() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Adds an element to the front of the deque.
     */
    public void addFirst(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to deque.");
        }
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    /**
     * Adds an element to the back of the deque.
     */
    public void addLast(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null item to deque.");
        }
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the element at the front of the deque.
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty.");
        }
        T data = head.data;
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Removes and returns the element at the back of the deque.
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty.");
        }
        T data = tail.data;
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    /**
     * Returns the element at the front without removing it, or null if empty.
     */
    public T peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return head.data;
    }

    /**
     * Returns the element at the back without removing it, or null if empty.
     */
    public T peekLast() {
        if (isEmpty()) {
            return null;
        }
        return tail.data;
    }

    /**
     * Returns true if the deque contains no elements.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the deque.
     */
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the deque.
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
