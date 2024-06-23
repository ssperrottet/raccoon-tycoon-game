import java.util.ArrayList;
import java.util.HashMap;
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

    // Initialization Methods
    private void initializePlayers() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            Player p = new Player("Player " + (i + 1));
            for (int j = 0; j < 3; j++) {
                p.getProductionCards().add(bank.drawProductionCard());
            }
            players[i] = p;
        }
    }

    private void initializeBank() {
        bank.initializeTickers();
        bank.initializeCards(PLAYER_COUNT);
    }

    // Main Game Loop
    private void gameLoop() {
        while (gameState != GameState.END) {
            updateDisplay();
            playTurn();
        }
        endGame();
    }

    // Game State Methods
    void startGame() {
        System.out.println("RACCOON TYCOON");
        while (true) {
            System.out.println("Type START to start the game: ");
            if (input.nextLine().trim().equalsIgnoreCase("START")) {
                gameState = GameState.ACTIVE;
                initializeBank();
                initializePlayers();
                System.out.println("Let the games begin!");
                gameLoop();
                break;
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

    // Player Turn Methods
    private void playTurn() {
        Player p = players[activePlayer];
        System.out.println("""
                Available Moves:
                PROD: Play a production card
                SELL: Sell a resource
                RAIL: Start an auction on a railroad
                TOWN: Purchase a town card
                """);
        String playerMove = input.nextLine().trim().toUpperCase();

        switch (playerMove) {
            case "END" -> endTurn();
            case "VP" -> {
                p.incrementVictoryPoints();
                endTurn();
            }
            case "PROD" -> {
                if (production(p)) {
                    endTurn();
                } else {
                    System.out.println("Production failed. Try again.");
                }
            }
            case "SELL" -> sellResource(p);
            default -> {
                System.out.println("Invalid Move");
                playTurn();
            }
        }
    }

    private boolean production(Player p) {
        TerminalUtils.clearConsole();
        ArrayList<ProductionCard> cards = p.getProductionCards();
        System.out.println("*Playing Production Card*\nAvailable Cards:");
        displayProductionCards(cards);

        System.out.print("Production Card Index: ");
        try {
            int choice = Integer.parseInt(input.nextLine());
            ProductionCard card = cards.get(choice - 1);
            playProductionCard(p, card);
            return true;
        } catch (Exception e) {
            System.out.println("\nInvalid choice. Aborting production.\n");
            return false;
        }
    }

    private void playProductionCard(Player p, ProductionCard card) {
        System.out.println("\nPlaying " + card + "\n");

        HashMap<Resources, Integer> playerResources = p.getResources();
        ArrayList<ProductionCard> playerProductions = p.getProductionCards();

        ArrayList<Resources> price = card.getPriceResources();
        ArrayList<Resources> production = card.getProductionResources();

        for (Resources resource : price) {
            bank.tickers.get(resource).changeLevel(1);
        }

        for (Resources resource : production) {
            playerResources.put(resource, playerResources.getOrDefault(resource, 0) + 1);
        }

        playerProductions.remove(card);
        playerProductions.add(bank.drawProductionCard());
    }

    private void sellResource(Player p) {
        Resources resource = selectResource();
        System.out.print("Quantity: ");
        try {
            int quantity = Integer.parseInt(input.nextLine());
            int currentQuantity = p.getResources().getOrDefault(resource, 0);
            if (quantity <= currentQuantity) {
                p.getResources().put(resource, currentQuantity - quantity);
                p.setMoney(p.getMoney() + quantity * bank.tickers.get(resource).getPrice());
                bank.tickers.get(resource).changeLevel(-quantity);
                endTurn();
            } else {
                System.out.println("Not enough resources to sell.");
                sellResource(p);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Try again.");
            sellResource(p);
        }
    }

    private Resources selectResource() {
        System.out.println("1) Wheat");
        System.out.println("2) Wood");
        System.out.println("3) Iron");
        System.out.println("4) Coal");
        System.out.println("5) Goods");
        System.out.println("6) Luxury");
        System.out.print("Select resource type (1-6): ");

        try {
            int choice = Integer.parseInt(input.nextLine());
            return Resources.values()[choice - 1];
        } catch (Exception e) {
            System.out.println("Invalid choice. Try again.");
            return selectResource();
        }
    }

    private void endTurn() {
        updateDisplay();
        awaitInput();
        activePlayer = (activePlayer + 1) % PLAYER_COUNT;
        if (activePlayer == 0)
            round++;
        if (round > MAX_ROUNDS) {
            gameState = GameState.END;
        }
    }

    private void awaitInput() {
        System.out.println("Turn over\nPRESS ENTER TO CONTINUE");
        input.nextLine();
    }

    // Display Methods

    private void updateDisplay() {
        TerminalUtils.clearConsole();
        displayBoard();
        displayInventory(players[activePlayer]);
    }
    private void displayBoard() {
        System.out.printf("""
                -----------------------------------|Board|----------------------------------------
                Prices: Wheat: $%d, Wood: $%d, Iron: $%d, Coal: $%d, Goods: $%d, Luxury: $%d
                Railroads: %s, %s (%d remaining)
                Towns: %s (%d remaining)
                Buildings: TODO (TODO remaining)
                ----------------------------------------------------------------------------------
                
                """,
                bank.tickers.get(Resources.WHEAT).getPrice(),
                bank.tickers.get(Resources.WOOD).getPrice(),
                bank.tickers.get(Resources.IRON).getPrice(),
                bank.tickers.get(Resources.COAL).getPrice(),
                bank.tickers.get(Resources.GOODS).getPrice(),
                bank.tickers.get(Resources.LUXURY).getPrice(),
                bank.railroadSlot1,
                bank.railroadSlot2,
                bank.railroadCards.size(),
                bank.townSlot,
                bank.townCards.size()
        );
    }

    private void displayInventory(Player p) {
        System.out.printf("(Round %d) Player %s\n", round, players[activePlayer].getName());
        System.out.println("\nMoney: $" + p.getMoney());

        System.out.print("Resources: ");
        displayResources(p.getResources());

        System.out.print("Railroads: ");
        displayRailroadCards(p.getRailroadCards());

        System.out.print("Towns: ");
        displayTownCards(p.getTownCards());

        System.out.println("\nProductions:");
        displayProductionCards(p.getProductionCards());

        System.out.println();
    }

    private void displayProductionCards(ArrayList<ProductionCard> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, cards.get(i));
        }
    }

    private void displayTownCards(ArrayList<TownCard> cards) {
        if (cards.isEmpty()) System.out.println("NONE");
        for (TownCard card : cards) {
            System.out.println(card);
        }
    }

    private void displayRailroadCards(ArrayList<RailroadCard> cards) {
        if (cards.isEmpty()) System.out.println("NONE");
        for (RailroadCard card : cards) {
            System.out.println(card);
        }
    }

    public void displayResources(HashMap<Resources, Integer> resources) {
        if (resources.values().stream().allMatch(count -> count == 0)) {
            System.out.println("NONE");
            return;
        }

        StringBuilder resourceDisplay = new StringBuilder();
        for (Resources resource : Resources.values()) {
            int count = resources.getOrDefault(resource, 0);
            if (count != 0) {
                if (!resourceDisplay.isEmpty()) {
                    resourceDisplay.append(", ");
                }
                resourceDisplay.append(count).append(" ").append(capitalize(resource.name().toLowerCase()));
            }
        }

        System.out.println(resourceDisplay.toString());
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    public int countResources(HashMap<Resources, Integer> resources) {
        int count = 0;
        for (Resources resource : resources.keySet()) {
            count += resources.get(resource);
        }
        return count;
    }
}
