import java.io.Serializable;
import java.util.ArrayList;

public class Record implements Serializable {
    private long id;
    private String name;
    private ArrayList<Double> coordinates;

    public Record(int id, String name, ArrayList<Double> coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public Record(String[] array) {
        id = Long.parseLong(array[0]);
        for (int i = 1; i < array.length; i++) {
//            coordinates.add(Double.parseDouble(array[i]));
        }
    }

    public long getId() {
        return id;
    }
}
