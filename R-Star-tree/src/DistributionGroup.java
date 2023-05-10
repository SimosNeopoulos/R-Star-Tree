import java.util.ArrayList;

// Συνάρτηση που χρησιμοποιείται για να την αποθήκευση ενός συνόλου απο entries
// και του Bounding Rectangle που τις περικλείει
// Χρησιμοποιείται στον αλγόριθμο chooseSplitAxis και chooseSplitIndex
public class DistributionGroup {

    private ArrayList<Entry> entries;
    private BoundingRectangle boundingRectangle;

    public DistributionGroup(ArrayList<Entry> entries, BoundingRectangle boundingRectangle) {
        this.entries = entries;
        this.boundingRectangle = boundingRectangle;
    }

    public BoundingRectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }
}
