import java.util.ArrayList;
import java.util.PriorityQueue;

// Κλάση που υλοποιεί τον αλγόριθμο k-Nearest Neighbour.
public class KNNQuery {
    private final int k;
    private double searchRadius;
    private final BoundingRectangle point;
    private final PriorityQueue<EntryComparator> kNNs;

    public KNNQuery(int k, BoundingRectangle point) {
        this.k = k;
        this.point = point;
        this.searchRadius = Double.MAX_VALUE;
        this.kNNs = new PriorityQueue<>(new EntryComparator(0.0, null));
    }

    public ArrayList<LeafEntry> getKNNs(Node root) {
        kNNQuery(root);
        return getEntriesFromPriorityQueue();
    }

    // Συνάρτηση που επιστέφει όλα τα entries απο το PriorityQueue σε ArrayList
    private ArrayList<LeafEntry> getEntriesFromPriorityQueue() {
        ArrayList<EntryComparator> knnComparatorList = new ArrayList<>();
        while (!this.kNNs.isEmpty()) {
            knnComparatorList.add(kNNs.poll());
        }

        return EntriesCalculator.getLeafEntriesFromEntryComparator(knnComparatorList);
    }

    private ArrayList<EntryComparator> getSortedEntriesForKNN(Node node) {
        ArrayList<EntryComparator> entries = getComparedEntriesFromNode(node);
        entries.sort(new EntryComparator(0.0, null));
        return entries;
    }

    private ArrayList<EntryComparator> getComparedEntriesFromNode(Node node) {
        ArrayList<Entry> entries = node.getEntries();
        ArrayList<EntryComparator> comparedEntries = new ArrayList<>();

        for (Entry entry : entries) {
            double distance = EntriesCalculator.calculateMinDistanceFromPoint(entry.getBoundingRectangle(), point);
            comparedEntries.add(new EntryComparator(distance, entry));
        }

        return comparedEntries;
    }


    // Η συνάρτηση που υλοποιεί τον k-Nearest Neighbour αλγόριθμο
    private void kNNQuery(Node node) {
        // Ταξινόμηση όλων των entries του node με βάση την απόσταση τους απο το σημείο (point)
        ArrayList<EntryComparator> entries = getSortedEntriesForKNN(node);

        if (!node.isLeaf()) {
            for (EntryComparator entry : entries) {
                // Αφού τα entries είναι ταξινομημένα στην περίπτωση που ένα entry είναι εκτός του searchRadius
                // δεν υπάρχει λόγος να συνεχίζουμε την αναζήτηση για κατάλληλα entries σε αυτόν τον κόμβο
                if (entry.getValueToCompare() > this.searchRadius) {
                    break;
                }
                NonLeafEntry nonLeafEntry = (NonLeafEntry) entry.getEntry();

                // Καλούμε αναδρομικά τη συνάρτηση kNNQuery για κάθε child node του "node" που τηρεί τις παραπάνω προυποθέσεις
                kNNQuery(DataHandler.getNodeFromIndexFile(nonLeafEntry.getChildPTR()));
            }
        } else {
            for (EntryComparator entry : entries) {
                if (entry.getValueToCompare() > this.searchRadius) {
                    continue;
                }
                this.kNNs.add(entry);

                if (this.kNNs.size() > k) {
                    this.kNNs.poll();
                }

                if (this.kNNs.size() == k) {
                    if (this.kNNs.peek() != null) {
                        this.searchRadius = this.kNNs.peek().getValueToCompare();
                    }
                }

            }
        }
    }


}
