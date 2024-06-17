import java.util.Scanner;

public class Main {
    static final int PLAYER_COUNT = 4;
    static final int MAX_ROUNDS = 3;
    static final Scanner input = new Scanner(System.in);
    static final Bank bank = new Bank();

    static int round = 1;
    static GameState gameState = GameState.MENU;
    static Player[] players = new Player[PLAYER_COUNT];
    static int activePlayer = 0;

    public static void main(String[] args) {
        gameLoop();
    }

    private static void initializePlayers() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
    }

    private static void initializeBank() {
        bank.initializeTickers();
        bank.initializeCards();
    }

    private static void gameLoop() {
        while (true) {
            switch (gameState) {
                case MENU -> showMenu();
                case ACTIVE -> playTurn();
                case END -> {
                    endGame();
                    return;
                }
            }
        }
    }

    private static void endGame() {
        System.out.println("The game is over.");
        Player winner = calculateWinner();
        System.out.printf("Player %s is the winner!\n", winner.getName());
    }

    private static Player calculateWinner() {
        Player highest = players[0];
        for (Player p : players) {
            if (p.getVictoryPoints() > highest.getVictoryPoints())
                highest = p;
        }
        return highest;
    }

    private static void playTurn() {
        Player p = players[activePlayer];
        System.out.printf("Round %d\nPlayer %s's Turn\n", round, p.getName());
        handlePlayerMove(input.nextLine().trim());
    }

    private static void handlePlayerMove(String playerMove) {
        Player p = players[activePlayer];
        switch (playerMove.toUpperCase()) {
            case "END" -> endTurn();
            case "VP" -> {
                p.incrementVictoryPoints();
                endTurn();
            }
            case "PROD" -> {
                bank.tickers.get("Wheat").changeLevel(1);
                System.out.println("Price: " + bank.tickers.get("Wheat").getPrice());
            }
            default -> {
                System.out.println("Invalid Move");
                playTurn();
            }
        }
    }

    private static void showMenu() {
        System.out.println("RACCOON TYCOON");
        while (true) {
            System.out.println("Type START to start the game: ");
            if (input.nextLine().trim().equalsIgnoreCase("START")) {
                startGame();
                gameState = GameState.ACTIVE;
                break;
            }
        }
    }

    private static void startGame() {
        initializePlayers();
        initializeBank();
        System.out.println("Let the games begin!");
    }

    private static void endTurn() {
        activePlayer = (activePlayer + 1) % PLAYER_COUNT;
        if (activePlayer == 0)
            round++;
        if (round > MAX_ROUNDS) {
            gameState = GameState.END;
        }
    }

    enum GameState {
        MENU, ACTIVE, END
    }
}