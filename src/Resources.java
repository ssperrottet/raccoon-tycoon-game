public enum Resources {
    WHEAT,
    WOOD,
    IRON,
    COAL,
    GOODS,
    LUXURY;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
