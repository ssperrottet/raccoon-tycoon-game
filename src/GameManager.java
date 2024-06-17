import java.util.Scanner;

public class GameManager {
    private final int PLAYER_COUNT;
    private final int MAX_ROUNDS;
    private final Scanner input;
    private final Bank bank;

    private int round;
    private GameState gameState;
    private final Player[] players;
    private int activePlayer;

    public GameManager(int playerCount, int maxRounds, Scanner input, Bank bank) {
        this.PLAYER_COUNT = playerCount;
        this.MAX_ROUNDS = maxRounds;
        this.input = input;
        this.bank = bank;
        this.round = 1;
        this.gameState = GameState.MENU;
        this.players = new Player[PLAYER_COUNT];
        this.activePlayer = 0;
    }

    private void initializePlayers() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
    }

    private void initializeBank() {
        bank.initializeTickers();
        bank.initializeCards(PLAYER_COUNT);
    }

    private void gameLoop() {
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

    private void endGame() {
        System.out.println("The game is over.");
        Player winner = calculateWinner();
        System.out.printf("Player %s is the winner!\n", winner.getName());
    }

    private Player calculateWinner() {
        Player highest = players[0];
        for (Player p : players) {
            if (p.getVictoryPoints() > highest.getVictoryPoints())
                highest = p;
        }
        return highest;
    }

    private void playTurn() {
        Player p = players[activePlayer];
        System.out.printf("Round %d\nPlayer %s's Turn\n", round, p.getName());
        handlePlayerMove(input.nextLine().trim());
    }

    private void handlePlayerMove(String playerMove) {
        System.out.println("""
                Available Moves:
                PROD: Play a production card
                SELL: Sell a resource
                RAIL: Start an auction on a railroad
                TOWN: Purchase a town card""");
        Player p = players[activePlayer];
        switch (playerMove.toUpperCase()) {
            case "END" -> endTurn();
            case "VP" -> {
                p.incrementVictoryPoints();
                endTurn();
            }
            case "PROD" -> {
                bank.tickers.get(Resources.WHEAT).changeLevel(1);
                System.out.println("Price: " + bank.tickers.get(Resources.WHEAT).getPrice());
            }
            case "SELL" -> {
                Resources resource = selectResource();
                System.out.print("Quantity: ");
                int quantity = input.nextInt();

                int currentQuantity = p.getResources().get(resource);
                p.getResources().put(resource, currentQuantity - quantity);
                p.setMoney(p.getMoney() + quantity * bank.tickers.get(resource).getPrice());
                endTurn();
            }
            default -> {
                System.out.println("Invalid Move");
                playTurn();
            }
        }
    }

    private Resources selectResource() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1) Wheat");
        System.out.println("2) Wood");
        System.out.println("3) Iron");
        System.out.println("4) Coal");
        System.out.println("5) Goods");
        System.out.println("6) Luxury");
        System.out.print("Select resource type (1-6): ");

        int choice = scanner.nextInt();

        return switch (choice) {
            case 1 -> Resources.WHEAT;
            case 2 -> Resources.WOOD;
            case 3 -> Resources.IRON;
            case 4 -> Resources.COAL;
            case 5 -> Resources.GOODS;
            case 6 -> Resources.LUXURY;
            default -> throw new IllegalArgumentException("Invalid resource type selected.");
        };
    }

    private void showMenu() {
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

    void startGame() {
        initializePlayers();
        initializeBank();
        System.out.println("Let the games begin!");
        gameLoop();
    }

    private void endTurn() {
        System.out.println("Turn over");
        activePlayer = (activePlayer + 1) % PLAYER_COUNT;
        if (activePlayer == 0)
            round++;
        if (round > MAX_ROUNDS) {
            gameState = GameState.END;
        }
    }
}