import java.io.Serializable;
import java.util.ArrayList;

public class BoundingRectangle implements Serializable {
    private final ArrayList<Bounds> boundaries;
    private double area;
    private double perimeter;


    public BoundingRectangle(ArrayList<Bounds> boundaries) {
        this.boundaries = boundaries;
        calculateArea();
        calculatePerimeter();
    }

    private void calculatePerimeter() {
        perimeter = 0;
        for (Bounds bounds : boundaries) {
            perimeter += (bounds.getUpperBound() - bounds.getLowerBound());
        }
    }

    private void calculateArea() {
        area = 1.0;
        for (Bounds bounds : boundaries) {
            area *= (bounds.getUpperBound() - bounds.getLowerBound());
        }
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

    public double getPerimeter() {
        return perimeter;
    }

    public static boolean checkOverlap(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB) {
        // For every dimension find the intersection point
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double overlapD = calculateOverlapInDimension(boundingRectangleA, boundingRectangleB, i);

            if (overlapD < 0) //TODO check if "=" is needed or not
                return false;
        }
        return true;
    }

    // Calculates and returns the overlap value between two bounding boxes
    public static double calculateOverlapValue(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB) {
        double overlapValue = 1;
        // For every dimension find the intersection point
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double overlapD = calculateOverlapInDimension(boundingRectangleA, boundingRectangleB, i);

            if (overlapD <= 0) //TODO check if "=" is needed or not
                return 0; // No overlap, return 0
            else
                overlapValue *= overlapD;
        }
        return overlapValue;
    }

    //TODO: Δες αν χρειάζεται και που αυτή συνάρτηση

    // Calculates and returns the euclidean distance value between two bounding boxes's centers
//    public static double findDistanceBetweenBoundingBoxes(BoundingBox boundingBoxA, BoundingBox boundingBoxB) {
//        double distance = 0;
//        // For every dimension find the intersection point
//        for (int d = 0; d < DataHandler.getCurrentDimensions(); d++)
//        {
//            distance += Math.pow(boundingBoxA.getCenter().get(d) - boundingBoxB.getCenter().get(d),2);
//        }
//        return sqrt(distance);
//    }

    private static double calculateOverlapInDimension(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB, int dimension) {
        return Math.min(boundingRectangleA.getBoundsOfDimension(dimension).getUpperBound(),
                boundingRectangleB.getBoundsOfDimension(dimension).getUpperBound())
                -
                Math.max(boundingRectangleA.getBoundsOfDimension(dimension).getLowerBound(),
                        boundingRectangleB.getBoundsOfDimension(dimension).getLowerBound());
    }


}
