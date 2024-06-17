import java.util.*;

public class Bank {
    private static final int TICKER_LEVELS = 12;
    private static final int TOWNS = 20;

    public List<Railroad> railroads = new ArrayList<>();
    public Map<Resources, ResourceTicker> tickers = new HashMap<>();

    public void initializeTickers() {
        tickers.put(Resources.WHEAT, new ResourceTicker(1, TICKER_LEVELS));
        tickers.put(Resources.WOOD, new ResourceTicker(1, TICKER_LEVELS));
        tickers.put(Resources.IRON, new ResourceTicker(2, TICKER_LEVELS));
        tickers.put(Resources.COAL, new ResourceTicker(2, TICKER_LEVELS));
        tickers.put(Resources.GOODS, new ResourceTicker(3, TICKER_LEVELS));
        tickers.put(Resources.LUXURY, new ResourceTicker(3, TICKER_LEVELS));
    }

    public void initializeCards(int playerCount) {
        for (int i = 0; i < 4; i++) {
            railroads.add(new Railroad(Railroad.types.BEAR));
            railroads.add(new Railroad(Railroad.types.DOG));
            railroads.add(new Railroad(Railroad.types.CAT));
            if (playerCount > 2)
                railroads.add(new Railroad(Railroad.types.FOX));
            if (playerCount > 3)
                railroads.add(new Railroad(Railroad.types.RACCOON));
            if (playerCount > 4)
                railroads.add(new Railroad(Railroad.types.SKUNK));
        }
        Collections.shuffle(railroads);
    }

    public Map<Resources, ResourceTicker> getTickers() {
        return tickers;
    }
}
