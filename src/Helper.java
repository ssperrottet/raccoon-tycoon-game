import java.util.ArrayList;

public class Helper {
    public static String stringifyArray(int[] array) {
        StringBuilder a = new StringBuilder("[");

        for (int i = 0; i < array.length; i++) {
            a.append(array[i]);
            if (i < array.length - 1) {
                a.append(", ");
            }
        }
        return a + "]";
    }

    public static String stringifyArray(ArrayList<Resources> array) {
        StringBuilder a = new StringBuilder("[");

        for (int i = 0; i < array.size(); i++) {
            a.append(array.get(i));
            if (i < array.size() - 1) {
                a.append(", ");
            }
        }
        return a + "]";
    }
}
