import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class RStarTree {
    // Τα επίπεδα του δέντρου στα οποία έχει εφαρμοστεί ηδη η reInsert κατά την είσοδο ενός νέου entry (insertData)
    private HashSet<Integer> levelsVisited;

    // Nodes τα οποία έχουν διαγραφεί επειδή είχαν λιγότερα απο τα minimum επιτρεπόμενα entries μετα την διαγραφη ενος entry (deleteData)
    // και τα entries των οποίων πρέπει να εισαχθούν ξανά μετά την διαγραφή όλων των απαρέτητων entries.
    private PriorityQueue<DeletedNode> deletedNodes;
    public static final int ROOT_LOCATION_IN_INDEX_FILE = 1;
    public static final int LEAF_LEVEL = 1;

    public RStarTree() {
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
                }
            }
            DataHandler.writeAlteredNodesToIndexFile();
        }
    }

    public int getTotalLevelNum() {
        return DataHandler.getTotalTreeLevelNum();
    }

    public void insertData(LeafEntry leafEntry) {
        levelsVisited = new HashSet<>();
        insert(leafEntry, LEAF_LEVEL, null, null);
    }

    public void deleteData(Entry entryToDelete) {
        deletedNodes = new PriorityQueue<>();
        delete(entryToDelete, null);
        reInsertDeletedEntries();
    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο της διαγραφής στο RStarTree
    private boolean delete(Entry entryToDelete, NonLeafEntry parentEntry) {
        Node childNode;

        if (parentEntry == null) {
            childNode = getRoot();
        } else {
            childNode = DataHandler.getNodeFromIndexFile(parentEntry.getChildPTR());
        }

        if (childNode.isLeaf()) {
            // Εντοπίζουμε σε ποία θέση στο ArrayList βρίσκεται το entry
            int entryToDeleteLocation = RStarTree.getIndexForEntryToDelete(entryToDelete, childNode);
            if (entryToDeleteLocation == -1)
                throw new IllegalStateException("Entry for deletion not found");
            LeafEntry leafEntry = (LeafEntry) childNode.getEntries().get(entryToDeleteLocation);

            // Διαγραφή του record απο το datafile
            DataHandler.deleteSlot(leafEntry.getDataFileLocation());

            // Διαγραφή του entry απο το index file
            childNode.removeEntry(entryToDeleteLocation);
        } else {
            NonLeafEntry newParentEntry = findLeaf(entryToDelete, childNode);

            // Καλούμε ανδρομικά την delete.
            // Επιστρέφει true αν το node στο οποίο δείχνει το newParentEntry υπέστη διαγραφη
            // και false σε κάθε άλλη περίπτωση
            boolean deleteNewParentEntry = delete(entryToDelete, newParentEntry);

            if (deleteNewParentEntry) {
                if (!childNode.removeEntry(newParentEntry))
                    throw new IllegalStateException("Entry for deletion not found");
            }
        }

        if (childNode.getEntries().size() <= DataHandler.getMinEntriesPerNode()) {
            return condenseTree(childNode);
        } else if (parentEntry != null) {
            parentEntry.reAdjustBoundingRectangle();
        }

        return false;
    }


    //TODO: Να δω αν χρειάζεται να κάνω delete τα nodes απο το indexfile ή απλά να τα αφήνω εκεί

    // Συνάρτηση που υλοποιεί τον αλγόριθμο condenseTree.
    private boolean condenseTree(Node childNode) {

        if (childNode.isRoot()) {
            if (childNode.getEntries().size() == 1 && !childNode.isLeaf()) {
                NonLeafEntry lastRootEntry = (NonLeafEntry) childNode.getEntries().get(0);
                Node newRoot = DataHandler.getNodeFromIndexFile(lastRootEntry.getChildPTR());
                newRoot.setIndexBlockLocation(ROOT_LOCATION_IN_INDEX_FILE);
                DataHandler.addEmptyIndexNode(lastRootEntry.getChildPTR());
                DataHandler.updateNode(newRoot);
                //TODO: Να δω τι θα κάνω με τις κενές θέσεις στο index file

                DataHandler.decreaseTotalLevelNum();
                DataHandler.decreaseTotalNodeNum();
            }
            return false;
        }

        deletedNodes.add(new DeletedNode(childNode.getTreeLevel(), childNode.getEntries()));
        DataHandler.decreaseTotalNodeNum();
        DataHandler.addEmptyIndexNode(childNode.getIndexBlockLocation());

        return true;
    }

    private void reInsertDeletedEntries() {
        while (!deletedNodes.isEmpty()) {
            DeletedNode deletedNode = deletedNodes.poll();
            for (Entry entry : deletedNode.getEntries()) {
                insert(entry, deletedNode.getLevelToInsertEntries(), null, null);
            }
        }
    }

    private NonLeafEntry findLeaf(Entry entryToDelete, Node childNode) {
        for (Entry entry : childNode.getEntries()) {
            if (EntriesCalculator.checkOverlap(entryToDelete.getBoundingRectangle(), entry.getBoundingRectangle())) {
                return (NonLeafEntry) entry;
            }
        }
        return null;
    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο insert
    private NonLeafEntry insert(Entry entryToInsert, int levelToInsert, Node parentNode, NonLeafEntry parentEntry) {
        Node childNode;

        if (parentEntry == null) {
            childNode = getRoot();
        } else {
            childNode = DataHandler.getNodeFromIndexFile(parentEntry.getChildPTR());
            parentEntry.reAdjustBoundingRectangle(entryToInsert);
            DataHandler.updateNode(parentNode);
        }

        if (childNode.getTreeLevel() == levelToInsert) {
            childNode.addEntry(entryToInsert);
        } else {
            NonLeafEntry bestParentEntry = chooseSubTree(childNode, entryToInsert);
            // Καλούμε αναδρομικά τη συνάρτηση insert.
            // Αν επιστρέψει null σημαίνει πως δεν υπήρχε node split στο node που δείχνει το bestParentEntry
            // Αν επιστραφεί NonLeafEntry υπήρξε node split κατα την εισαγωγή του entry.
            Entry newSearchEntry = insert(entryToInsert, levelToInsert, childNode, bestParentEntry);

            // Αν υπήρχε εισαγωγή προσθέτουμε το νέο entry στο childNode
            if (newSearchEntry != null) {
                childNode.addEntry(newSearchEntry);
            }
        }
        DataHandler.updateNode(childNode);

        // Ελέγχουμε αν ο αριθμός των entries στο node ξεπερνάει το μέγιστο επιτρεπτό
        if (childNode.getEntries().size() > DataHandler.getMaxEntriesPerNode()) {
            return overFlowTreatment(parentNode, childNode, parentEntry);
        }

        return null;
    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο chooseSubTree
    private NonLeafEntry chooseSubTree(Node node, Entry entryToInsert) {
        ArrayList<Entry> entries = node.getEntries();
        int bestParentEntryIndex = 0;
        int minAreaDifference = Integer.MAX_VALUE;

        for (int i = 0; i < entries.size(); i++) {
            Entry currentEntry = entries.get(i);

            // Υπολογισμός του area του node πριν και μετά την είσοδο του νέου entry
            double areaBefore = currentEntry.getBoundingRectangle().getArea();
            double areaAfter = EntriesCalculator.getNewMinBoundingRectangle(currentEntry.getBoundingRectangle(), entryToInsert).getArea();

            // Ελέγχουμε αν η διαφορά του area πριν και μετα την εισαγωγή είναι η καλύτερη μέχρι τώρα.
            if (areaAfter - areaBefore < minAreaDifference) {
                bestParentEntryIndex = i;
            }
        }
        return (NonLeafEntry) entries.get(bestParentEntryIndex);
    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο Overflow Treatment
    private NonLeafEntry overFlowTreatment(Node parentNode, Node childNode, NonLeafEntry parentEntry) {
        // Αν δεν έχει καλεστεί άλλη φορα η overFlowTreatment σε αυτή την εισαγωγή καλείτε η reInsert.
        if (childNode.getIndexBlockLocation() != ROOT_LOCATION_IN_INDEX_FILE && !this.levelsVisited.contains(childNode.getTreeLevel())) {

            this.levelsVisited.add(childNode.getTreeLevel());
            reInsert(parentNode, childNode, parentEntry);
            return null;
        }

        // Εφαρμόζονται οι αλγόριθμοι chooseSplitAxis και chooseSplitIndex για τη διάσπαση του childNode
        ArrayList<Node> splittedNodes = EntriesCalculator.chooseSplitIndex(EntriesCalculator.chooseSplitAxis(childNode), childNode.getTreeLevel());
        childNode.replaceEntries(splittedNodes.get(0).getEntries());
        Node newNode = splittedNodes.get(1);

        // Αν η διάσπαση δεν έγινε στη ρίζα
        if (childNode.getIndexBlockLocation() != ROOT_LOCATION_IN_INDEX_FILE) {
            DataHandler.updateNode(childNode);
            int newNodeLocation = DataHandler.addNewNode(newNode, false);

            parentEntry.reAdjustBoundingRectangle(childNode.getEntries());
            DataHandler.updateNode(parentNode);

            return new NonLeafEntry(newNode.getEntries(), newNodeLocation);
        }

        // Αν η διάσπαση έγινε στη ρίζα δημιουργούμε μια νέα ρίζα και περνάμε ως entries τα δυο
        // NonLeafEntries που δείχνουν στους νέους κόμβους που προέκυψαν απο τη διάσπαση της παλιάς ρίζας

        int childNodeLocation = DataHandler.addNewNode(childNode, false);
        int newNodeLocation = DataHandler.addNewNode(newNode, false);

        DataHandler.increaseTotalLevelNum();

        Node newRootNode = new Node(ROOT_LOCATION_IN_INDEX_FILE, DataHandler.getTotalTreeLevelNum());
        newRootNode.addEntry(new NonLeafEntry(childNode.getEntries(), childNodeLocation));
        newRootNode.addEntry(new NonLeafEntry(newNode.getEntries(), newNodeLocation));
        DataHandler.addNewNode(newRootNode, true);

        return null;

    }

    // Συνάρτηση που υλοποιεί τον αλγόριθμο του re-insert.
    private void reInsert(Node parentNode, Node childNode, NonLeafEntry parentEntry) {
        ArrayList<Entry> entries = childNode.getEntries();
        ArrayList<EntryComparator> toBeSorted = new ArrayList<>();
        int p = DataHandler.getPReInsertNum();

        for (Entry entry : entries) {
            // Συγκρίνουμε όλα τα entries ως προς την απόσταση του κέντρου τους απο το κέντρο του parentEntry
            toBeSorted.add(new EntryComparator(EntriesCalculator.calculateCenterDistanceOfEntries(entry, parentEntry), entry));
        }

        // Ταξινομούμε τα entries ανάλογα
        toBeSorted.sort(new EntryComparator(0.0, null));

        // Κρατάμε τις πρώτες p entries
        childNode.replaceEntriesFromComparator(new ArrayList<>(toBeSorted.subList(0, p)));

        parentEntry.reAdjustBoundingRectangle(childNode.getEntries());
        DataHandler.updateNode(parentNode);
        DataHandler.updateNode(childNode);

        List<EntryComparator> deletedEntries = toBeSorted.subList(p, toBeSorted.size());

        // Πραγματοποιούμε insert εκ νέου στις υπόλοιπες entries
        for (EntryComparator reEntry : deletedEntries) {
            insert(reEntry.getEntry(), 1, null, null);
        }
    }

    public Node getRoot() {
        return DataHandler.getNodeFromIndexFile(ROOT_LOCATION_IN_INDEX_FILE);
    }

    public ArrayList<LeafEntry> rangeQueryAlgorithm(BoundingRectangle boundingRectangle) {
        RangeQuery rangeQuery = new RangeQuery(getRoot(), boundingRectangle);
        return rangeQuery.rangeQuerySearch();
    }

    public ArrayList<LeafEntry> kNNQuery(int k, BoundingRectangle point) {
     KNNQuery knnQuery = new KNNQuery(k, point);
     return knnQuery.getKNNs(getRoot());
    }

    private static int getIndexForEntryToDelete(Entry entry, Node node) {
        for (int i = 0; i < node.getEntries().size(); i++) {
            if (node.getEntries().get(i).equals(entry))
                return i;
        }

        return -1;
    }
}
