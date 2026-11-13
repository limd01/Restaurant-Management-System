import java.util.NoSuchElementException;
import student.TestCase;

public class DLinkedDequeTest extends TestCase {

    private DLinkedDeque<String> deque;

    public void setUp() {
        deque = new DLinkedDeque<String>();
    }

    public void testInitialState() {
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.peekFirst());
        assertNull(deque.peekLast());
    }

    public void testAddFirstAndPeek() {
        deque.addFirst("First");
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());
        assertEquals("First", deque.peekFirst());
        assertEquals("First", deque.peekLast());

        deque.addFirst("NewFirst");
        assertEquals(2, deque.size());
        assertEquals("NewFirst", deque.peekFirst());
        assertEquals("First", deque.peekLast());
    }

    public void testAddLastAndPeek() {
        deque.addLast("First");
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());
        assertEquals("First", deque.peekFirst());
        assertEquals("First", deque.peekLast());

        deque.addLast("Second");
        assertEquals(2, deque.size());
        assertEquals("First", deque.peekFirst());
        assertEquals("Second", deque.peekLast());
    }

    public void testAddNullThrowsException() {
        Exception firstNull = null;
        try {
            deque.addFirst(null);
        } catch (IllegalArgumentException e) {
            firstNull = e;
        }
        assertNotNull(firstNull);

        Exception lastNull = null;
        try {
            deque.addLast(null);
        } catch (IllegalArgumentException e) {
            lastNull = e;
        }
        assertNotNull(lastNull);
    }

    public void testRemoveFirstSingleAndMultiple() {
        deque.addLast("A");
        deque.addLast("B");
        deque.addLast("C");

        assertEquals("A", deque.removeFirst());
        assertEquals(2, deque.size());
        assertEquals("B", deque.peekFirst());

        assertEquals("B", deque.removeFirst());
        assertEquals(1, deque.size());
        assertEquals("C", deque.peekFirst());
        assertEquals("C", deque.peekLast());

        // Removing final remaining element resets both head and tail
        assertEquals("C", deque.removeFirst());
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.peekFirst());
        assertNull(deque.peekLast());
    }

    public void testRemoveLastSingleAndMultiple() {
        deque.addLast("A");
        deque.addLast("B");
        deque.addLast("C");

        assertEquals("C", deque.removeLast());
        assertEquals(2, deque.size());
        assertEquals("B", deque.peekLast());

        assertEquals("B", deque.removeLast());
        assertEquals(1, deque.size());
        assertEquals("A", deque.peekFirst());
        assertEquals("A", deque.peekLast());

        // Removing final remaining element resets both head and tail
        assertEquals("A", deque.removeLast());
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.peekFirst());
        assertNull(deque.peekLast());
    }

    public void testRemoveFromEmptyThrowsException() {
        Exception removeFirstEmpty = null;
        try {
            deque.removeFirst();
        } catch (NoSuchElementException e) {
            removeFirstEmpty = e;
        }
        assertNotNull(removeFirstEmpty);

        Exception removeLastEmpty = null;
        try {
            deque.removeLast();
        } catch (NoSuchElementException e) {
            removeLastEmpty = e;
        }
        assertNotNull(removeLastEmpty);
    }

    public void testClearAndReuse() {
        deque.addFirst("One");
        deque.addLast("Two");
        deque.addFirst("Zero");
        assertEquals(3, deque.size());

        deque.clear();
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.peekFirst());
        assertNull(deque.peekLast());

        // Ensure deque still works properly after clear()
        deque.addLast("Fresh");
        assertEquals(1, deque.size());
        assertEquals("Fresh", deque.peekFirst());
        assertEquals("Fresh", deque.peekLast());
    }
}
