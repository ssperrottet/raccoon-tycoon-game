public class TownCard {
    private final int level;
    private final Resources type;

    // Example prices associated with each level
    private static final int[] PRICES = {2, 3, 4, 5};
    private static final int[] ALT_PRICES = {4, 5, 6, 8};

    public TownCard(int level, Resources type) {
        this.level = level;
        this.type = type;
    }

    public int getPrice() {
        return PRICES[level - 2];
    }

    public int getAltPrice() {
        return ALT_PRICES[level - 2];
    }

    public Resources getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "TownCard{" +
                "level=" + level +
                ", type=" + type +
                ", price=[" + getPrice() + " " + type + " or " + getAltPrice() + " ANY]" +
                '}';
    }
}
