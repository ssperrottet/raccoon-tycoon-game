public class ResourceTicker {
    private final int startingPrice;
    private final int levels;
    private int level;

    public ResourceTicker(int startingPrice, int levels) {
        if (startingPrice < 0 || levels < 0) {
            throw new IllegalArgumentException("Starting price and levels must be non-negative.");
        }
        this.startingPrice = startingPrice;
        this.levels = levels;
        this.level = 0;
    }

    public void changeLevel(int amount) {
        level = level + amount;
        level = Math.max(0, Math.min(level, levels));
    }

    public int getPrice() {
        return startingPrice + level;
    }
}
