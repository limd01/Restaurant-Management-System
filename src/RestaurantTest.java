import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import student.TestCase;

public class RestaurantTest extends TestCase {

    private Restaurant restaurant;
    private LocalDateTime baseTime;

    public void setUp() {
        restaurant = new Restaurant();
        baseTime = LocalDateTime.of(2026, 11, 12, 18, 0);
    }

    /** Fills both 6-tops so any 6-person party must wait. */
    private void fillSixTops() {
        restaurant.addReservation(new Reservation("SixOne", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("SixTwo", 6, false, baseTime), baseTime);
    }

    private boolean registerThrows(String id) {
        try {
            restaurant.registerMember(id);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }

    private boolean isVipThrows(String id) {
        try {
            restaurant.isVipMember(id);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }

    public void testInitialLayoutAndBestFitTable() {
        assertEquals(10, restaurant.getTables().size());

        // Party of 2 should get a 2-top
        Table t2 = restaurant.findAvailableTable(2);
        assertNotNull(t2);
        assertEquals(2, t2.getCapacity());

        // Party of 3 should get a 4-top
        Table t4 = restaurant.findAvailableTable(3);
        assertNotNull(t4);
        assertEquals(4, t4.getCapacity());

        // Party of 6 should get a 6-top
        Table t6 = restaurant.findAvailableTable(6);
        assertNotNull(t6);
        assertEquals(6, t6.getCapacity());
    }

    public void testFindAvailableTableInvalidSizeThrows() {
        Exception thrownLow = null;
        try {
            restaurant.findAvailableTable(0);
        } catch (IllegalArgumentException e) {
            thrownLow = e;
        }
        assertNotNull(thrownLow);

        Exception thrownHigh = null;
        try {
            restaurant.findAvailableTable(7);
        } catch (IllegalArgumentException e) {
            thrownHigh = e;
        }
        assertNotNull(thrownHigh);
    }

    public void testAddReservationSeatsImmediatelyWhenTableOpen() {
        Reservation party = new Reservation("Smith", 2, false, baseTime);
        restaurant.addReservation(party, baseTime);

        assertTrue(restaurant.getWaitlist().isEmpty());
        assertTrue(restaurant.isNameActive("smith"));
    }

    public void testAddReservationQueuesWhenFullAndPrioritizesVIP() {
        // Fill both 6-tops
        restaurant.addReservation(new Reservation("SixOne", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("SixTwo", 6, false, baseTime), baseTime);

        // Next two 6-person parties must go to the waitlist
        Reservation regularSix = new Reservation("RegularSix", 6, false, baseTime.plusMinutes(5));
        Reservation vipSix = new Reservation("VipSix", 6, true, baseTime.plusMinutes(10));

        restaurant.addReservation(regularSix, baseTime.plusMinutes(5));
        restaurant.addReservation(vipSix, baseTime.plusMinutes(10));

        assertEquals(2, restaurant.getWaitlist().size());
        assertEquals("VipSix", restaurant.getWaitlist().peek().getName());
    }

    public void testDuplicateNameInvariantThrows() {
        Reservation first = new Reservation("Johnson", 2, false, baseTime);
        restaurant.addReservation(first, baseTime);

        Reservation duplicate = new Reservation("JOHNSON", 4, true, baseTime.plusMinutes(5));
        Exception thrown = null;
        try {
            restaurant.addReservation(duplicate, baseTime.plusMinutes(5));
        } catch (IllegalArgumentException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testCheckOutTableCalculatesBillAndSeatsNextWaiting() {
        // Fill both 6-tops
        restaurant.addReservation(new Reservation("Alpha", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Beta", 6, false, baseTime), baseTime);

        // Queue a third 6-person party
        Reservation waiting = new Reservation("Gamma", 6, false, baseTime.plusMinutes(10));
        restaurant.addReservation(waiting, baseTime.plusMinutes(10));
        assertEquals(1, restaurant.getWaitlist().size());

        // Add orders to Table 9 (first 6-top)
        Table table9 = restaurant.getTables().get(8);
        assertEquals(9, table9.getTableId());
        table9.addOrder(new MenuItem("Steak", 25.50, 20));
        table9.addOrder(new MenuItem("Soda", 3.50, 2));

        // Check out Table 9
        double bill = restaurant.checkOutTable(9, baseTime.plusMinutes(45));
        assertEquals(29.00, bill, 0.001);

        // Waiting party Gamma should now be seated at Table 9
        assertTrue(restaurant.getWaitlist().isEmpty());
        assertFalse(table9.isAvailable());
        assertEquals("Gamma", table9.getReservation().getName());
        assertFalse(restaurant.isNameActive("Alpha"));
    }

    public void testCheckOutInvalidOrEmptyTableThrows() {
        Exception missingTable = null;
        try {
            restaurant.checkOutTable(99, baseTime);
        } catch (NoSuchElementException e) {
            missingTable = e;
        }
        assertNotNull(missingTable);

        Exception emptyTable = null;
        try {
            restaurant.checkOutTable(1, baseTime);
        } catch (IllegalStateException e) {
            emptyTable = e;
        }
        assertNotNull(emptyTable);
    }

    public void testEstimateWaitTime() {
        // Seat two 6-tops at 18:00 and 18:30
        restaurant.addReservation(new Reservation("Seated1", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Seated2", 6, false, baseTime.plusMinutes(30)), baseTime.plusMinutes(30));

        // Queue two 6-person parties
        Reservation wait1 = new Reservation("Wait1", 6, false, baseTime.plusMinutes(40));
        Reservation wait2 = new Reservation("Wait2", 6, false, baseTime.plusMinutes(45));
        restaurant.addReservation(wait1, baseTime.plusMinutes(40));
        restaurant.addReservation(wait2, baseTime.plusMinutes(45));

        // At 19:00 (60 mins after Seated1), Seated1 has 60 mins left of its 120-min max stay
        LocalDateTime checkTime = baseTime.plusMinutes(60);
        int estWait1 = restaurant.estimateWaitTime(wait1, checkTime);
        assertEquals(60, estWait1);

        // Verify queue order was preserved after estimation
        assertEquals("Wait1", restaurant.getWaitlist().peek().getName());
        assertEquals(2, restaurant.getWaitlist().size());
    }

    public void testGetOvertimeTables() {
        restaurant.addReservation(new Reservation("EarlyBirds", 2, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Latecomers", 2, false, baseTime.plusMinutes(30)), baseTime.plusMinutes(30));

        // At 20:05 (125 mins after EarlyBirds, 95 mins after Latecomers)
        List<Table> overtime = restaurant.getOvertimeTables(baseTime.plusMinutes(125));
        assertEquals(1, overtime.size());
        assertEquals("EarlyBirds", overtime.get(0).getReservation().getName());
    }

    // ---- NEW: VIP member tests ----

    public void testRegisterMemberAndIsVipMember() {
        assertFalse(restaurant.isVipMember("1234"));
        restaurant.registerMember("1234");
        assertTrue(restaurant.isVipMember("1234"));

        // Extra spaces are trimmed
        assertTrue(restaurant.isVipMember(" 1234 "));
        restaurant.registerMember(" 5678 ");
        assertTrue(restaurant.isVipMember("5678"));

        // A well-formed ID that was never registered is not a VIP
        assertFalse(restaurant.isVipMember("9999"));

        // Registering the same ID twice is harmless
        restaurant.registerMember("1234");
        assertTrue(restaurant.isVipMember("1234"));
    }

    public void testMemberIdBadFormatThrows() {
        assertTrue(registerThrows(null));
        assertTrue(registerThrows(""));
        assertTrue(registerThrows("123"));
        assertTrue(registerThrows("12345"));
        assertTrue(registerThrows("abcd"));
        assertTrue(registerThrows("12 4"));
        assertTrue(registerThrows("12-4"));

        assertTrue(isVipThrows(null));
        assertTrue(isVipThrows(""));
        assertTrue(isVipThrows("123"));
        assertTrue(isVipThrows("12345"));
        assertTrue(isVipThrows("abcd"));
    }

    public void testRegisteredMemberGetsVipPriorityInLine() {
        fillSixTops();
        restaurant.registerMember("1234");

        boolean regularIsVip = restaurant.isVipMember("0000");
        boolean memberIsVip = restaurant.isVipMember("1234");
        assertFalse(regularIsVip);
        assertTrue(memberIsVip);

        restaurant.addReservation(new Reservation("Regular", 6, regularIsVip, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Member", 6, memberIsVip, baseTime), baseTime);

        assertEquals(2, restaurant.getWaitlist().size());
        assertEquals("Member", restaurant.getWaitlist().peek().getName());
        assertTrue(restaurant.getWaitlist().peek().isVip());
    }

    // ---- NEW: cancelReservation tests ----

    public void testCancelReservationRemovesWaitingParty() {
        fillSixTops();
        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);
        assertEquals(1, restaurant.getWaitlist().size());

        restaurant.cancelReservation("wait1");  // case-insensitive
        assertTrue(restaurant.getWaitlist().isEmpty());
        assertFalse(restaurant.isNameActive("Wait1"));
    }

    public void testCancelReservationKeepsOrderOfOthers() {
        fillSixTops();
        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Wait2", 6, false, baseTime), baseTime);
        restaurant.addReservation(new Reservation("Wait3", 6, false, baseTime), baseTime);

        restaurant.cancelReservation("Wait2");
        assertEquals(2, restaurant.getWaitlist().size());
        assertEquals("Wait1", restaurant.getWaitlist().dequeue().getName());
        assertEquals("Wait3", restaurant.getWaitlist().dequeue().getName());
    }

    public void testCancelReservationFreesNameForReuse() {
        fillSixTops();
        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);
        restaurant.cancelReservation("Wait1");

        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);
        assertEquals(1, restaurant.getWaitlist().size());
        assertTrue(restaurant.isNameActive("Wait1"));
    }

    public void testCancelReservationNameNotWaitingThrows() {
        fillSixTops();
        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);

        Exception missing = null;
        try {
            restaurant.cancelReservation("Ghost");
        } catch (NoSuchElementException e) {
            missing = e;
        }
        assertNotNull(missing);
        assertEquals(1, restaurant.getWaitlist().size());

        // A seated party is not on the waitlist, so it cannot be cancelled here
        Exception seated = null;
        try {
            restaurant.cancelReservation("SixOne");
        } catch (NoSuchElementException e) {
            seated = e;
        }
        assertNotNull(seated);
        assertTrue(restaurant.isNameActive("SixOne"));
    }

    public void testCancelReservationEmptyWaitlistThrows() {
        Exception thrown = null;
        try {
            restaurant.cancelReservation("Anyone");
        } catch (NoSuchElementException e) {
            thrown = e;
        }
        assertNotNull(thrown);
    }

    public void testCancelReservationBlankOrNullNameThrows() {
        fillSixTops();
        restaurant.addReservation(new Reservation("Wait1", 6, false, baseTime), baseTime);

        Exception blank = null;
        try {
            restaurant.cancelReservation("   ");
        } catch (IllegalArgumentException e) {
            blank = e;
        }
        assertNotNull(blank);

        Exception nullName = null;
        try {
            restaurant.cancelReservation(null);
        } catch (IllegalArgumentException e) {
            nullName = e;
        }
        assertNotNull(nullName);
        assertEquals(1, restaurant.getWaitlist().size());
    }
}
