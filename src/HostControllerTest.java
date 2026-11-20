import student.TestCase;

public class HostControllerTest extends TestCase {

    private HostController controller;

    public void setUp() {
        controller = new HostController();
    }

    /** Fills all 10 tables: four 2-tops, four 4-tops, two 6-tops. */
    private void fillAllTables() {
        for (int i = 1; i <= 10; i++) {
            int size = 2;
            if (i > 8) {
                size = 6;
            } else if (i > 4) {
                size = 4;
            }
            controller.addParty("T" + i, String.valueOf(size), "");
        }
    }

    // ---- adding parties ----

    public void testAddPartySeatsImmediately() {
        assertEquals("Smith seated at table 1.", controller.addParty("Smith", "2", ""));
        assertEquals("Jones seated at table 2.", controller.addParty("Jones", "2"));
    }

    public void testSizeNotANumber() {
        assertEquals("Party size must be a number.",
            controller.addParty("Smith", "abc", ""));
        assertEquals("Party size must be a number.",
            controller.addParty("Smith", "", ""));
        assertEquals("Party size must be a number.",
            controller.addParty("Smith", "abc"));
    }

    public void testSizeOutOfRange() {
        assertEquals("Invalid party size.", controller.addParty("Smith", "0", ""));
        assertEquals("Invalid party size.", controller.addParty("Smith", "7", ""));
        assertEquals("Invalid party size.", controller.addParty("Smith", "-2", ""));
    }

    public void testBadNames() {
        assertEquals("Invalid name.", controller.addParty("", "2", ""));
        assertEquals("Invalid name.", controller.addParty("Smith, John", "2", ""));
        assertEquals("Invalid name.",
            controller.addParty("This name is definitely too long", "2", ""));
    }

    public void testDuplicateName() {
        controller.addParty("Smith", "2", "");
        String message = controller.addParty("smith", "2", "");
        assertTrue(message.contains("already"));
    }

    public void testFullWaitlistTurnsPartyAway() {
        fillAllTables();
        for (int i = 1; i <= 15; i++) {
            assertEquals("W" + i + " added to the waitlist.",
                controller.addParty("W" + i, "6", ""));
        }
        assertEquals("The waitlist is full. Party turned away.",
            controller.addParty("W16", "6", ""));
    }

    // ---- VIP members ----

    public void testRegisterMemberNormal() {
        assertEquals("Member 1234 registered as VIP.", controller.registerMember("1234"));
        assertEquals("Member 5678 registered as VIP.", controller.registerMember(" 5678 "));
        // Registering twice does not crash
        assertEquals("Member 1234 registered as VIP.", controller.registerMember("1234"));
    }

    public void testRegisterMemberBadInput() {
        String expected = "Member ID must be exactly 4 digits.";
        assertEquals(expected, controller.registerMember("abc"));
        assertEquals(expected, controller.registerMember("123"));
        assertEquals(expected, controller.registerMember("12345"));
        assertEquals(expected, controller.registerMember(""));
        assertEquals(expected, controller.registerMember(null));
    }

    public void testRegisteredMemberIsSeatedAsVip() {
        controller.registerMember("1234");
        assertEquals("Vic (VIP) seated at table 1.",
            controller.addParty("Vic", "2", "1234"));
    }

    public void testUnregisteredMemberIdIsTreatedAsRegularParty() {
        // A well-formed but unknown ID cannot claim VIP status
        assertEquals("Smith seated at table 1.",
            controller.addParty("Smith", "2", "9999"));
    }

    public void testBlankOrNullMemberIdIsAllowed() {
        assertEquals("Smith seated at table 1.", controller.addParty("Smith", "2", ""));
        assertEquals("Jones seated at table 2.", controller.addParty("Jones", "2", "   "));
        assertEquals("Lee seated at table 3.", controller.addParty("Lee", "2", null));
    }

    public void testBadMemberIdRejectsPartyWithoutSeating() {
        String expected = "Member ID must be exactly 4 digits.";
        assertEquals(expected, controller.addParty("Smith", "2", "12"));
        assertEquals(expected, controller.addParty("Smith", "2", "abcd"));
        assertEquals(expected, controller.addParty("Smith", "2", "12345"));
        assertFalse(controller.getStatus().contains("Smith"));
    }

    public void testVipMemberJumpsAheadOfRegularPartyInLine() {
        fillAllTables();
        assertEquals("Reg added to the waitlist.",
            controller.addParty("Reg", "6", ""));
        controller.registerMember("1234");
        assertEquals("Vic (VIP) added to the waitlist.",
            controller.addParty("Vic", "6", "1234"));

        String status = controller.getStatus();
        assertTrue(status.contains("Waitlist: 2 parties waiting"));
        assertTrue(status.contains("Next up: Vic (party of 6, VIP)"));
    }

    // ---- cancelling ----

    public void testCancelPartyNormal() {
        fillAllTables();
        controller.addParty("W1", "6", "");
        controller.addParty("W2", "6", "");

        assertEquals("Removed W1 from the waitlist.", controller.cancelParty("W1"));
        String status = controller.getStatus();
        assertTrue(status.contains("Waitlist: 1 parties waiting"));
        assertTrue(status.contains("Next up: W2"));

        // Names are trimmed
        assertEquals("Removed W2 from the waitlist.", controller.cancelParty(" W2 "));
        assertTrue(controller.getStatus().contains("Waitlist: 0 parties waiting"));
    }

    public void testCancelPartyBadInput() {
        // Empty waitlist
        assertEquals("Waitlist is empty.", controller.cancelParty("Anyone"));

        fillAllTables();
        controller.addParty("W1", "6", "");

        // Name not waiting
        assertEquals("No reservation found under the name: Ghost",
            controller.cancelParty("Ghost"));
        // A seated party is not on the waitlist
        assertTrue(controller.cancelParty("T1").contains("T1"));
        // Blank name
        assertEquals("Reservation name cannot be empty.", controller.cancelParty("   "));

        // The waiting party was never removed
        assertTrue(controller.getStatus().contains("Next up: W1"));
    }

    // ---- checkout and status ----

    public void testCheckOutNormalAndBadInput() {
        controller.addParty("Smith", "2", "");
        assertEquals("Table 1 checked out. Bill: $0.00", controller.checkOut("1"));
        assertEquals("Table 1 is already empty.", controller.checkOut("1"));
        assertEquals("Table 99 does not exist.", controller.checkOut("99"));
        assertEquals("Table number must be a number.", controller.checkOut("x"));
        assertEquals("Table number must be a number.", controller.checkOut(""));
    }

    public void testCheckOutSeatsNextWaitingParty() {
        fillAllTables();
        controller.addParty("W1", "6", "");
        assertTrue(controller.getStatus().contains("Waitlist: 1 parties waiting"));

        assertEquals("Table 9 checked out. Bill: $0.00", controller.checkOut("9"));
        String status = controller.getStatus();
        assertTrue(status.contains("Waitlist: 0 parties waiting"));
        assertTrue(status.contains("Table 9 (seats 6): W1"));
    }

    public void testStatusShowsTablesAndWaitlist() {
        controller.addParty("Smith", "2", "");
        String status = controller.getStatus();
        assertTrue(status.contains("Smith"));
        assertTrue(status.contains("Table 2 (seats 2): Available"));
        assertTrue(status.contains("Waitlist: 0 parties waiting"));
    }
}
