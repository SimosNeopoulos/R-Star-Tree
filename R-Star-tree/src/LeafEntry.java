import java.io.Serializable;
import java.util.ArrayList;

public class LeafEntry implements Serializable, Entry {
    private BoundingRectangle boundingRectangle;
    private long objectId;
    private int dataFileLocation;

    public LeafEntry(BoundingRectangle boundingRectangle, long objectId, int dataFileLocation) {
        this.boundingRectangle = boundingRectangle;
        this.objectId = objectId;
        this.dataFileLocation = dataFileLocation;
    }

    public BoundingRectangle getBoundingBox() {
        return boundingRectangle;
    }

    public long getObjectId() {
        return objectId;
    }

    public int getDataFileLocation() {
        return dataFileLocation;
    }
}
