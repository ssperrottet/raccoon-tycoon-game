import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    private int PLAYER_COUNT;
    private final GameUI ui;
    private final Bank bank;

    private int round;
    private GameState gameState;
    private Player[] players;
    private int activePlayer;

    public GameManager(GameUI ui, Bank bank) {
        this.ui = ui;
        this.bank = bank;
        this.round = 1;
        this.gameState = GameState.MENU;
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
                PLAYER_COUNT = Integer.parseInt(ui.getUserInput("Player Count: "));
                players = new Player[PLAYER_COUNT];

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
                if (playProduction())
                    turnConcluded = true;
            }
            case 2 -> {
                if (sellResource())
                    turnConcluded = true;
            }
            case 3 -> {
                // Implement auction on railroad (if applicable)
                ui.awaitInput("Railroad functionality not implemented yet.");
                playTurn(); // Go back to playTurn() if not implemented
            }
            case 4 -> {
                // Implement purchasing town card (if applicable)
                if (purchaseTown())
                    turnConcluded = true;
            }
            default -> {
                ui.awaitInput("Invalid choice. Please select a number from 1 to 4.");
                playTurn(); // Retry turn if choice is invalid
            }
        }
        if (turnConcluded)
            endTurn();
    }

    private boolean purchaseTown() {
        Player p = players[activePlayer];
        TownCard card = bank.townSlot;
        boolean canPayMain = p.getResources().getOrDefault(card.getType(), 0) >= card.getPrice();
        boolean canPayAlt = countResources(p) >= card.getAltPrice();

        if (!canPayMain && !canPayAlt) {
            ui.awaitInput("Cannot afford town card.");
            playTurn();
            return false;
        }

        ui.clearConsole();
        ui.displayMessage("*Purchasing Town Card*\n\n" + card + "\n");
        ui.displayMessage("Resources: " + ui.formatResources(p.getResources()) + "\n");

        String priceInfo;
        if (canPayMain && !canPayAlt) {
            priceInfo = card.getPrice() + " " + card.getType();
        } else if (!canPayMain) {
            priceInfo = card.getAltPrice() + " of any resource";
        } else {
            String choice = ui.getUserInput("Do you want to pay the main price (" + card.getPrice() + " " + card.getType() + ") or the alternative price (" + card.getAltPrice() + " of any resource)? (1 for main, 2 for alternative): ").trim();
            if (choice.equals("1")) {
                priceInfo = card.getPrice() + " " + card.getType();
            } else if (choice.equals("2")) {
                priceInfo = card.getAltPrice() + " of any resource";
            } else {
                ui.awaitInput("Invalid choice. Purchase canceled.");
                playTurn();
                return false;
            }
        }

        if (!ui.getUserInput("Confirm Purchase of Town Card for " + priceInfo + "? (y/n) ").trim().equalsIgnoreCase("y")) {
            playTurn();
            return false;
        }

        if (priceInfo.equals(card.getPrice() + " " + card.getType())) {
            p.getResources().put(card.getType(), p.getResources().get(card.getType()) - card.getPrice());
        } else {
            discardResources(p, card.getAltPrice());
        }

        p.getTownCards().add(card);
        bank.townSlot = bank.drawTownCard();
        return true;
    }


    private int countResources(Player p) {
        return p.getResources().values().stream().mapToInt(Integer::intValue).sum();
    }

    private void discardResources(Player p, int amount) {
        HashMap<Resources, Integer> resources = p.getResources();
        int remaining = amount;

        if (countResources(p) == amount) {
            p.getResources().clear();
            ui.awaitInput("All resources have been discarded.");
            return;
        }

        HashMap<Resources, Integer> discardedResources = new HashMap<>();

        while (remaining > 0) {
            ui.clearConsole();
            ui.displayMessage("You need to discard " + remaining + " resources.");
            ui.displayMessage("Resources: " + ui.formatResources(resources));

            Resources resource = ui.selectResource(resources);
            if (resource == null) {
                ui.displayMessage("Invalid selection. Please choose a resource to discard.");
                continue; //add ability to cancel transaction maybe
            }

            int currentAmount = resources.get(resource);
            int discardAmount = 1;

            try {
                if (currentAmount > 1 && remaining > 1)
                    discardAmount = Integer.parseInt(ui.getUserInput("Enter amount to discard (1 to " + Math.min(currentAmount, remaining) + "): ").trim());
            } catch (NumberFormatException e) {
                ui.awaitInput("Invalid input. Please enter a valid number.");
                continue;
            }
            if (discardAmount < 1 || discardAmount > Math.min(currentAmount, remaining)) {
                ui.awaitInput("Invalid amount. Please enter a number between 1 and " + Math.min(currentAmount, remaining) + ".");
                continue;
            }

            resources.put(resource, currentAmount - discardAmount);
            remaining -= discardAmount;

            // Aggregate discarded resources of the same type
            discardedResources.put(resource, discardedResources.getOrDefault(resource, 0) + discardAmount);
        }

        // Prepare formatted output of discarded resources
        List<String> output = new ArrayList<>();
        for (Map.Entry<Resources, Integer> entry : discardedResources.entrySet()) {
            output.add(entry.getValue() + " " + entry.getKey().name());
        }
        ui.clearConsole();
        ui.awaitInput("Discarded " + String.join(", ", output));
    }

    private boolean playProduction() {
        Player p = players[activePlayer];
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

    private boolean sellResource() {
        Player p = players[activePlayer];
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
            return sellResource(); // Retry selling with valid input
        }

        // Validate quantity against player's inventory
        if (quantity > currentQuantity) {
            ui.awaitInput("Insufficient " + resource + " to sell.");
            return sellResource(); // Retry selling with valid quantity
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
        if (activePlayer == 0) {
            round++;
            if (bank.townCards.isEmpty() || bank.railroadCards.isEmpty())
                gameState = GameState.END;
        }
    }
}
