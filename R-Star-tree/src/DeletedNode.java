import java.util.ArrayList;
import java.util.Comparator;

public class DeletedNode implements Comparator<DeletedNode> {

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
    public int compare(DeletedNode o1, DeletedNode o2) {
        return Integer.compare(o1.getLevelToInsertEntries(), o2.getLevelToInsertEntries());
    }
}
