import java.io.Serializable;
import java.util.ArrayList;

public class Record implements Serializable {
    private final int id;
    private final String name;
    private final ArrayList<Double> coordinates;

    public Record(int id, String name, ArrayList<Double> coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }
}
