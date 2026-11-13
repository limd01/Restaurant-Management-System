import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** One table: its seats, the party sitting there, and their orders. */
public class Table {
    public static final int MAX_STAY_MINUTES = 120;

    private final int tableId;
    private final int capacity;
    private Reservation reservation;
    private LocalDateTime seatedTime;
    private ArrayList<MenuItem> orders;

    public Table(int tableId, int capacity) {
        if (tableId < 1) {
            throw new IllegalArgumentException("Bad table id.");
        }
        if (capacity < 1 || capacity > Reservation.MAX_PARTY) {
            throw new IllegalArgumentException("Bad capacity.");
        }
        this.tableId = tableId;
        this.capacity = capacity;
        this.reservation = null;
        this.seatedTime = null;
        this.orders = new ArrayList<MenuItem>();
    }

    public int getTableId() {
        return tableId;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        return reservation == null;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public LocalDateTime getSeatedTime() {
        return seatedTime;
    }

    public void reserve(Reservation party, LocalDateTime time) {
        if (party == null || time == null) {
            throw new IllegalArgumentException("Party and time are required.");
        }
        if (party.getSize() > capacity) {
            throw new IllegalArgumentException("Party is too big for this table.");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("Table is already occupied.");
        }
        reservation = party;
        seatedTime = time;
    }

    public void clear() {
        reservation = null;
        seatedTime = null;
        orders.clear();
    }

    public void addOrder(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        if (isAvailable()) {
            throw new IllegalStateException("Table is empty.");
        }
        orders.add(item);
    }

    public ArrayList<MenuItem> getOrders() {
        return new ArrayList<MenuItem>(orders);
    }

    /** Adds up the orders every time so it is never out of date. */
    public double getBill() {
        double total = 0;
        for (MenuItem item : orders) {
            total += item.getPrice();
        }
        return total;
    }

    public double splitBill() {
        if (isAvailable()) {
            return 0;
        }
        return getBill() / reservation.getSize();
    }

    /** True if the party has been seated for more than 120 minutes. */
    public boolean isOvertime(LocalDateTime now) {
        if (isAvailable()) {
            return false;
        }
        long minutes = Duration.between(seatedTime, now).toMinutes();
        return minutes > MAX_STAY_MINUTES;
    }

    public String toString() {
        if (isAvailable()) {
            return "Table " + tableId + " (seats " + capacity + "): Available";
        }
        return "Table " + tableId + " (seats " + capacity + "): "
            + reservation.getName();
    }
}
