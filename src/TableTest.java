import static org.junit.Assert.*;
import java.time.LocalDateTime;
import org.junit.Test;

public class TableTest {
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 12, 12, 0);
    private Reservation party(int size) {
        return new Reservation("Taylor", size, false, now);
    }

    @Test public void testConstructorAndGetters() {
        Table table = new Table(3, 4);
        assertEquals(3, table.getTableId());
        assertEquals(4, table.getCapacity());
        assertTrue(table.isAvailable());
        assertNull(table.getReservation());
        assertNull(table.getSeatedTime());
    }

    @Test public void testRejectsInvalidTableData() {
        assertThrows(IllegalArgumentException.class, () -> new Table(0, 4));
        assertThrows(IllegalArgumentException.class, () -> new Table(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Table(1, 7));
    }

    @Test public void testReserveAndClear() {
        Table table = new Table(1, 4);
        table.reserve(party(2), now);
        assertFalse(table.isAvailable());
        assertEquals("Taylor", table.getReservation().getName());
        assertEquals(now, table.getSeatedTime());
        table.clear();
        assertTrue(table.isAvailable());
        assertNull(table.getReservation());
        assertNull(table.getSeatedTime());
        assertTrue(table.getOrders().isEmpty());
    }

    @Test public void testRejectsInvalidReservation() {
        Table table = new Table(1, 2);
        assertThrows(IllegalArgumentException.class, () -> table.reserve(null, now));
        assertThrows(IllegalArgumentException.class, () -> table.reserve(party(2), null));
        assertThrows(IllegalArgumentException.class, () -> table.reserve(party(4), now));
    }

    @Test public void testRejectsReservingOccupiedTable() {
        Table table = new Table(1, 4);
        table.reserve(party(2), now);
        assertThrows(IllegalStateException.class, () -> table.reserve(party(2), now));
    }

    @Test public void testAddOrderAndGetOrders() {
        Table table = new Table(1, 4);
        table.reserve(party(2), now);
        MenuItem pizza = new MenuItem("Pizza", 12.0, 15);
        table.addOrder(pizza);
        assertEquals(1, table.getOrders().size());
        assertEquals("Pizza", table.getOrders().get(0).getName());
    }

    @Test public void testRejectsInvalidOrder() {
        Table table = new Table(1, 4);
        table.reserve(party(2), now);
        assertThrows(IllegalArgumentException.class, () -> table.addOrder(null));
        Table empty = new Table(2, 2);
        assertThrows(IllegalStateException.class,
            () -> empty.addOrder(new MenuItem("Tea", 2, 2)));
    }

    @Test public void testBillAndSplitBill() {
        Table table = new Table(1, 4);
        table.reserve(party(2), now);
        table.addOrder(new MenuItem("Pizza", 12.0, 15));
        table.addOrder(new MenuItem("Tea", 4.0, 2));
        assertEquals(16.0, table.getBill(), 0.001);
        assertEquals(8.0, table.splitBill(), 0.001);
    }

    @Test public void testEmptyBillAndSplitBill() {
        Table table = new Table(1, 2);
        assertEquals(0.0, table.getBill(), 0.001);
        assertEquals(0.0, table.splitBill(), 0.001);
    }

    @Test public void testOvertime() {
        Table table = new Table(1, 2);
        assertFalse(table.isOvertime(now.plusMinutes(121)));
        table.reserve(party(2), now);
        assertFalse(table.isOvertime(now.plusMinutes(120)));
        assertTrue(table.isOvertime(now.plusMinutes(121)));
        assertFalse(table.isOvertime(now));
    }

    @Test public void testToStringForFreeAndOccupiedTable() {
        Table table = new Table(1, 2);
        assertTrue(table.toString().contains("Available"));
        table.reserve(party(2), now);
        assertTrue(table.toString().contains("Taylor"));
    }
}
