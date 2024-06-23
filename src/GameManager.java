import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {
    private final int PLAYER_COUNT;
    private final int MAX_ROUNDS;
    private final GameUI ui;
    private final Bank bank;

    private int round;
    private GameState gameState;
    private final Player[] players;
    private int activePlayer;

    public GameManager(int playerCount, int maxRounds, GameUI ui, Bank bank) {
        this.PLAYER_COUNT = playerCount;
        this.MAX_ROUNDS = maxRounds;
        this.ui = ui;
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
            playTurn();
        }
        endGame();
    }

    // Game State Methods
    void startGame() {
        ui.displayMessage("RACCOON TYCOON");
        while (true) {
            if (ui.getUserInput("Type START to start the game: ").equalsIgnoreCase("START")) {
                gameState = GameState.ACTIVE;
                initializeBank();
                initializePlayers();
                ui.displayMessage("Let the games begin!");
                gameLoop();
                break;
            }
        }
    }

    private void endGame() {
        ui.displayMessage("The game is over.");
        Player winner = calculateWinner();
        ui.displayMessage(String.format("Player %s is the winner!\n", winner.getName()));
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
        ui.updateDisplay(bank, p, round);
        // Display numbered menu options
        ui.displayMessage("""
            Available Moves:
            1. Play a production card
            2. Sell a resource
            3. Start an auction on a railroad
            4. Purchase a town card
            """);

        int choice;
        try {
            choice = Integer.parseInt(ui.getUserInput("Select your move (1-4): ").trim());
        }
        catch (Exception e) {
            choice = 0;
        }

        boolean turnConcluded = false;

        switch (choice) {
            case 1 -> {
                if (playProduction(p))
                    turnConcluded = true;
            }
            case 2 -> {
                if (sellResource(p))
                    turnConcluded = true;
            }
            case 3 -> {
                // Implement auction on railroad (if applicable)
                ui.awaitInput("Railroad functionality not implemented yet.");
                playTurn(); // Go back to playTurn() if not implemented
            }
            case 4 -> {
                // Implement purchasing town card (if applicable)
                ui.awaitInput("Town card functionality not implemented yet.");
                playTurn(); // Go back to playTurn() if not implemented
            }
            default -> {
                ui.awaitInput("Invalid choice. Please select a number from 1 to 4.");
                playTurn(); // Retry turn if choice is invalid
            }
        }
        if (turnConcluded)
            endTurn();
    }

    private boolean playProduction(Player p) {
        ArrayList<ProductionCard> cards = p.getProductionCards();
        ProductionCard card = ui.selectProductionCard(cards);
        if (card == null) {
            return false; // Player canceled or invalid choice
        }
        executeProduction(p, card);
        return true;
    }

    private void executeProduction(Player p, ProductionCard card) {
        ui.displayMessage("\nPlaying " + card + "\n");

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
        ui.awaitInput("PRESS ENTER TO CONTINUE");
    }

    private boolean sellResource(Player p) {
        // Check if the player has any resources to sell
        if (p.getResources().values().stream().allMatch(count -> count == 0)) {
            ui.awaitInput("No Resources To Sell. Aborting Sell");
            playTurn();
            return false;
        }

        // Display sell menu and select resource to sell
        HashMap<Resources, Integer> resources = p.getResources();
        ui.sellMenu(bank, resources);
        Resources resource = ui.selectResource(resources);
        if (resource == null) {
            return false; // Player canceled or has no resources to sell
        }

        // Get quantity of resources to sell
        int quantity = 1;
        int currentQuantity = resources.getOrDefault(resource, 0);
        try {
            if (currentQuantity > 1)
                quantity = Integer.parseInt(ui.getUserInput("Quantity: ").trim());
        } catch (NumberFormatException e) {
            ui.awaitInput("Invalid quantity. Please enter a number.");
            return sellResource(p); // Retry selling with valid input
        }

        // Validate quantity against player's inventory
        if (quantity > currentQuantity) {
            ui.awaitInput("Insufficient " + resource + " to sell.");
            return sellResource(p); // Retry selling with valid quantity
        }

        // Calculate total earnings and update player's resources and money
        int price = quantity * bank.tickers.get(resource).getPrice();
        p.getResources().put(resource, currentQuantity - quantity);
        p.setMoney(p.getMoney() + price);
        bank.tickers.get(resource).changeLevel(-quantity);

        // Inform user about the transaction
        ui.awaitInput("\nSelling " + quantity + " " + resource + " for $" + price + "\n");
        return true;
    }

    private void endTurn() {
        ui.updateDisplay(bank, players[activePlayer], round);
        ui.awaitInput("TURN OVER\nPRESS ENTER TO CONTINUE");
        activePlayer = (activePlayer + 1) % PLAYER_COUNT;
        if (activePlayer == 0)
            round++;
        if (round > MAX_ROUNDS) {
            gameState = GameState.END;
        }
    }
}
