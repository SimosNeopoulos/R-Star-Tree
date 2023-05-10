import java.io.Serializable;

// Κλάση που αναπαριστά τα entries στο επίπεδο των φύλλων
public class LeafEntry implements Serializable, Entry {
    private BoundingRectangle boundingRectangle;
    private long objectId;
    private int dataFileLocation;

    public LeafEntry(BoundingRectangle boundingRectangle, long objectId, int dataFileLocation) {
        this.boundingRectangle = boundingRectangle;
        this.objectId = objectId;
        this.dataFileLocation = dataFileLocation;
    }

    public BoundingRectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public long getObjectId() {
        return objectId;
    }

    public int getDataFileLocation() {
        return dataFileLocation;
    }
}
