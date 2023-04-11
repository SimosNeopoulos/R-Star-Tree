import java.io.Serializable;
import java.util.ArrayList;

public class NonLeafEntry implements Serializable, Entry  {
    private BoundingRectangle boundingRectangle;
    private long childPTR;

    public NonLeafEntry(ArrayList<Entry> entries, long childPTR) {
        this.boundingRectangle = EntriesCalculator.getMinimumBoundingBoxForEntries(entries);
        this.childPTR = childPTR;
    }

    public NonLeafEntry(BoundingRectangle boundingRectangle, long childPTR) {
        this.boundingRectangle = boundingRectangle;
        this.childPTR = childPTR;
    }

    public BoundingRectangle getBoundingBox() {
        return boundingRectangle;
    }

    public long getChildPTR() {
        return childPTR;
    }

    public void reAdjustBoundingBox(BoundingRectangle boundingRectangle) {
        this.boundingRectangle = boundingRectangle;
    }
}
