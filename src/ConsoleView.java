/**
import java.util.Scanner;

/**
 * The console screen. It only talks to HostController and only handles
 * text, so it never needs to know what a Table or Reservation is.
 
public class ConsoleView {
    private final HostController controller;
    private final Scanner in;

    public ConsoleView(HostController controller) {
        this.controller = controller;
        this.in = new Scanner(System.in);
    }

    /** Shows the menu and handles commands until the host quits. 
    public void run() {
        boolean running = true;
        while (running) {
            showMenu();
            String choice = in.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Party name: ");
                String name = in.nextLine();
                System.out.print("Party size: ");
                String size = in.nextLine();
                System.out.println(controller.addParty(name, size));
            } else if (choice.equals("2")) {
                System.out.println(controller.getStatus());
            } else if (choice.equals("3")) {
                System.out.print("Table number: ");
                String table = in.nextLine();
                System.out.println(controller.checkOut(table));
            } else if (choice.equals("4")) {
                running = false;
                System.out.println("Goodbye!");
            } else {
                System.out.println("Please enter 1, 2, 3, or 4.");
            }

            if (running) {
                System.out.println();
            }
        }
    }

    private void showMenu() {
        System.out.println("=== Line and Dine ===");
        System.out.println("1. Add party");
        System.out.println("2. Show status");
        System.out.println("3. Check out table");
        System.out.println("4. Quit");
        System.out.print("Choose: ");
    }
}

*/
import java.util.Scanner;

/**
 * The console screen. Handles only text prompts and output,
 * with no inner classes and no references to Model or Controller classes.
 */
public class ConsoleView {
    private final Scanner in;

    public ConsoleView() {
        this.in = new Scanner(System.in);
    }

    /** Prints the main menu options. */
    public void showMenu() {
        System.out.println("=== Line and Dine ===");
        System.out.println("1. Add party");
        System.out.println("2. Show status");
        System.out.println("3. Check out table");
        System.out.println("4. Register VIP member");
        System.out.println("5. Cancel waiting party");
        System.out.println("6. Quit");
        System.out.print("Choose: ");
    }



    /** Reads the user's menu selection, ignoring accidental empty lines. */
    public String readChoice() {
        String line = in.nextLine().trim();
        while (line.isEmpty()) {
            line = in.nextLine().trim();
        }
        return line;
    }

    /** Prompts for and returns the party name. */
    public String promptPartyName() {
        System.out.print("Party name: ");
        return in.nextLine();
    }

    /** Prompts for and returns the party size. */
    public String promptPartySize() {
        System.out.print("Party size: ");
        return in.nextLine();
    }
    public String promptMemberId() {
        System.out.print("Member ID (4 digits, or press Enter to skip): ");
        return in.nextLine();
    }

    public String promptNewMemberId() {
        System.out.print("New 4-digit Member ID: ");
        return in.nextLine();
    }

    /** Prompts for and returns the table number. */
    public String promptTableId() {
        System.out.print("Table number: ");
        return in.nextLine();
    }

    /** Prints any message or status output to the console. */
    public void showMessage(String message) {
        System.out.println(message);
        System.out.println();
    }
    
    public String promptCancelName() {
        System.out.print("Party name to cancel: ");
        return in.nextLine();
    }

    
}
