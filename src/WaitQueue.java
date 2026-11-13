import java.util.NoSuchElementException;

public class WaitQueue {
    private final DLinkedDeque<Reservation> deque;
    private int vipCount;
    private final int maxSize;

    public static final int DEFAULT_MAX_SIZE = 15;

    public WaitQueue() {
        this(DEFAULT_MAX_SIZE);
    }

    public WaitQueue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be greater than 0.");
        }
        this.deque = new DLinkedDeque<>();
        this.vipCount = 0;
        this.maxSize = maxSize;
    }

    /**
     * Adds a normal party to the back of the queue.
     * Rejects null reservations, full queues, and duplicate names (case-insensitive).
     */
    public void enqueue(Reservation reserve) {
        validateNewReservation(reserve);
        deque.addLast(reserve);
    }

    /**
     * Adds a VIP party behind the last waiting VIP member (or at the front if no VIPs are waiting).
     */
    public void enqueueVIP(Reservation reserve) {
        validateNewReservation(reserve);

        if (vipCount == 0) {
            deque.addFirst(reserve);
        } else if (vipCount == deque.size()) {
            deque.addLast(reserve);
        } else {
            // Temporarily rotate existing VIPs out of the front to insert behind the last VIP
            DLinkedDeque<Reservation> tempVips = new DLinkedDeque<>();
            for (int i = 0; i < vipCount; i++) {
                tempVips.addLast(deque.removeFirst());
            }
            deque.addFirst(reserve);
            while (!tempVips.isEmpty()) {
                deque.addFirst(tempVips.removeLast());
            }
        }
        vipCount++;
    }

    /**
     * Pops and returns the reservation at the front of the deque.
     * Throws NoSuchElementException if the queue is empty.
     */
    public Reservation dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue from an empty waitlist.");
        }
        Reservation front = deque.removeFirst();
        if (front.isVip() && vipCount > 0) {
            vipCount--;
        }
        return front;
    }

    /**
     * Finds a reservation with a matching name (case-insensitive) and removes them from the line.
     * Throws NoSuchElementException if the queue is empty or the name is not found.
     */
    public boolean removeReservation(String name) {
        if (isEmpty()) {
            throw new NoSuchElementException("Waitlist is empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation name cannot be empty.");
        }

        String target = name.trim();
        boolean removed = false;
        int currentSize = deque.size();

        // Cycle through the deque once to preserve order while removing the target
        for (int i = 0; i < currentSize; i++) {
            Reservation current = deque.removeFirst();
            if (!removed && current.getName().equalsIgnoreCase(target)) {
                removed = true;
                if (current.isVip() && vipCount > 0) {
                    vipCount--;
                }
            } else {
                deque.addLast(current);
            }
        }

        if (!removed) {
            throw new NoSuchElementException("No reservation found under the name: " + target);
        }
        return true;
    }

    /**
     * Checks if anyone is waiting in the queue.
     */
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    /**
     * Shows who is next in the queue without removing them.
     * Returns null if the queue is empty.
     */
    public Reservation peek() {
        if (isEmpty()) {
            return null;
        }
        return deque.peekFirst();
    }

    /**
     * Returns the current number of parties in the queue.
     */
    public int size() {
        return deque.size();
    }

    /**
     * Helper method to enforce class invariants before adding a reservation.
     */
    private void validateNewReservation(Reservation reserve) {
        if (reserve == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        if (deque.size() >= maxSize) {
            throw new IllegalStateException("Waitlist is full (max capacity: " + maxSize + ").");
        }
        if (containsName(reserve.getName())) {
            throw new IllegalArgumentException("A party with the name '" + reserve.getName() + "' is already in the waitlist.");
        }
    }

    /**
     * Checks if a party name is already in the queue (case-insensitive).
     */
    public boolean containsName(String name) {
        if (name == null) {
            return false;
        }
        String target = name.trim();
        int currentSize = deque.size();
        boolean found = false;

        for (int i = 0; i < currentSize; i++) {
            Reservation current = deque.removeFirst();
            if (current.getName().equalsIgnoreCase(target)) {
                found = true;
            }
            deque.addLast(current);
        }
        return found;
    }
}
