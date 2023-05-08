import java.util.Comparator;

//Used for reInsert and chooseAxis
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
