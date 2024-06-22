public class TownCard {
    private final int level;
    private final Resources type;
    // levels : 2, 3, 4, 5 (victory points)
    private static final int[] PRICES = new int[] {2, 3, 4, 5};
    private static final int[] ALT_PRICES = new int[] {4, 5, 6, 8};
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

    public String toString() {
        return "Town{Level=" + level + ", price=[" + getPrice() + " " + type + " or " + getAltPrice() + " ANY]}";
    }
}
