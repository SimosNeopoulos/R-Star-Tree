import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;

// Κλάση που αποθηκεύει integers σε ένα HashSet.
// Χρησιμοποιείτε, κυρίως, για να επιστρέψει το μικρότερο στοιχείο του set
public class PriorityDeQueue implements Serializable {
    private HashSet<Integer> set;

    public PriorityDeQueue() {
        this.set = new HashSet<>();
    }

    public PriorityDeQueue(PriorityDeQueue oldSet) {
        this.set = oldSet.getSet();
    }

    public HashSet<Integer> getSet() {
        return this.set;
    }

    public void add(int x) {
        set.add(x);
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public int getSize() {
        return this.set.size();
    }

    public int getMinEntry() {
        if(set.isEmpty())
            return 0;
        int min = Collections.min(set, null);
        set.remove(min);
        return min;
    }
}
