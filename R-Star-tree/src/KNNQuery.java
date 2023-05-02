import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<LeafEntry> getEntriesFromPriorityQueue() {
        ArrayList<EntryComparator> knnComparatorList = new ArrayList<>();
        while (!this.kNNs.isEmpty()) {
            knnComparatorList.add(kNNs.poll());
        }
        Collections.reverse(knnComparatorList);

        return EntriesCalculator.getLeafEntriesFromEntryComparator(knnComparatorList);
    }

    private void kNNQuery(Node node) {

    }
}
