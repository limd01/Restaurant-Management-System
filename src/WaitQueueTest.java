import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import student.TestCase;

public class WaitQueueTest extends TestCase {

    private WaitQueue queue;
    private LocalDateTime baseTime;

    public void setUp() {
        queue = new WaitQueue();
        baseTime = LocalDateTime.of(2026, 11, 13, 18, 0);
    }

    public void testConstructorsAndInvalidMaxSize() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());

        WaitQueue customQueue = new WaitQueue(5);
        assertTrue(customQueue.isEmpty());

        Exception zeroSize = null;
        try {
            new WaitQueue(0);
        } catch (IllegalArgumentException e) {
            zeroSize = e;
        }
        assertNotNull(zeroSize);

        Exception negativeSize = null;
        try {
            new WaitQueue(-3);
        } catch (IllegalArgumentException e) {
            negativeSize = e;
        }
        assertNotNull(negativeSize);
    }

    public void testEnqueueAndDequeueNormalParties() {
        Reservation r1 = new Reservation("Alice", 2, false, baseTime);
        Reservation r2 = new Reservation("Bob", 4, false, baseTime.plusMinutes(5));

        queue.enqueue(r1);
        queue.enqueue(r2);

        assertFalse(queue.isEmpty());
        assertEquals(2, queue.size());
        assertEquals(r1, queue.peek());

        assertEquals(r1, queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals(r2, queue.peek());

        assertEquals(r2, queue.dequeue());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    public void testEnqueueVIPOrderingAllBranches() {
        Reservation reg1 = new Reservation();
        Reservation reg2 = new Reservation();
        Reservation vip1 = new Reservation();
        Reservation vip2 = new Reservation();
        Reservation vip3 = new Reservation();

        // Branch 1: vipCount == 0 when regular parties are already waiting
        queue.enqueue(reg1);
        queue.enqueue(reg2);
        queue.enqueueVIP(vip1);
        assertEquals("VipOne", queue.peek().getName());

        // Branch 2: 0 < vipCount < deque.size() (inserts behind VipOne, ahead of RegOne)
        queue.enqueueVIP(vip2);
        assertEquals(4, queue.size());
        assertEquals("VipOne", queue.dequeue().getName());
        assertEquals("VipTwo", queue.dequeue().getName());
        assertEquals("RegOne", queue.dequeue().getName());
        assertEquals("RegTwo", queue.dequeue().getName());

        // Branch 3: vipCount == deque.size() (only VIPs currently in queue)
        queue.enqueueVIP(vip1);
        queue.enqueueVIP(vip3);
        assertEquals(2, queue.size());
        assertEquals("VipOne", queue.dequeue().getName());
        assertEquals("VipThree", queue.dequeue().getName());
    }

    public void testValidationRejectsNullDuplicatesAndFullQueue() {
        WaitQueue smallQueue = new WaitQueue(2);

        // Null reservation check
        Exception nullEnqueue = null;
        try {
            smallQueue.enqueue(null);
        } catch (IllegalArgumentException e) {
            nullEnqueue = e;
        }
        assertNotNull(nullEnqueue);

        Reservation first = new Reservation("Smith", 2, false, baseTime);
        smallQueue.enqueue(first);

        // Duplicate name check (case-insensitive)
        Reservation duplicate = new Reservation("SMITH", 4, true, baseTime.plusMinutes(2));
        Exception dupThrown = null;
        try {
            smallQueue.enqueueVIP(duplicate);
        } catch (IllegalArgumentException e) {
            dupThrown = e;
        }
        assertNotNull(dupThrown);

        // Fill to capacity
        smallQueue.enqueue(new Reservation("Jones", 3, false, baseTime.plusMinutes(3)));
        assertEquals(2, smallQueue.size());

        // Full queue check
        Reservation overflow = new Reservation("Taylor", 2, false, baseTime.plusMinutes(4));
        Exception fullThrown = null;
        try {
            smallQueue.enqueue(overflow);
        } catch (IllegalStateException e) {
            fullThrown = e;
        }
        assertNotNull(fullThrown);
    }

    public void testDequeueEmptyThrows() {
        Exception thrown = null;
        try {
            queue.dequeue();
        } catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testRemoveReservationSuccessAndVipCountUpdate() {
        Reservation vip1 = new Reservation("VipAlpha", 2, true, baseTime);
        Reservation reg1 = new Reservation("RegBeta", 4, false, baseTime.plusMinutes(1));
        Reservation vip2 = new Reservation("VipGamma", 2, true, baseTime.plusMinutes(2));

        queue.enqueueVIP(vip1);
        queue.enqueue(reg1);
        queue.enqueueVIP(vip2);

        // Remove VipAlpha (case-insensitive) and verify vipCount decrements accurately
        assertTrue(queue.removeReservation("vipalpha"));
        assertEquals(2, queue.size());
        assertFalse(queue.containsName("VipAlpha"));

        // Adding a new VIP should place them right behind VipGamma and ahead of RegBeta
        Reservation vip3 = new Reservation("VipDelta", 3, true, baseTime.plusMinutes(3));
        queue.enqueueVIP(vip3);

        assertEquals("VipGamma", queue.dequeue().getName());
        assertEquals("VipDelta", queue.dequeue().getName());
        assertEquals("RegBeta", queue.dequeue().getName());
    }

    public void testRemoveReservationExceptionsAndContainsNameEdgeCases() {
        assertFalse(queue.containsName(null));
        assertFalse(queue.containsName("Nobody"));

        // Removing from empty queue
        Exception emptyRemove = null;
        try {
            queue.removeReservation("Alice");
        } catch (NoSuchElementException e) {
            emptyRemove = e;
        }
        assertNotNull(emptyRemove);

        queue.enqueue(new Reservation());

        // Null or blank name
        Exception nullName = null;
        try {
            queue.removeReservation(null);
        } catch (IllegalArgumentException e) {
            nullName = e;
        }
        assertNotNull(nullName);

        Exception blankName = null;
        try {
            queue.removeReservation("   ");
        } catch (IllegalArgumentException e) {
            blankName = e;
        }
        assertNotNull(blankName);

        // Name not found in non-empty queue
        Exception missingName = null;
        try {
            queue.removeReservation("Charlie");
        } catch (NoSuchElementException e) {
            missingName = e;
        }
        assertNotNull(missingName);
    }
}
