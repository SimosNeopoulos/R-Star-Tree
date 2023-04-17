import java.util.ArrayList;

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
