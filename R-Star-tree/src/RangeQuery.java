import java.util.ArrayList;

// Συνάρτηση που υλοποιεί τον αλγόριθμο Range Query
public class RangeQuery {
    private ArrayList<LeafEntry> validEntries;
    private Node root;
    private BoundingRectangle searchArea;

    public RangeQuery(Node root, BoundingRectangle searchArea) {
        this.root = root;
        this.searchArea = searchArea;
    }

    public ArrayList<LeafEntry> rangeQuerySearch() {
        validEntries = new ArrayList<>();
        search(root, searchArea);
        return validEntries;
    }

    public ArrayList<LeafEntry> rangeQuerySearch(BoundingRectangle searchArea) {
        validEntries = new ArrayList<>();
        this.searchArea = searchArea;
        search(root, this.searchArea);
        return validEntries;
    }

    private void search(Node node, BoundingRectangle searchBox) {
        if (!node.isLeaf()) {
            for (Entry entry : node.getEntries()) {
                NonLeafEntry nonLeafEntry = (NonLeafEntry) entry;

                if (EntriesCalculator.checkOverlap(entry.getBoundingRectangle(), searchBox)) {
                    search(DataHandler.getNodeFromIndexFile(nonLeafEntry.getChildPTR()), searchBox);
                }
            }

        } else {

            for (Entry entry : node.getEntries()) {

                LeafEntry leafEntry = (LeafEntry) entry;
                if (EntriesCalculator.checkOverlap(leafEntry.getBoundingRectangle(), searchBox)) {
                    validEntries.add(leafEntry);
                }
            }
        }
    }


}
