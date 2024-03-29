import java.io.Serializable;
import java.util.ArrayList;

// Κλάση που αναπαριστά τα entries του δέντρου τα οποία δεν βρίσκονται στα φύλλα
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

    @Override
    public boolean isDominatedByEntry(Entry entry) {
        for (int i = 0; i < DataHandler.getCurrentDimensions(); i++) {
            double currentLowerBound = entry.getBoundingRectangle().getBoundsOfDimension(i).getLowerBound();
            if (currentLowerBound < boundingRectangle.getBoundsOfDimension(i).getLowerBound())
                return true;
        }
        return false;
    }

    public void reAdjustBoundingRectangle() {
        this.boundingRectangle = EntriesCalculator.getMinimumBoundingRectangleForEntries(DataHandler.getNodeFromIndexFile(childPTR).getEntries());
    }

    public void reAdjustBoundingRectangle(ArrayList<Entry> entries) {
        this.boundingRectangle = EntriesCalculator.getMinimumBoundingRectangleForEntries(entries);
    }

    public void reAdjustBoundingRectangle(Entry entry) {
        this.boundingRectangle = EntriesCalculator.getNewMinBoundingRectangle(this.boundingRectangle, entry);
    }
}
