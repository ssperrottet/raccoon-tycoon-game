import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Bank bank = new Bank(); // Example implementation of Bank class

        // Example: Configure game parameters
        int playerCount = 4;
        int maxRounds = 3;

        // Instantiate GameManager with dependencies
        GameManager gameManager = new GameManager(playerCount, maxRounds, scanner, bank);

        // Start the game
        gameManager.showMenu();

        // Close scanner at the end of the game
        scanner.close();
    }
}
