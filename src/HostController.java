import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Sits between the view and the model. It turns the text the host typed
 * into numbers, calls Restaurant, and turns the results into messages.
 */
public class HostController {
    private final Restaurant restaurant;

    public HostController() {
        restaurant = new Restaurant();
    }

    /** Seats the party if a table is free, otherwise puts them on the waitlist. */
    public String addParty(String name, String sizeText) {
        int size;
        try {
            size = Integer.parseInt(sizeText.trim());
        } catch (NumberFormatException e) {
            return "Party size must be a number.";
        }

        try {
            Reservation party = new Reservation(name, size, false,
                LocalDateTime.now());
            Table table = restaurant.findAvailableTable(size);
            restaurant.addReservation(party, LocalDateTime.now());
            if (table != null) {
                return party.getName() + " seated at table "
                    + table.getTableId() + ".";
            }
            return party.getName() + " added to the waitlist.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (IllegalStateException e) {
            return "The waitlist is full. Party turned away.";
        }
    }

    /** Checks out a table and returns the bill as a message. */
    public String checkOut(String tableText) {
        int id;
        try {
            id = Integer.parseInt(tableText.trim());
        } catch (NumberFormatException e) {
            return "Table number must be a number.";
        }

        try {
            double bill = restaurant.checkOutTable(id, LocalDateTime.now());
            return "Table " + id + " checked out. Bill: $"
                + String.format("%.2f", bill);
        } catch (NoSuchElementException e) {
            return e.getMessage();
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    /** Returns a text summary of every table and the waitlist. */
    public String getStatus() {
        String text = "Tables:\n";
        for (Table table : restaurant.getTables()) {
            text += "  " + table + "\n";
        }

        for (Table table : restaurant.getOvertimeTables(LocalDateTime.now())) {
            text += "  ** Table " + table.getTableId() + " is over time **\n";
        }

        WaitQueue waitlist = restaurant.getWaitlist();
        text += "Waitlist: " + waitlist.size() + " parties waiting\n";
        if (!waitlist.isEmpty()) {
            text += "  Next up: " + waitlist.peek() + "\n";
        }
        return text;
    }
}
