/** Starts the program by connecting the controller to the view. */
public class Main {
    public static void main(String[] args) {
        HostController controller = new HostController();
        ConsoleView view = new ConsoleView(controller);
        view.run();
    }
}
