import java.util.ArrayList;

public class DeletedNode implements Comparable<DeletedNode> {

    private int levelToInsertEntries;
    private ArrayList<Entry> entries;

    public DeletedNode(int levelToInsertEntries, ArrayList<Entry> entries) {
        this.levelToInsertEntries = levelToInsertEntries;
        this.entries = entries;
    }

    public int getLevelToInsertEntries() {
        return levelToInsertEntries;
    }

    public void setLevelToInsertEntries(int levelToInsertEntries) {
        this.levelToInsertEntries = levelToInsertEntries;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    @Override
    public int compareTo(DeletedNode other) {
        return Integer.compare(this.levelToInsertEntries, other.levelToInsertEntries);
    }
}
