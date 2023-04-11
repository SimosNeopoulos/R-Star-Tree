import java.util.ArrayList;

public class RStarTree {
    private int totalLevelNum;
    private final int ROOT_LOCATION = 0;
    private final int LEAF_LEVEL = 1;

    public RStarTree(boolean RStarTreeExists) {
        if (!RStarTreeExists) {
            int dataBlockNum = DataHandler.getTotalBlockNum();
            for (int i = 1; i < dataBlockNum; i++) {
                DataBlock dataBlock = DataHandler.getDataBlock(i);
                int dataBlockRecordNum = dataBlock.getRecordLength();

                for (int j = 0; j < dataBlockRecordNum; j++) {
                    int recordDataFileLocation = DataHandler.calculateDataFileLocationOfRecord(i, j);
                    Record record = dataBlock.getRecord(j);
                    ArrayList<Bounds> dimensionsBounds = new ArrayList<>();

                    for (int d = 0; d < DataHandler.getCurrentDimensions(); d++) {
                        dimensionsBounds.add(new Bounds(record.getCoordinate(d), record.getCoordinate(d)));
                    }

                    LeafEntry leafEntry = new LeafEntry(new BoundingRectangle(dimensionsBounds), record.getId(), recordDataFileLocation);

                    insertData(leafEntry);
                }
            }
            return;
        }
        // TODO: ΟΤΑΝ ΥΠΑΡΧΕΙ ΗΔΗ ΚΑΤΑΛΟΓΟΣ
    }

    public void insertData(LeafEntry leafEntry) {
        insert(leafEntry, 1, null, null);
    }

    private Entry insert(Entry entryToInsert, int levelToInsert, Node parentNode, NonLeafEntry parentEntry) {
        Node childNode = null;

        if (parentNode == null && parentEntry == null) {
            childNode = getRoot();
        } else {
            parentEntry.reAdjustBoundingBox(entryToInsert);
            // TODO: Make function that updates the "parentNode" in the HashMap
            // TODO: add it here
            // (also make metaDataNode functional in DataHandler)
            childNode = DataHandler.getNodeFromIndexFile(parentEntry.getChildPTR());
        }

        if (childNode.getTreeLevel() == levelToInsert) {
            childNode.addEntry(entryToInsert);
            // TODO: and here
        } else {
            NonLeafEntry bestParentEntry = chooseLeaf(childNode, entryToInsert, levelToInsert);
            Entry newSearchEntry = insert(entryToInsert, levelToInsert, childNode, bestParentEntry);
        }

        return null;
    }

    private NonLeafEntry chooseLeaf(Node node, Entry entryToInsert, int levelToInsert) {
        ArrayList<Entry> entries = node.getEntries();
        int bestParentEntryIndex = 0;
        int minAreaDifference = Integer.MAX_VALUE;

        for (int i = 0; i < entries.size(); i++) {
            Entry currentEntry = entries.get(i);

            double areaBefore = currentEntry.getBoundingBox().getArea();
            double areaAfter = EntriesCalculator.getNewMinBoundingRectangle(currentEntry.getBoundingBox(), entryToInsert).getArea();

            if (areaAfter - areaBefore < minAreaDifference) {
                bestParentEntryIndex = i;
            }
        }
        return (NonLeafEntry) entries.get(bestParentEntryIndex);
    }

    private Node getRoot() {
        return DataHandler.getNodeFromIndexFile(ROOT_LOCATION);
    }
}
