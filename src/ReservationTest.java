import static org.junit.Assert.*;
import java.time.LocalDateTime;
import org.junit.Test;

public class ReservationTest {
    
    private static void expectThrows(Class<? extends Exception> type, Runnable code) {
        try {
            code.run();
        }
        catch (Exception e) {
            assertTrue(type.isInstance(e));
            return;
        }
        fail("Expected " + type.getSimpleName());
    }
    private final LocalDateTime time = LocalDateTime.of(2026, 11, 12, 12, 0);

    @Test public void testValidReservationAndGetters() {
        Reservation party = new Reservation(" Smith  ", 4, true, time);
        assertEquals("Smith", party.getName());
        assertEquals(4, party.getSize());
        assertTrue(party.isVip());
        assertEquals(time, party.getReservationTime());
    }

    @Test public void testRegularParty() {
        Reservation party = new Reservation("Jones", 2, false, time);
        assertFalse(party.isVip());
    }

    @Test public void testRejectsNullOrBlankName() {
        assertFalse(Reservation.isValidName(null));
        assertFalse(Reservation.isValidName("   "));
        expectThrows(IllegalArgumentException.class,
            () -> new Reservation("", 2, false, time));
    }

    @Test public void testRejectsLongOrSpecialCharacterName() {
        assertFalse(Reservation.isValidName("This name is definitely too long"));
        assertFalse(Reservation.isValidName("Smith, John"));
        expectThrows(IllegalArgumentException.class,
            () -> new Reservation("Smith, John", 2, false, time));
    }

    @Test public void testRejectsPartySizeOutsideRange() {
        expectThrows(IllegalArgumentException.class,
            () -> new Reservation("Smith", 0, false, time));
        expectThrows(IllegalArgumentException.class,
            () -> new Reservation("Smith", 7, false, time));
    }

    @Test public void testRejectsNullReservationTime() {
        expectThrows(IllegalArgumentException.class,
            () -> new Reservation("Smith", 2, false, null));
    }

    @Test public void testToStringContainsPartyInformation() {
        Reservation party = new Reservation("Lee", 1, true, time);
        assertTrue(party.toString().contains("Lee"));
        assertTrue(party.toString().contains("VIP"));
    }
}
