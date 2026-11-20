/** Starts the program by connecting the controller to the view. */

/*
public class Main {
    public static void main(String[] args) {
        HostController controller = new HostController();
        ConsoleView view = new ConsoleView(controller);
        view.run();
    }
}
*/

public class Main {
    public static void main(String[] args) {
        HostController controller = new HostController();
        ConsoleView view = new ConsoleView();

        boolean running = true;
        while (running) {
            view.showMenu();
            String choice = view.readChoice();

            if (choice.equals("1")) {
                String name = view.promptPartyName();
                String size = view.promptPartySize();
                String memberId = view.promptMemberId();
                view.showMessage(controller.addParty(name, size, memberId));
            } else if (choice.equals("2")) {
                view.showMessage(controller.getStatus());
            } else if (choice.equals("3")) {
                String table = view.promptTableId();
                view.showMessage(controller.checkOut(table));
            } else if (choice.equals("4")) {
                String newId = view.promptNewMemberId();
                view.showMessage(controller.registerMember(newId));
            } else if (choice.equals("5")) {
                String cancelName = view.promptCancelName();
                view.showMessage(controller.cancelParty(cancelName));
            } else if (choice.equals("6")) {
                running = false;
                view.showMessage("Goodbye!");
            } else {
                view.showMessage("Please enter a valid option.");
            }

        }

        }
    }

