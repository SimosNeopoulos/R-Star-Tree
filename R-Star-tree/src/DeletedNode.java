import java.util.ArrayList;

// Κλάση που αναπαριστά έναν κόμβο ο οποίος έχει διαγραφεί επειδή είχε λιγότερα απο τα minimum επιτρεπόμενα entries
public class DeletedNode implements Comparable<DeletedNode> {

    // Σε πίο επίπεδο του δέντρου θα πρέπει να επανεισαχθούν τα entries του κόμβου που διαγράφτηκε στο τέλος
    // του αλγορίθμου διαγραφής
    private int levelToInsertEntries;
    private ArrayList<Entry> entries;

    public DeletedNode(int levelToInsertEntries, ArrayList<Entry> entries) {
        this.levelToInsertEntries = levelToInsertEntries;
        this.entries = entries;
    }

    public int getLevelToInsertEntries() {
        return levelToInsertEntries;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    @Override
    public int compareTo(DeletedNode other) {
        return Integer.compare(this.levelToInsertEntries, other.levelToInsertEntries);
    }
}
