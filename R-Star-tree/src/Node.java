import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable {
    private int indexBlockLocation;
    private int treeLevel;
    ArrayList<Entry> entries;

    public Node(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public int getTreeLevel() {
        return treeLevel;
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
