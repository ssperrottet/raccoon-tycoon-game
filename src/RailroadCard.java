import java.util.HashMap;

public class RailroadCard {
    public enum Types {
        FOX, SKUNK, BEAR, CAT, RACCOON, DOG
    }

    private final Types type;

    // Example values associated with each type
    private static final HashMap<Types, int[]> values = new HashMap<>();

    static {
        values.put(Types.FOX, new int[]{1, 2, 3});
        values.put(Types.SKUNK, new int[]{4, 5, 6});
        values.put(Types.BEAR, new int[]{7, 8, 9});
        values.put(Types.CAT, new int[]{10, 11, 12});
        values.put(Types.RACCOON, new int[]{13, 14, 15});
        values.put(Types.DOG, new int[]{16, 17, 18});
    }

    public RailroadCard(Types type) {
        this.type = type;
    }

    public Types getType() {
        return type;
    }

    public int[] getValues() {
        return values.get(type);
    }

    @Override
    public String toString() {
        return "RailroadCard{" +
                "type=" + type +
                ", values=" + Helper.stringifyArray(getValues()) +
                '}';
    }
}
