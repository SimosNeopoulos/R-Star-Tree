import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
    private int indexBlockLocation;
    private int treeLevel;
    private ArrayList<Entry> entries;

    public Node(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public int getTreeLevel() {
        return treeLevel;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
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
