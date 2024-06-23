public class TerminalUtils {
    public static void clearConsole() {
        // Print a large number of newlines to simulate clearing the console
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
        // Optionally reset cursor to the top if supported (not effective in IntelliJ)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}