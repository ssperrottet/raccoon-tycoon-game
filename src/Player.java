import java.util.HashMap;

public class Player {
    private final String name;
    private int money;
    private int victoryPoints;

    private HashMap<Resources, Integer> resources = new HashMap<>();

    public Player(String name) {
        this.name = name;
        this.money = 10;
        this.victoryPoints = 0;

        for (Resources resource : Resources.values()) {
            resources.put(resource, 0);
        }
    }

    public String getName() {
        return name;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void incrementVictoryPoints() {
        this.victoryPoints++;
    }

    public HashMap<Resources, Integer> getResources() {
        return resources;
    }
}