import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private final String name;
    private int money;
    private int victoryPoints;
    private int resourceCap = 10;

    private ArrayList<ProductionCard> productionCards = new ArrayList<>();
    private ArrayList<RailroadCard> railroadCards = new ArrayList<>();
    private ArrayList<TownCard> townCards = new ArrayList<>();

    private HashMap<Resources, Integer> resources = new HashMap<>();

    public Player(String name) {
        this.name = name;
        this.money = 10;
        this.victoryPoints = 0;

        for (Resources resource : Resources.values()) {
            resources.put(resource, 0);
        }
    }

    public ArrayList<ProductionCard> getProductionCards() {
        return productionCards;
    }

    public ArrayList<RailroadCard> getRailroadCards() {
        return railroadCards;
    }

    public ArrayList<TownCard> getTownCards() {
        return townCards;
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

    public int getResourceCap() {
        return resourceCap;
    }

    public void setResourceCap(int cap) {
        resourceCap = cap;
    }
}