import java.io.Serializable;
import java.util.ArrayList;

// Κλάση που αναπαριστά τα records του datafile
public class Record implements Serializable {
    private final long id;
    private final ArrayList<Double> coordinates;

    public Record(int id, ArrayList<Double> coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

    public Record(String[] array) {
        coordinates = new ArrayList<>();
        id = Long.parseLong(array[0]);
        for (int i = 1; i < array.length; i++) {
            coordinates.add(Double.parseDouble(array[i]));
        }
    }

    public double getCoordinate(int i) {
        return coordinates.get(i);
    }

    public long getId() {
        return id;
    }
}
