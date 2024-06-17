import java.util.HashMap;

public class Bank {
    private static final int TICKER_LEVELS = 12;
    public HashMap<String, ResourceTicker> tickers = new HashMap<>();



    public void initializeTickers() {
        tickers.put("Wheat", new ResourceTicker(1, TICKER_LEVELS));
        tickers.put("Wood", new ResourceTicker(1, TICKER_LEVELS));
        tickers.put("Iron", new ResourceTicker(2, TICKER_LEVELS));
        tickers.put("Coal", new ResourceTicker(2, TICKER_LEVELS));
        tickers.put("Goods", new ResourceTicker(3, TICKER_LEVELS));
        tickers.put("Luxury", new ResourceTicker(3, TICKER_LEVELS));
    }

    public void initializeCards() {
    }
}
