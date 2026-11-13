import java.time.LocalDateTime;

/** A party's reservation: name, size, VIP status, and time made. */
public class Reservation {
    public static final int MAX_NAME_LENGTH = 20;
    public static final int MIN_PARTY = 1;
    public static final int MAX_PARTY = 6;

    private final String name;
    private final int size;
    private final boolean vip;
    private final LocalDateTime reservationTime;

    public Reservation(String name, int size, boolean vip, LocalDateTime time) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (size < MIN_PARTY || size > MAX_PARTY) {
            throw new IllegalArgumentException("Invalid party size.");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null.");
        }
        this.name = name.trim();
        this.size = size;
        this.vip = vip;
        this.reservationTime = time;
    }

    /** Name must be 1-20 letters, digits, or spaces. */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_NAME_LENGTH) {
            return false;
        }
        return trimmed.matches("[A-Za-z0-9 ]+");
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isVip() {
        return vip;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public String toString() {
        if (vip) {
            return name + " (party of " + size + ", VIP)";
        }
        return name + " (party of " + size + ")";
    }
}
