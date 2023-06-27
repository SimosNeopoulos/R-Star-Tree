import java.util.ArrayList;
import java.util.PriorityQueue;

public class SkylineQuery {
    ArrayList<LeafEntry> S;
    Node root;

    public SkylineQuery(Node root) {
        this.root = root;
    }

    public ArrayList<LeafEntry> getSkylineEntries() {
        S = new ArrayList<>();
        skylineBBS();
        return S;
    }

    private boolean entryIsDominant(Entry entry) {
        for (LeafEntry entryS : S) {
            if (entry.isDominatedByEntry(entryS))
                return false;
        }
        return true;
    }

    private void skylineBBS() {
        PriorityQueue<EntryComparator> heap = new PriorityQueue<>(new EntryComparator(0.0, null, true));

        for (Entry entry : root.getEntries()) {
            heap.add(new EntryComparator(false, EntriesCalculator.getMinDistOfEntry(entry), entry));
        }

        while (!heap.isEmpty()) {
            EntryComparator entryComparator = heap.poll();
            if (!entryIsDominant(entryComparator.getEntry()))
                continue;

            if (!entryComparator.isLeafEntry()) {
                NonLeafEntry nonLeafEntry = (NonLeafEntry) entryComparator.getEntry();
                Node node = DataHandler.getNodeFromIndexFile(nonLeafEntry.getChildPTR());
                ArrayList<Entry> childEntries = node.getEntries();
                boolean entriesAreLeaf = node.isLeaf();
                for (Entry childEntry: childEntries) {
                    if (entryIsDominant(childEntry))
                        heap.add(new EntryComparator(entriesAreLeaf, EntriesCalculator.getMinDistOfEntry(childEntry), childEntry));
                }
            } else {
                S.add((LeafEntry) entryComparator.getEntry());
            }
        }
    }
}
