import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Bank {
    private static final int TICKER_LEVELS = 12;

    public HashMap<Resources, ResourceTicker> tickers = new HashMap<>();

    public List<RailroadCard> railroadCards = new ArrayList<>();
    public RailroadCard railroadSlot1;
    public RailroadCard railroadSlot2;

    public List<TownCard> townCards = new ArrayList<>();
    public TownCard townSlot;

    public List<ProductionCard> productionCards = new ArrayList<ProductionCard>();


    public void initializeTickers() {
        tickers.put(Resources.WHEAT, new ResourceTicker(1, TICKER_LEVELS));
        tickers.put(Resources.WOOD, new ResourceTicker(1, TICKER_LEVELS));
        tickers.put(Resources.IRON, new ResourceTicker(2, TICKER_LEVELS));
        tickers.put(Resources.COAL, new ResourceTicker(2, TICKER_LEVELS));
        tickers.put(Resources.GOODS, new ResourceTicker(3, TICKER_LEVELS));
        tickers.put(Resources.LUXURY, new ResourceTicker(3, TICKER_LEVELS));
    }

    private void loadRailroadCards(int playerCount) {
        for (int i = 0; i < 4; i++) {
            railroadCards.add(new RailroadCard(RailroadCard.types.BEAR));
            railroadCards.add(new RailroadCard(RailroadCard.types.DOG));
            railroadCards.add(new RailroadCard(RailroadCard.types.CAT));
            if (playerCount > 2)
                railroadCards.add(new RailroadCard(RailroadCard.types.FOX));
            if (playerCount > 3)
                railroadCards.add(new RailroadCard(RailroadCard.types.RACCOON));
            if (playerCount > 4)
                railroadCards.add(new RailroadCard(RailroadCard.types.SKUNK));
        }
    }
    private void loadProductionCards() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/production_cards.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    ArrayList<Resources> productionResources = parseResources(parts[0]);
                    ArrayList<Resources> priceResources = parseResources(parts[1]);
                    ProductionCard card = new ProductionCard(productionResources, priceResources);
                    productionCards.add(card);
                } else {
                    System.err.println("Invalid card format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public TownCard drawTownCard() {
        return townCards.remove(townCards.size() - 1);
    }

    public ProductionCard drawProductionCard() {
        return productionCards.remove(productionCards.size() - 1);
    }

    public RailroadCard drawRailroadCard() {
        return railroadCards.remove(railroadCards.size() - 1);
    }

    private void loadTownCards() {

        for (int i = 3; i >= 0; i--) {
            ArrayList<Resources> townCardResources = new ArrayList<>();
            townCardResources.add(Resources.IRON);
            townCardResources.add(Resources.COAL);
            townCardResources.add(Resources.GOODS);
            townCardResources.add(Resources.LUXURY);

            Collections.shuffle(townCardResources);

            townCards.add(new TownCard(i + 2, townCardResources.get(0)));
            townCards.add(new TownCard(i + 2, townCardResources.get(1)));
            townCards.add(new TownCard(i + 2, townCardResources.get(2)));
            townCards.add(new TownCard(i + 2, townCardResources.get(3)));
        }
    }

    private ArrayList<Resources> parseResources(String resourcesPart) {
        String[] resourceStrings = resourcesPart.split(",");
        ArrayList<Resources> resources = new ArrayList<>();
        for (String resourceString : resourceStrings) {
            try {
                Resources resource = Resources.valueOf(resourceString.trim().toUpperCase());
                resources.add(resource);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid resource: " + resourceString.trim());
            }
        }
        return resources;
    }

    public void initializeCards(int playerCount) {
        loadProductionCards();
        Collections.shuffle(productionCards);

        loadRailroadCards(playerCount);
        Collections.shuffle(railroadCards);

        loadTownCards();

        railroadSlot1 = drawRailroadCard();
        railroadSlot2 = drawRailroadCard();
        townSlot = drawTownCard();
    }

    public Map<Resources, ResourceTicker> getTickers() {
        return tickers;
    }
}
