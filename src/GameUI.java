import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GameUI {
    private final Scanner input;

    public GameUI(Scanner input) {
        this.input = input;
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    public void clearConsole() {
        // Clear console implementation
        TerminalUtils.clearConsole();
    }

    public void awaitInput() {
        input.nextLine();
    }

    public void awaitInput(String message) {
        displayMessage(message);
        input.nextLine();
    }

    public void updateDisplay(Bank bank, Player player, int round) {
        clearConsole();
        displayBoard(bank);
        displayInventory(player, round);
    }

    public void displayBoard(Bank bank) {
        displayMessage(String.format("""
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
                bank.townCards.size()));
    }

    public void sellMenu(Bank bank, HashMap<Resources, Integer> resources) {
        clearConsole();
        displayMessage("*Selling Resources*\n");
        displayPrices(bank);
        displayMessage("Resources: " + formatResources(resources) + "\n");
    }

    public void displayPrices(Bank bank) {
        displayMessage(String.format("""
                -----------------------------------|Current Prices|-------------------------------
                Prices: Wheat: $%d, Wood: $%d, Iron: $%d, Coal: $%d, Goods: $%d, Luxury: $%d
                ----------------------------------------------------------------------------------
                """,
                bank.tickers.get(Resources.WHEAT).getPrice(),
                bank.tickers.get(Resources.WOOD).getPrice(),
                bank.tickers.get(Resources.IRON).getPrice(),
                bank.tickers.get(Resources.COAL).getPrice(),
                bank.tickers.get(Resources.GOODS).getPrice(),
                bank.tickers.get(Resources.LUXURY).getPrice()));
    }

    public void productionMenu(ArrayList<ProductionCard> cards) {
        clearConsole();
        displayMessage("*Playing Production Card*\n");
    }

    public ProductionCard selectProductionCard(ArrayList<ProductionCard> cards) {
        productionMenu(cards);


        int index = 0;
        for (ProductionCard card : cards) {
            displayMessage((index + 1) + ") " + card);
            index++;
        }

        try {
            int choice = Integer.parseInt(getUserInput("Select a production card (or type '0' to cancel): ").trim());
            if (choice < 0 || choice > cards.size()) {
                displayMessage("Invalid choice. Aborting production.");
                return null;
            } else if (choice == 0) {
                return null; // Cancel option selected
            } else {
                return cards.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid choice. Aborting production.");
            return null;
        }
    }

    public void displayInventory(Player player, int round) {
        displayMessage(String.format("(Round %d) Player %s\n", round, player.getName()));
        displayMessage("Money: $" + player.getMoney());
        displayMessage("Resources: " + formatResources(player.getResources()));
        displayMessage("Railroads: " + formatCards(player.getRailroadCards()));
        displayMessage("Towns: " + formatCards(player.getTownCards()));
        displayMessage("Productions: \n" + formatProductionCards(player.getProductionCards()));
    }

    public Resources selectResource(HashMap<Resources, Integer> resources) {
        ArrayList<Resources> availableResources = new ArrayList<>();
        for (Resources resource : Resources.values()) {
            if (resources.containsKey(resource) && resources.get(resource) > 0) {
                availableResources.add(resource);
            }
        }

        if (availableResources.isEmpty()) {
            displayMessage("You don't have any resources to select.");
            return null; // Or handle the lack of resources in your game logic
        }

        int index = 0;
        for (Resources resource : availableResources) {
            displayMessage((index + 1) + ") " + capitalize(resource.name().toLowerCase()));
            index++;
        }

        try {
            int choice = Integer.parseInt(getUserInput("Select a resource (or type '0' to cancel): ").trim());
            if (choice < 0 || choice > availableResources.size()) {
                displayMessage("Invalid choice. Try again.");
                return selectResource(resources);
            } else if (choice == 0) {
                return null; // Cancel option selected
            } else {
                return availableResources.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid choice. Try again.");
            return selectResource(resources);
        }
    }


    public String formatResources(HashMap<Resources, Integer> resources) {
        if (resources.values().stream().allMatch(count -> count == 0)) {
            return "NONE";
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

        return resourceDisplay.toString();
    }

    private String formatCards(ArrayList<?> cards) {
        if (cards.isEmpty()) {
            return "NONE";
        }

        StringBuilder cardDisplay = new StringBuilder();
        for (Object card : cards) {
            if (!cardDisplay.isEmpty()) {
                cardDisplay.append(", ");
            }
            cardDisplay.append(card);
        }

        return cardDisplay.toString();
    }

    String formatProductionCards(ArrayList<ProductionCard> cards) {
        StringBuilder cardDisplay = new StringBuilder();
        for (ProductionCard card : cards) {
            cardDisplay.append(card).append("\n");
        }
        return cardDisplay.toString();
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
