import java.util.HashMap;

public class Railroad {
    public types type;

    public Railroad(types type) {
        this.type = type;
    }

    public enum types {
        FOX, SKUNK, BEAR, CAT, RACCOON, DOG
    }

    static HashMap<types, int[]> values = new HashMap<>();

    static {
        // Initialize the HashMap with some example values
        values.put(types.FOX, new int[]{1, 2, 3});
        values.put(types.SKUNK, new int[]{4, 5, 6});
        values.put(types.BEAR, new int[]{7, 8, 9});
        values.put(types.CAT, new int[]{10, 11, 12});
        values.put(types.RACCOON, new int[]{13, 14, 15});
        values.put(types.DOG, new int[]{16, 17, 18});
    }

    // Method to retrieve the values associated with a type
    public static int[] getValuesForType(types type) {
        return values.get(type);
    }
}
