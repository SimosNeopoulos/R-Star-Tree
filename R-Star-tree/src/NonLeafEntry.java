import java.io.Serializable;
import java.util.ArrayList;

public class NonLeafEntry implements Serializable, Entry  {
    private BoundingRectangle boundingRectangle;
    private int childPTR;

    public NonLeafEntry(ArrayList<Entry> entries, int childPTR) {
        this.boundingRectangle = EntriesCalculator.getMinimumBoundingRectangleForEntries(entries);
        this.childPTR = childPTR;
    }

    public NonLeafEntry(BoundingRectangle boundingRectangle, int childPTR) {
        this.boundingRectangle = boundingRectangle;
        this.childPTR = childPTR;
    }

    public BoundingRectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public int getChildPTR() {
        return childPTR;
    }

    public void reAdjustBoundingRectangle(ArrayList<Entry> entries) {
        boundingRectangle = EntriesCalculator.getMinimumBoundingRectangleForEntries(entries);
    }

    public void reAdjustBoundingRectangle(Entry entry) {
        this.boundingRectangle = EntriesCalculator.getNewMinBoundingRectangle(this.boundingRectangle, entry);
    }
}