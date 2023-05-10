import java.util.Comparator;

// Συνάρτηση που χρησιμοποιείτε όταν θέλουμε να ταξινομήσουμε entries ως προς κάποια τιμή (valueToCompare)
public class EntryComparator implements Comparator<EntryComparator> {
    private double valueToCompare;
    private Entry entry;

    public EntryComparator(double valueToCompare, Entry entry) {
        this.valueToCompare = valueToCompare;
        this.entry = entry;
    }

    public double getValueToCompare() {
        return valueToCompare;
    }

    public Entry getEntry() {
        return entry;
    }


    @Override
    public int compare(EntryComparator o1, EntryComparator o2) {
        if (o1.getValueToCompare() < o2.getValueToCompare()) {
            return -1;
        } else if (o1.getValueToCompare() > o2.getValueToCompare()) {
            return 1;
        }
        return 0;
    }
}
