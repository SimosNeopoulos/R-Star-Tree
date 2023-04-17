import java.io.Serializable;
import java.util.ArrayList;

public class BoundingRectangle implements Serializable {
    private final ArrayList<Bounds> boundaries;
    private double area;
    private double margin;
    private ArrayList<Double> center;


    public BoundingRectangle(ArrayList<Bounds> boundaries) {
        this.boundaries = boundaries;
        calculateArea();
        calculateMargin();
        calculateCenter();
    }

    private void calculateMargin() {
        margin = 0;
        for (Bounds bounds : boundaries) {
            margin += (bounds.getUpperBound() - bounds.getLowerBound());
        }
    }

    private void calculateArea() {
        area = 1.0;
        for (Bounds bounds : boundaries) {
            area *= (bounds.getUpperBound() - bounds.getLowerBound());
        }
    }

    private void calculateCenter() {
        center = new ArrayList<>();

        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            Bounds currentBound = boundaries.get(i);
            center.add((currentBound.getUpperBound() + currentBound.getLowerBound()) / 2);
        }
    }

    public ArrayList<Double> getCenter() {
        return center;
    }

    public Bounds getBoundsOfDimension(int dimension) {
        return this.boundaries.get(dimension);
    }

    public ArrayList<Bounds> getBoundaries() {
        return boundaries;
    }

    public double getArea() {
        return area;
    }

    public double getMargin() {
        return margin;
    }

}
