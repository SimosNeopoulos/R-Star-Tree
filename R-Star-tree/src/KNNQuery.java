import java.util.ArrayList;
import java.util.PriorityQueue;

public class KNNQuery {
    private int k;
    private double searchRadius;
    private BoundingRectangle point;
    private PriorityQueue<EntryComparator> kNNs;

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

    private void kNNQuery(Node node) {
        ArrayList<EntryComparator> entries = getSortedEntriesForKNN(node);

        if (!node.isLeaf()) {
            for (EntryComparator entry : entries) {
                if (entry.getValueToCompare() > this.searchRadius) {
                    break;
                }
                NonLeafEntry nonLeafEntry = (NonLeafEntry) entry.getEntry();
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
