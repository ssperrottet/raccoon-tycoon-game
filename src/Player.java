public class Player {
    private final String name;
    private int victoryPoints;

    public Player(String name) {
        this.name = name;
        this.victoryPoints = 0;
    }

    public String getName() {
        return name;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void incrementVictoryPoints() {
        this.victoryPoints++;
    }
}