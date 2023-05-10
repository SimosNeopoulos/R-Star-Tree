import java.io.Serializable;
import java.util.ArrayList;

// Κλάση που αναπαριστά το ένα data node των 32KB που περιέχει τα entries που αποθηκεύονται στο index file
public class Node implements Serializable {
    private int indexBlockLocation;
    private int treeLevel;
    private ArrayList<Entry> entries;

    public Node(ArrayList<Entry> entries, int treeLevel) {
        this.treeLevel = treeLevel;
        this.entries = entries;
    }

    public Node(int indexBlockLocation, int treeLevel) {
        this.indexBlockLocation = indexBlockLocation;
        this.treeLevel = treeLevel;
        entries = new ArrayList<>();
    }

    public Node(ArrayList<Entry> entries, int indexBlockLocation, int treeLevel) {
        this.indexBlockLocation = indexBlockLocation;
        this.treeLevel = treeLevel;
        this.entries = entries;
    }

    public int getTreeLevel() {
        return treeLevel;
    }

    public boolean isLeaf() {
        return treeLevel == RStarTree.LEAF_LEVEL;
    }

    public boolean isRoot() {
        return indexBlockLocation == RStarTree.ROOT_LOCATION_IN_INDEX_FILE;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void replaceEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public void replaceEntriesFromComparator(ArrayList<EntryComparator> entries) {
        this.entries = EntriesCalculator.getEntriesFromEntryComparator(entries);
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void removeEntry(int i) {
        entries.remove(i);
    }

    public boolean removeEntry(Entry entry) {
        return entries.remove(entry);
    }

    public void setTreeLevel(int treeLevel) {
        this.treeLevel = treeLevel;
    }

    public void setIndexBlockLocation(int indexBlockLocation) {
        this.indexBlockLocation = indexBlockLocation;
    }

    public int getIndexBlockLocation() {
        return indexBlockLocation;
    }
}
