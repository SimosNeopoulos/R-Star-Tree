import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RStarTree {
    private HashSet<Integer> levelsVisited;
    public static final int ROOT_LOCATION_IN_INDEX_FILE = 1;
    public static final int LEAF_LEVEL = 1;
    public int numOfInsertsTest;

    public RStarTree() {
        numOfInsertsTest = 0;
        if (!DataHandler.initialiseIndexFile()) {
            int dataBlockNum = DataHandler.getTotalBlockNum();
            for (int i = 1; i < dataBlockNum; i++) {

                DataBlock dataBlock = DataHandler.getDataBlock(i);
                int dataBlockRecordNum = dataBlock.getRecordSize();

                for (int j = 0; j < dataBlockRecordNum; j++) {
                    int recordDataFileLocation = DataHandler.calculateDataFileLocationOfRecord(i, j);
                    Record record = dataBlock.getRecord(j);
                    ArrayList<Bounds> dimensionsBounds = new ArrayList<>();

                    for (int d = 0; d < DataHandler.getCurrentDimensions(); d++) {
                        dimensionsBounds.add(new Bounds(record.getCoordinate(d), record.getCoordinate(d)));
                    }

                    LeafEntry leafEntry = new LeafEntry(new BoundingRectangle(dimensionsBounds), record.getId(), recordDataFileLocation);

                    insertData(leafEntry);
                    numOfInsertsTest++;
                }
            }
            System.out.println("Num of inserts: " + numOfInsertsTest);
            DataHandler.writeAlteredNodesToIndexFile();
        }
        // TODO: ΟΤΑΝ ΥΠΑΡΧΕΙ ΗΔΗ ΚΑΤΑΛΟΓΟΣ
    }

    public int getTotalLevelNum() {
        return DataHandler.getTotalTreeLevelNum();
    }

    public void insertData(LeafEntry leafEntry) {
        levelsVisited = new HashSet<>();
        insert(leafEntry, LEAF_LEVEL, null, null);
    }

    private Entry delete(Entry entryToDelete, Node parentNode, NonLeafEntry parentEntry) {
        Node childNode;

        if(parentEntry == null) {
            childNode = getRoot();
        } else {
            childNode = DataHandler.getNodeFromIndexFile(parentEntry.getChildPTR());
        }




        return null;
    }

    private Entry insert(Entry entryToInsert, int levelToInsert, Node parentNode, NonLeafEntry parentEntry) {
        Node childNode;

        if (parentEntry == null) {
            childNode = getRoot();
        } else {
            parentEntry.reAdjustBoundingRectangle(entryToInsert);
            DataHandler.updateNode(parentNode);
            childNode = DataHandler.getNodeFromIndexFile(parentEntry.getChildPTR());
        }

        if (childNode.getTreeLevel() == levelToInsert) {
            childNode.addEntry(entryToInsert);
            DataHandler.updateNode(childNode);
        } else {
            NonLeafEntry bestParentEntry = chooseSubTree(childNode, entryToInsert);
            Entry newSearchEntry = insert(entryToInsert, levelToInsert, childNode, bestParentEntry);

            if (newSearchEntry != null) {
                childNode.addEntry(newSearchEntry);
                DataHandler.updateNode(childNode);
            } else {
                DataHandler.updateNode(childNode);
                return null;
            }
        }

        if (childNode.getEntries().size() > DataHandler.getMaxEntriesPerNode()) {
            return overFlowTreatment(parentNode, childNode, parentEntry);
        }

        return null;
    }

    private NonLeafEntry chooseSubTree(Node node, Entry entryToInsert) {
        ArrayList<Entry> entries = node.getEntries();
        int bestParentEntryIndex = 0;
        int minAreaDifference = Integer.MAX_VALUE;

        for (int i = 0; i < entries.size(); i++) {
            Entry currentEntry = entries.get(i);

            double areaBefore = currentEntry.getBoundingRectangle().getArea();
            double areaAfter = EntriesCalculator.getNewMinBoundingRectangle(currentEntry.getBoundingRectangle(), entryToInsert).getArea();

            if (areaAfter - areaBefore < minAreaDifference) {
                bestParentEntryIndex = i;
            }
        }
        return (NonLeafEntry) entries.get(bestParentEntryIndex);
    }

    private Entry overFlowTreatment(Node parentNode, Node childNode, NonLeafEntry parentEntry) {
        if (childNode.getIndexBlockLocation() != ROOT_LOCATION_IN_INDEX_FILE && !this.levelsVisited.contains(childNode.getTreeLevel())) {

            this.levelsVisited.add(childNode.getTreeLevel());
            reInsert(parentNode, childNode, parentEntry);
            return null;
        }

        ArrayList<Node> splittedNodes = EntriesCalculator.chooseSplitIndex(EntriesCalculator.chooseSplitAxis(childNode), childNode.getTreeLevel());
        childNode.replaceEntries(splittedNodes.get(0).getEntries());
        Node newNode = splittedNodes.get(1);

        if (childNode.getIndexBlockLocation() != ROOT_LOCATION_IN_INDEX_FILE) {
            DataHandler.updateNode(childNode);
            int newNodeLocation = DataHandler.addNewNode(newNode, false);

            parentEntry.reAdjustBoundingRectangle(childNode.getEntries());
            DataHandler.updateNode(parentNode);

            return new NonLeafEntry(newNode.getEntries(), newNodeLocation);
        }

        int childNodeLocation = DataHandler.addNewNode(childNode, false);
        int newNodeLocation = DataHandler.addNewNode(newNode, false);

        DataHandler.increaseTotalLevelNum();

        Node newRootNode = new Node(ROOT_LOCATION_IN_INDEX_FILE, DataHandler.getTotalTreeLevelNum());
        newRootNode.addEntry(new NonLeafEntry(childNode.getEntries(), childNodeLocation));
        newRootNode.addEntry(new NonLeafEntry(newNode.getEntries(), newNodeLocation));
        DataHandler.addNewNode(newRootNode, true);

        return null;

    }

    private void reInsert(Node parentNode, Node childNode, NonLeafEntry parentEntry) {
        ArrayList<Entry> entries = childNode.getEntries();
        ArrayList<EntryComparator> toBeSorted = new ArrayList<>();
        int p = DataHandler.getPReInsertNum();

        for (Entry entry : entries) {
            toBeSorted.add(new EntryComparator(EntriesCalculator.calculateCenterDistanceOfEntries(entry, parentEntry), entry));
        }

        toBeSorted.sort(new EntryComparator(0.0, null));

        childNode.replaceEntriesFromComparator(new ArrayList<>(toBeSorted.subList(0, p)));

        parentEntry.reAdjustBoundingRectangle(childNode.getEntries());
        DataHandler.updateNode(parentNode);
        DataHandler.updateNode(childNode);

        List<EntryComparator> deletedEntries = toBeSorted.subList(p, toBeSorted.size());

        for (EntryComparator reEntry : deletedEntries) {
            insert(reEntry.getEntry(), 1, null, null);
        }
    }

    public Node getRoot() {
        return DataHandler.getNodeFromIndexFile(ROOT_LOCATION_IN_INDEX_FILE);
    }
}
