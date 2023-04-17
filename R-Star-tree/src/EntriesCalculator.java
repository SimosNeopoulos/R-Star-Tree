import java.util.ArrayList;

import static java.lang.Math.sqrt;

public class EntriesCalculator {
    private static int dimensionNum = DataHandler.getCurrentDimensions();

    // Returns the minimum bounding box that contains the entries passed as a parameter
    public static BoundingRectangle getMinimumBoundingRectangleForEntries(ArrayList<Entry> entries) {
        ArrayList<Double> minBounds = new ArrayList<>();
        ArrayList<Double> maxBounds = new ArrayList<>();

        for (int i = 0; i < dimensionNum; i++) {
            minBounds.add(Double.MAX_VALUE);
            maxBounds.add(Double.MIN_VALUE);
        }

        for (Entry entry : entries) {
            for (int i = 0; i < dimensionNum; i++) {
                double entryLowerBound = entry.getBoundingRectangle().getBoundaries().get(i).getLowerBound();
                double entryUpperBound = entry.getBoundingRectangle().getBoundaries().get(i).getUpperBound();

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

    public static BoundingRectangle getNewMinBoundingRectangle(BoundingRectangle boundingRectangle, Entry entry) {
        ArrayList<Double> minBounds = new ArrayList<>();
        ArrayList<Double> maxBounds = new ArrayList<>();

        for (int i = 0; i < dimensionNum; i++) {
            minBounds.add(Double.MAX_VALUE);
            maxBounds.add(Double.MIN_VALUE);
        }

        ArrayList<Bounds> entryBounds = entry.getBoundingRectangle().getBoundaries();
        ArrayList<Bounds> oldBoundingRectangleBounds = boundingRectangle.getBoundaries();

        for (int i = 0; i < dimensionNum; i++) {
            minBounds.set(i, Math.min(entryBounds.get(i).getLowerBound(), oldBoundingRectangleBounds.get(i).getLowerBound()));
            maxBounds.set(i, Math.min(entryBounds.get(i).getUpperBound(), oldBoundingRectangleBounds.get(i).getUpperBound()));
        }

        ArrayList<Bounds> boundaries = new ArrayList<>();
        for (int i = 0; i < dimensionNum; i++) {
            boundaries.add(new Bounds(maxBounds.get(i), minBounds.get(i)));
        }

        return new BoundingRectangle(boundaries);
    }

    public static ArrayList<Distributions> chooseSplitAxis(Node nodeToSplit) {

        ArrayList<Distributions> splitAxis = new ArrayList<>();

        double distributionsMarginSum = Double.MAX_VALUE;

        for (int i = 0; i < dimensionNum; i++) {
            ArrayList<EntryComparator> sortedByUpperTemp = new ArrayList<>();
            ArrayList<EntryComparator> sortedByLowerTemp = new ArrayList<>();

            for (Entry entry : nodeToSplit.getEntries()) {
                sortedByUpperTemp.add(new EntryComparator(entry.getBoundingRectangle().
                        getBoundsOfDimension(i).getUpperBound(), entry));

                sortedByLowerTemp.add(new EntryComparator(entry.getBoundingRectangle().
                        getBoundsOfDimension(i).getLowerBound(), entry));
            }

            sortedByUpperTemp.sort(new EntryComparator(0.0, null));
            sortedByLowerTemp.sort(new EntryComparator(0.0, null));

            ArrayList<ArrayList<Entry>> sortedArrayLists = new ArrayList<>();
            sortedArrayLists.add(getEntriesFromEntryComparator(sortedByUpperTemp));
            sortedArrayLists.add(getEntriesFromEntryComparator(sortedByLowerTemp));

            double distributionsMarginSumTemp = 0;
            ArrayList<Distributions> distributions = new ArrayList<>();

            for (ArrayList<Entry> entries : sortedArrayLists) {
                int nodeMaxEntries = DataHandler.getMaxEntriesPerNode();
                int nodeMinEntries = DataHandler.getMinEntriesPerNode();
                int kDistributionNum = nodeMaxEntries - 2 * nodeMinEntries + 2;

                for (int k = 1; k < kDistributionNum; k++) {
                    ArrayList<Entry> groupA = new ArrayList<>();
                    ArrayList<Entry> groupB = new ArrayList<>();

                    for (int j = 0; j < (nodeMinEntries - 1) + k; j++) {
                        groupA.add(entries.get(j));
                    }

                    for (int j = (nodeMinEntries - 1) + k; j < entries.size(); j++) {
                        groupB.add(entries.get(j));
                    }

                    BoundingRectangle boundingRectangleA = EntriesCalculator.getMinimumBoundingRectangleForEntries(groupA);
                    BoundingRectangle boundingRectangleB = EntriesCalculator.getMinimumBoundingRectangleForEntries(groupB);

                    distributions.add(new Distributions(new DistributionGroup(groupA, boundingRectangleA),
                            new DistributionGroup(groupB, boundingRectangleB)));

                    distributionsMarginSumTemp += boundingRectangleA.getMargin() + boundingRectangleA.getMargin();

                    if (distributionsMarginSumTemp < distributionsMarginSum) {
                        distributionsMarginSum = distributionsMarginSumTemp;
                        splitAxis = distributions;
                    }
                }
            }
        }

        return splitAxis;
    }

    public static ArrayList<Node> chooseSplitIndex(ArrayList<Distributions> splitAxis, int levelOnTree) {

        double minOverlapValue = Double.MAX_VALUE;
        double minAreaValue = Double.MAX_VALUE;

        int splitIndex = 0;

        for (int i = 0; i < splitAxis.size(); i++) {
            DistributionGroup groupA = splitAxis.get(i).getGroupA();
            DistributionGroup groupB = splitAxis.get(i).getGroupB();

            double overlapValueOfDistribution = calculateOverlapValue(groupA.getBoundingRectangle(), groupB.getBoundingRectangle());

            if (overlapValueOfDistribution < minOverlapValue) {
                minOverlapValue = overlapValueOfDistribution;
                minAreaValue = groupA.getBoundingRectangle().getArea() + groupB.getBoundingRectangle().getArea();
                splitIndex = i;
            } else if (overlapValueOfDistribution == minOverlapValue) {
                double areaValueOfDistribution = groupA.getBoundingRectangle().getArea() + groupB.getBoundingRectangle().getArea();

                if (areaValueOfDistribution < minAreaValue) {
                    minAreaValue = areaValueOfDistribution;
                    splitIndex = i;
                }
            }
        }

        ArrayList<Node> splittedNodes = new ArrayList<>();

        DistributionGroup bestGroupA = splitAxis.get(splitIndex).getGroupA();
        DistributionGroup bestGroupB = splitAxis.get(splitIndex).getGroupB();

        splittedNodes.add(new Node(bestGroupA.getEntries(), levelOnTree));
        splittedNodes.add(new Node(bestGroupB.getEntries(), levelOnTree));

        return splittedNodes;
    }

    public static ArrayList<Entry> getEntriesFromEntryComparator(ArrayList<EntryComparator> comparatorEntries) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (EntryComparator comparatorEntry : comparatorEntries) {
            entries.add(comparatorEntry.getEntry());
        }
        return entries;
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

    public static double calculateCenterDistanceOfEntries(Entry entryA, Entry entryB) {
        double distance = 0;
        // For every dimension find the intersection point
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            distance += Math.pow(entryA.getBoundingRectangle().getCenter().get(i) - entryB.getBoundingRectangle().getCenter().get(i), 2);
        }
        return sqrt(distance);
    }

    private static double calculateOverlapInDimension(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB, int dimension) {
        return Math.min(boundingRectangleA.getBoundsOfDimension(dimension).getUpperBound(),
                boundingRectangleB.getBoundsOfDimension(dimension).getUpperBound())
                -
                Math.max(boundingRectangleA.getBoundsOfDimension(dimension).getLowerBound(),
                        boundingRectangleB.getBoundsOfDimension(dimension).getLowerBound());
    }


}
