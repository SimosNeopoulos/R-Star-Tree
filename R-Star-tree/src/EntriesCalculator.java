import java.util.ArrayList;

public class EntriesCalculator {
    private static int dimensionNum = DataHandler.getCurrentDimensions();

    // Returns the minimum bounding box that contains the entries passed as a parameter
    public static BoundingRectangle getMinimumBoundingBoxForEntries(ArrayList<Entry> entries) {
        ArrayList<Double> minBounds = new ArrayList<>();
        ArrayList<Double> maxBounds = new ArrayList<>();

        for (int i = 0; i < dimensionNum; i++) {
            minBounds.add(Double.MAX_VALUE);
            maxBounds.add(Double.MIN_VALUE);
        }

        for (Entry entry : entries) {
            for (int i = 0; i < dimensionNum; i++) {
                double entryLowerBound = entry.getBoundingBox().getBoundaries().get(i).getLowerBound();
                double entryUpperBound = entry.getBoundingBox().getBoundaries().get(i).getUpperBound();

                minBounds.set(i, Math.min(entryLowerBound, minBounds.get(i)));
                maxBounds.set(i, Math.max(entryUpperBound, maxBounds.get(i)));
            }
        }

        ArrayList<Bounds> boundaries = new ArrayList<>();
        for (int i = 0; i < dimensionNum; i++) {
            boundaries.add(new Bounds(maxBounds.get(i), minBounds.get(i)));
        }

        return new BoundingRectangle(boundaries);
    }
}
