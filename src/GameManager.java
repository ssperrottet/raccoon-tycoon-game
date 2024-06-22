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

    private void gameLoop() {
        while (true) {
            switch (gameState) {
                case MENU -> showMenu();
                case ACTIVE -> {
                    playTurn();
                }
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

        displayBoard();
        handlePlayerMove();
    }

    private void displayInventory(Player p) {
        System.out.println("Money: $" + p.getMoney());
        System.out.println("Resources: " + p.getResources());
        System.out.println("Productions:");
        displayProductionCards(p.getProductionCards());

        System.out.println("Railroads:");
        displayRailroadCards(p.getRailroadCards());

        System.out.println("Towns:");
        displayTownCards(p.getTownCards());
    }

    private void handlePlayerMove() {
        System.out.println("""
                Available Moves:
                PROD: Play a production card
                SELL: Sell a resource
                RAIL: Start an auction on a railroad
                TOWN: Purchase a town card""");
        String playerMove = input.nextLine().trim();
        Player p = players[activePlayer];
        switch (playerMove.toUpperCase()) {
            case "END" -> endTurn();
            case "VP" -> {
                p.incrementVictoryPoints();
                endTurn();
            }
            case "PROD" -> {
                if (production(p)) {
                    endTurn();
                    return;
                }
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

    private boolean production(Player p) {
        ArrayList<ProductionCard> cards = p.getProductionCards();
        System.out.println("""
                *Playing Production Card*

                Available Cards:""");

        displayProductionCards(cards);

        System.out.print("Production Card Index: ");
        try {
            int choice = Integer.parseInt(input.nextLine());
            ProductionCard card = cards.get(choice - 1);
            playProductionCard(p, card);
            return  true;
        }
        catch (Exception e) {
            System.out.println("\nABORTING\n");
            return false;
        }
    }

    private void playProductionCard(Player p, ProductionCard card) {
        HashMap<Resources, Integer> playerResources = p.getResources();
        ArrayList<ProductionCard> playerProductions = p.getProductionCards();

        ArrayList<Resources> price = card.getPriceResources();
        ArrayList<Resources> production = card.getProductionResources();

        for (Resources resource : price) {
            bank.tickers.get(resource).changeLevel(1);
        }

        for (Resources resource : production) {
            if (playerResources.size() == p.getResourceCap()) {
                //TODO
            }
            int originalCount = playerResources.get(resource);
            playerResources.put(resource, originalCount + 1);
        }
        playerProductions.remove(card);
        playerProductions.add(bank.drawProductionCard());

    }

    private void displayBoard() {
        System.out.printf("""
                RACCOON TYCOON
                
                Resource Prices:
                Wheat: $%d
                Wood: $%d
                Iron: $%d
                Coal: $%d
                Goods: $%d
                Luxury: $%d
                
                Railroads (%d remaining)
                %s
                %s
                
                Towns: (%d remaining)
                %s
                
                Buildings: (TODO remaining)
                TODO
                
                """,

                bank.tickers.get(Resources.WHEAT).getPrice(),
                bank.tickers.get(Resources.WOOD).getPrice(),
                bank.tickers.get(Resources.IRON).getPrice(),
                bank.tickers.get(Resources.COAL).getPrice(),
                bank.tickers.get(Resources.GOODS).getPrice(),
                bank.tickers.get(Resources.LUXURY).getPrice(),

                bank.railroadCards.size(),
                bank.railroadSlot1,
                bank.railroadSlot2,

                bank.townCards.size(),
                bank.townSlot
                );
        System.out.printf("(Round %d) Player %s's Turn:\n", round, players[activePlayer].getName());
        displayInventory(players[activePlayer]);
    }

    private void displayProductionCards(ArrayList<ProductionCard> cards) {
        int cardId = 1;
        for (ProductionCard card : cards) {
            System.out.print(cardId + ") ");
            cardId++;
            System.out.println(card);
        }
    }

    private void displayTownCards(ArrayList<TownCard> cards) {
        if (cards.isEmpty()) {
            //System.out.println("No Towns");
            return;
        }
        for (TownCard card : cards) {
            System.out.println(card);
        }
    }

    private void displayRailroadCards(ArrayList<RailroadCard> cards) {
        if (cards.isEmpty()) {
            //System.out.println("No Railroads");
            return;
        }
        for (RailroadCard card : cards) {
            System.out.println(card);
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

    void showMenu() {
        System.out.println("RACCOON TYCOON");
        while (true) {
            System.out.println("Type START to start the game: ");
            if (input.nextLine().trim().equalsIgnoreCase("START")) {
                gameState = GameState.ACTIVE;
                startGame();
                break;
            }
        }
    }

    void startGame() {

        initializeBank();
        initializePlayers();
        System.out.println("Let the games begin!");
        gameLoop();
    }

    private void endTurn() {
        displayBoard();
        System.out.println("Turn over\nPRESS ENTER TO CONTINUE");
        input.nextLine();
        activePlayer = (activePlayer + 1) % PLAYER_COUNT;
        if (activePlayer == 0)
            round++;
        if (round > MAX_ROUNDS) {
            gameState = GameState.END;
        }
    }
}