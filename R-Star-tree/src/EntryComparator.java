import java.util.Comparator;

// Συνάρτηση που χρησιμοποιείτε όταν θέλουμε να ταξινομήσουμε entries ως προς κάποια τιμή (valueToCompare)
// TODO: Να ελενξω τον compare και να αλλαξω το ονομα του isSkyline
public class EntryComparator implements Comparator<EntryComparator>{
    private final double valueToCompare;
    private final Entry entry;

    // Αυτές οι δυο μεταβλητές
    private boolean isSkyline;
    private boolean isLeafEntry;

    public EntryComparator(double valueToCompare, Entry entry) {
        this.valueToCompare = valueToCompare;
        this.entry = entry;
        this.isSkyline = false;
    }

    public EntryComparator(double valueToCompare, Entry entry, boolean isSkyline) {
        this.valueToCompare = valueToCompare;
        this.entry = entry;
        this.isSkyline = isSkyline;
    }

    public EntryComparator(boolean isLeafEntry, double valueToCompare, Entry entry) {
        this.valueToCompare = valueToCompare;
        this.entry = entry;
        this.isLeafEntry = isLeafEntry;
    }

    public boolean isLeafEntry() {
        return isLeafEntry;
    }

    public double getValueToCompare() {
        return valueToCompare;
    }

    public Entry getEntry() {
        return entry;
    }


    @Override
    public int compare(EntryComparator o1, EntryComparator o2) {

        return isSkyline ? -1 * Double.compare(o1.getValueToCompare(), o2.getValueToCompare()) :
                Double.compare(o1.getValueToCompare(), o2.getValueToCompare());
//        if (isSkyline)
//            return Double.compare(o1.getValueToCompare(), o2.getValueToCompare());
//
//        if (o1.getValueToCompare() < o2.getValueToCompare()) {
//            return -1;
//        } else if (o1.getValueToCompare() > o2.getValueToCompare()) {
//            return 1;
//        }
//        return 0;
    }
}
