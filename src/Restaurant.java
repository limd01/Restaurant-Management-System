import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Restaurant {
    private final List<Table> tables;
    private final WaitQueue waitlist;
    private final List<MenuItem> menu;

    public Restaurant() {
        this.tables = new ArrayList<>();
        this.waitlist = new WaitQueue();
        this.menu = new ArrayList<>();
        initializeDefaultTables();
    }

    /**
     * Sets up the default layout: four 2-tops, four 4-tops, and two 6-tops.
     */
    private void initializeDefaultTables() {
        int id = 1;
        for (int i = 0; i < 4; i++) {
            tables.add(new Table(id++, 2));
        }
        for (int i = 0; i < 4; i++) {
            tables.add(new Table(id++, 4));
        }
        for (int i = 0; i < 2; i++) {
            tables.add(new Table(id++, 6));
        }
    }

    /**
     * Checks the list of tables to find the smallest empty one large enough for the party size.
     * Returns null if there isn't one available.
     */
    public Table findAvailableTable(int partySize) {
        if (partySize < Reservation.MIN_PARTY || partySize > Reservation.MAX_PARTY) {
            throw new IllegalArgumentException(
                "Party size must be between " + Reservation.MIN_PARTY + " and " + Reservation.MAX_PARTY + "."
            );
        }

        Table bestFit = null;
        for (Table table : tables) {
            if (table.isAvailable() && table.getCapacity() >= partySize) {
                if (bestFit == null || table.getCapacity() < bestFit.getCapacity()) {
                    bestFit = table;
                }
            }
        }
        return bestFit;
    }

    /**
     * Checks for a table and seats the group if one is available;
     * otherwise adds the party to the queue (VIPs ahead of non-VIPs).
     */
    public void addReservation(Reservation reserve, LocalDateTime now) {
        if (reserve == null) {
            throw new IllegalArgumentException("Reservation cannot be null.");
        }
        if (isNameActive(reserve.getName())) {
            throw new IllegalArgumentException(
                "A party with the name '" + reserve.getName() + "' is already seated or waiting."
            );
        }

        Table available = findAvailableTable(reserve.getSize());
        if (available != null) {
            available.reserve(reserve, now);
        } else if (reserve.isVip()) {
            waitlist.enqueueVIP(reserve);
        } else {
            waitlist.enqueue(reserve);
        }
    }

    /**
     * Checks if anyone is waiting, finds them a proper table, and removes the reservation from the queue.
     * Returns true if a party was seated, false otherwise.
     */
    public boolean addWaitingReservation(LocalDateTime now) {
        if (waitlist.isEmpty()) {
            return false;
        }

        Reservation next = waitlist.peek();
        Table available = findAvailableTable(next.getSize());
        if (available != null) {
            waitlist.dequeue();
            available.reserve(next, now);
            return true;
        }
        return false;
    }

    /**
     * Clears the table, seats the next party if anyone is waiting, and returns the bill.
     * Throws an exception if the table does not exist or is already empty.
     */
    public double checkOutTable(int tableId, LocalDateTime now) {
        Table target = getTableById(tableId);
        if (target == null) {
            throw new NoSuchElementException("Table " + tableId + " does not exist.");
        }
        if (target.isAvailable()) {
            throw new IllegalStateException("Table " + tableId + " is already empty.");
        }

        double bill = target.getBill();
        target.clear();
        addWaitingReservation(now);
        return bill;
    }

    /**
     * Counts reservations ahead of this party and multiplies by max stay time,
     * factoring in remaining time on occupied tables of the required size.
     * Throws NoSuchElementException if the reservation does not exist in the waitlist.
     */
    public int estimateWaitTime(Reservation reserve, LocalDateTime now) {
        if (reserve == null || !waitlist.containsName(reserve.getName())) {
            throw new NoSuchElementException("Reservation does not exist in the waitlist.");
        }

        int requiredCapacity = getRequiredTableTier(reserve.getSize());
        int partiesAhead = 0;
        boolean found = false;

        // Read queue order safely and restore it intact
        int count = waitlist.size();
        List<Reservation> snapshot = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            snapshot.add(waitlist.dequeue());
        }
        for (Reservation r : snapshot) {
            if (r.isVip()) {
                waitlist.enqueueVIP(r);
            } else {
                waitlist.enqueue(r);
            }
        }

        for (Reservation r : snapshot) {
            if (r.getName().equalsIgnoreCase(reserve.getName())) {
                found = true;
                break;
            }
            if (getRequiredTableTier(r.getSize()) >= requiredCapacity) {
                partiesAhead++;
            }
        }

        if (!found) {
            throw new NoSuchElementException("Reservation does not exist in the waitlist.");
        }

        // Factor in the soonest-to-free occupied table of the needed size
        int minRemainingMinutes = Table.MAX_STAY_MINUTES;
        int matchingTables = 0;
        for (Table t : tables) {
            if (t.getCapacity() >= requiredCapacity) {
                matchingTables++;
                if (!t.isAvailable() && t.getSeatedTime() != null) {
                    long elapsed = Duration.between(t.getSeatedTime(), now).toMinutes();
                    int remaining = (int) Math.max(1, Table.MAX_STAY_MINUTES - elapsed);
                    if (remaining < minRemainingMinutes) {
                        minRemainingMinutes = remaining;
                    }
                }
            }
        }

        if (matchingTables == 0) {
            return Table.MAX_STAY_MINUTES;
        }

        int turnoverCycles = partiesAhead / matchingTables;
        return minRemainingMinutes + (turnoverCycles * Table.MAX_STAY_MINUTES);
    }

    /**
     * Compares current time to seated time and returns a list of tables going over time.
     */
    public List<Table> getOvertimeTables(LocalDateTime now) {
        List<Table> overtime = new ArrayList<>();
        for (Table table : tables) {
            if (!table.isAvailable() && table.getSeatedTime() != null) {
                long minutesSeated = Duration.between(table.getSeatedTime(), now).toMinutes();
                if (minutesSeated > Table.MAX_STAY_MINUTES) {
                    overtime.add(table);
                }
            }
        }
        return overtime;
    }

    /**
     * Retrieves the menu items.
     */
    public ArrayList<MenuItem> getMenu() {
        return new ArrayList<>(menu);
    }

    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }

    public WaitQueue getWaitlist() {
        return waitlist;
    }

    /**
     * Enforces the "One place per name" invariant across tables and waitlist.
     */
    public boolean isNameActive(String name) {
        if (name == null) {
            return false;
        }
        String target = name.trim();
        for (Table table : tables) {
            if (!table.isAvailable() && table.getReservation().getName().equalsIgnoreCase(target)) {
                return true;
            }
        }
        return waitlist.containsName(target);
    }

    private Table getTableById(int tableId) {
        for (Table table : tables) {
            if (table.getTableId() == tableId) {
                return table;
            }
        }
        return null;
    }

    private int getRequiredTableTier(int partySize) {
        if (partySize <= 2) return 2;
        if (partySize <= 4) return 4;
        return 6;
    }
}
