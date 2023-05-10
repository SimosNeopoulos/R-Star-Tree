import java.io.Serializable;

// Κλάση που αναπαριστά το ένα meta data block των 32KB που περιέχει πληροφορίες σχετικά με το datafile
public class MetaDataBlock implements Serializable {
    private int id;
    private int totalBlockNum;
    private int totalSlotsNum;
    private final int currentDimensions;
    private PriorityDeQueue freeSlots;
    private final PriorityDeQueue alteredBlocks;
    private int maxRecordsPerBlock;

    public MetaDataBlock(int dimensionsNum) {
        this.id = 0;
        this.totalBlockNum = 0;
        this.totalSlotsNum = 0;
        this.currentDimensions = dimensionsNum;
        this.maxRecordsPerBlock = DataHandler.calculateMaxRecordsPerBlock(dimensionsNum);
        this.freeSlots = new PriorityDeQueue();
        this.alteredBlocks = new PriorityDeQueue();
    }

    public void addAlteredBlock(int i) {
        alteredBlocks.add(i);
    }

    public int getMaxRecordsPerBlock() {
        return maxRecordsPerBlock;
    }

    public void setMaxRecordsPerBlock(int maxRecordsPerBlock) {
        this.maxRecordsPerBlock = maxRecordsPerBlock;
    }

    public boolean alteredBlocksExist() {
        return this.alteredBlocks.isEmpty();
    }

    public int getMinAlteredBlock() {
        return alteredBlocks.getMinEntry();
    }

    public boolean freeSlotExist() {
        return !this.freeSlots.isEmpty();
    }

    public int getAlteredBlocksNum() {
        return alteredBlocks.getSet().size();
    }

    public int getFreeSlotsNum() {
        return freeSlots.getSet().size();
    }

    public int getFreeSlot() {
        return freeSlots.getMinEntry();
    }
    public void addEmptySlot(int i) {
        freeSlots.add(i);
    }

    public int getTotalBlockNum() {
        return totalBlockNum;
    }

    public void increaseTotalBlockNum() {
        this.totalBlockNum++;
    }

    public int getTotalSlotsNum() {
        return totalSlotsNum;
    }

    public void increaseTotalSlotsNum() {
        this.totalSlotsNum++;
    }

    public int getValidSlotNum() {
        return totalSlotsNum - freeSlots.getSize();
    }

    public int getCurrentDimensions() {
        return currentDimensions;
    }

    public PriorityDeQueue getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(PriorityDeQueue freeSlots) {
        this.freeSlots = freeSlots;
    }
}
