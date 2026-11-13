import static org.junit.Assert.*;
import org.junit.Test;

public class MenuItemTest {
    @Test public void testConstructorAndGetters() {
        MenuItem item = new MenuItem("Pizza", 12.50, 15);
        assertEquals("Pizza", item.getName());
        assertEquals(12.50, item.getPrice(), 0.001);
        assertEquals(15, item.getWaitTime());
    }

    @Test public void testSetName() {
        MenuItem item = new MenuItem("Pizza", 12.50, 15);
        item.setName("Cheese Pizza");
        assertEquals("Cheese Pizza", item.getName());
    }

    @Test public void testRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("", 5.0, 5));
        assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza, Pasta", 5.0, 5));
        MenuItem item = new MenuItem("Pizza", 5.0, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setName("!!!"));
    }

    @Test public void testSetPrice() {
        MenuItem item = new MenuItem("Pizza", 12.50, 15);
        item.setPrice(14.00);
        assertEquals(14.00, item.getPrice(), 0.001);
    }

    @Test public void testRejectsInvalidPrice() {
        assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza", 0, 5));
        MenuItem item = new MenuItem("Pizza", 5, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setPrice(-2));
    }

    @Test public void testSetWaitTime() {
        MenuItem item = new MenuItem("Pizza", 12.50, 15);
        item.setWaitTime(10);
        assertEquals(10, item.getWaitTime());
    }

    @Test public void testRejectsInvalidWaitTime() {
        assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza", 5, 0));
        MenuItem item = new MenuItem("Pizza", 5, 5);
        assertThrows(IllegalArgumentException.class, () -> item.setWaitTime(-1));
    }

    @Test public void testToString() {
        MenuItem item = new MenuItem("Pizza", 12.50, 15);
        assertTrue(item.toString().contains("Pizza"));
        assertTrue(item.toString().contains("12.50"));
    }
}
