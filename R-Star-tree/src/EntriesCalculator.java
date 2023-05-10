import java.util.ArrayList;

import static java.lang.Math.sqrt;

// Κλάση με στατικές μεθόδους
public class EntriesCalculator {
    // Ο αριθμός των διαστάσεων των records
    private final static int dimensionNum = DataHandler.getCurrentDimensions();

    // Συνάρτηση που υπολογίζει το minimum bounding rectangle με βάση τα entries που περνιούνται ως παράμετρος
    public static BoundingRectangle getMinimumBoundingRectangleForEntries(ArrayList<Entry> entries) {
        // Minimum και maximum bounds για κάθε διάσταση
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

    // Παρόμοια με την "getMinimumBoundingRectangleForEntries"
    // Συνάρτηση που υπολογίζει το νέο minimum bounding rectangle αν της μεταβλητής "boundingRectangle"
    // αν θελήσουμε να περικλείει και την μεταβλητή "entry"
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
            maxBounds.set(i, Math.max(entryBounds.get(i).getUpperBound(), oldBoundingRectangleBounds.get(i).getUpperBound()));
        }

        ArrayList<Bounds> boundaries = new ArrayList<>();
        for (int i = 0; i < dimensionNum; i++) {
            boundaries.add(new Bounds(maxBounds.get(i), minBounds.get(i)));
        }

        return new BoundingRectangle(boundaries);
    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο "chooseSplitAxis".
    // TODO: να δω αν θέλω να προσθέσω σχόλια
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

    // Συνάρτηση που υλοποιεί τον αλγόριθμο "chooseSplitIndex".
    // TODO: να δω αν θέλω να προσθέσω σχόλια
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

    // Συνάρτηση που δέχεται ενα ArrayList<EntryComparator> και επιστρέφει ένα ArrayList με τα entries
    // απο τα EntryComparator αντικείμενα
    public static ArrayList<Entry> getEntriesFromEntryComparator(ArrayList<EntryComparator> comparatorEntries) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (EntryComparator comparatorEntry : comparatorEntries) {
            entries.add(comparatorEntry.getEntry());
        }
        return entries;
    }

    // Συνάρτηση που δέχεται ενα ArrayList<EntryComparator> και επιστρέφει ένα ArrayList με τα leaf entries
    // απο τα EntryComparator αντικείμενα
    public static ArrayList<LeafEntry> getLeafEntriesFromEntryComparator(ArrayList<EntryComparator> comparatorEntries) {
        ArrayList<LeafEntry> entries = new ArrayList<>();
        for (EntryComparator comparatorEntry : comparatorEntries) {
            entries.add((LeafEntry) comparatorEntry.getEntry());
        }
        return entries;
    }

    // Συνάρτηση που υπολογίζει τη minimum απόσταση ενός boundingRectangle απο ένα σημείο (point),
    // σύμφωνα με τον αλγόριθμο MINDIST(R,P)
    public static double calculateMinDistanceFromPoint(BoundingRectangle boundingRectangle, BoundingRectangle point) {
        double minDistance = 0;

        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double pi = point.getBoundsOfDimension(i).getLowerBound();
            Bounds currentBound = boundingRectangle.getBoundsOfDimension(i);
            double ri;

            if (currentBound.getLowerBound() > pi) {
                ri = currentBound.getLowerBound();
            } else ri = Math.min(currentBound.getUpperBound(), pi);

            minDistance += Math.pow(pi - ri, 2);
        }

        return minDistance;
    }

    // Συνάρτηση που ελέγχει αν υπάρχει overlap μεταξύ δυο bounding rectangles
    public static boolean checkOverlap(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB) {
        // Έλεγχος overlap για κάθε διάσταση
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double overlap = calculateOverlapInDimension(boundingRectangleA.getBoundsOfDimension(i),
                    boundingRectangleB.getBoundsOfDimension(i));

            // Αν δεν υπάρχει overlap έστω και σε μια διάσταση δεν υπάρχει overlap
            if (overlap < 0) //TODO check if "=" is needed or not
                return false;
        }
        return true;
    }

    // Συνάρτηση που υπολογίζει το overlap value μεταξύ δυο bounding rectangles
    public static double calculateOverlapValue(BoundingRectangle boundingRectangleA, BoundingRectangle boundingRectangleB) {
        double overlapValue = 1;
        // Έλεγχος overlap για κάθε διάσταση
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double overlap = calculateOverlapInDimension(boundingRectangleA.getBoundsOfDimension(i),
                    boundingRectangleB.getBoundsOfDimension(i));

            // Αν δεν υπάρχει overlap έστω και σε μια διάσταση δεν υπάρχει overlap
            if (overlap <= 0) {
                return 0;
            }
            overlapValue *= overlap;
        }
        return overlapValue;
    }

    // Συνάρτηση που υπολογίζει την απόσταση των κέντρων μεταξύ δυο entries
    public static double calculateCenterDistanceOfEntries(Entry entryA, Entry entryB) {
        double distance = 0;
        // For every dimension find the intersection point
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            distance += Math.pow(entryA.getBoundingRectangle().getCenter().get(i) - entryB.getBoundingRectangle().getCenter().get(i), 2);
        }
        return sqrt(distance);
    }

    // Υπολογισμός του overlap value ανάμεσα σε δυο bounds
    private static double calculateOverlapInDimension(Bounds boundA, Bounds boundB) {
        return Math.min(boundA.getUpperBound(),boundB.getUpperBound())
                -
               Math.max(boundA.getLowerBound(), boundB.getLowerBound());
    }


}
