import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataHandler {
    private static final String PATH_TO_DATAFILE = "data_file.dat";
    private static final String PATH_TO_INDEX_FILE = "index_file.dat";
    private static final String PATH_TO_CSV = "nodes.csv";
    private static final int BLOCK_SIZE = 32 * 1024;
    private static MetaDataBlock metaDataBlock;
    private static final HashMap<Integer, DataBlock> dataBlocksInMemory = new HashMap<>();
    private static final HashMap<Integer, Node> nodesInMemory = new HashMap<>();
    private static MetaDataNode metaDataNode;

    private static boolean isNodeInMemory(int nodeIndex) {
        return nodesInMemory.containsKey(nodeIndex);
    }

    public static int getPReInsertNum() {
        return metaDataNode.getReInsertPEntries();
    }

    public static int getTotalTreeLevelNum() {
        return metaDataNode.getTotalLevelNum();
    }

    // Works
    public static void addAlteredNode(int i) {
        metaDataNode.addAlteredNode(i);
    }

    public static int getAlteredNodesNum() {
        return metaDataNode.getAlteredNodesNum();
    }

    // Works (probably)
    public static boolean initialiseIndexFile() {
        if (new File(PATH_TO_INDEX_FILE).exists()) {
            metaDataNode = readMetaDataNode();
            return true;
        }
        createIndexFile();
        return false;
    }

    // Works (probably)
    private static void createIndexFile() {
        metaDataNode = new MetaDataNode();
        createRoot();
        increaseTotalNodeNum();
        increaseIndexFileLocationForNewNode();
        calculateMaxEntriesPerNode();
        writeAlteredNodesToIndexFile();
    }

    // Works
    public static void updateNode(Node node) {
        nodesInMemory.put(node.getIndexBlockLocation(), node);
        addAlteredNode(node.getIndexBlockLocation());
    }

    // Works
    public static void increaseTotalLevelNum() {
        metaDataNode.increaseTotalLevelNum();
    }

    // Works
    public static void decreaseTotalLevelNum() {
        metaDataNode.decreaseTotalLevelNum();
    }

    // Works
    public static void increaseTotalNodeNum() {
        metaDataNode.increaseTotalNodesNum();
    }

    public static void decreaseTotalNodeNum() {
        metaDataNode.decreaseTotalNodeNum();
    }

    public static int getIndexFileLocationForNewNode() {
        return metaDataNode.getIndexFileLocationForNewNode();
    }

    public static void increaseIndexFileLocationForNewNode() {
        metaDataNode.increaseIndexFileLocationForNewNode();
    }

    public static void addEmptyIndexNode(int locationInIndexFile) {
        metaDataNode.addEmptyIndexNode(locationInIndexFile);
    }

    public static boolean emptyIndexNodeExists() {
        return metaDataNode.emptyIndexNodeExists();
    }

    public static int getEmptyIndexLocation() {
        return metaDataNode.getEmptyIndexLocation();
    }

    // Works
    public static int addNewNode(Node node, boolean isRoot) {
        int nodeLocation;
        if (isRoot) {
            nodeLocation = RStarTree.ROOT_LOCATION_IN_INDEX_FILE;
        } else {
            increaseTotalNodeNum();
            if (DataHandler.emptyIndexNodeExists()) {
                nodeLocation = getEmptyIndexLocation();
            } else {
                increaseIndexFileLocationForNewNode();
                nodeLocation = getIndexFileLocationForNewNode();
            }
        }
        node.setIndexBlockLocation(nodeLocation);
        updateNode(node);
        return nodeLocation;
    }

    // Works
    private static void createRoot() {
        Node root = new Node(RStarTree.ROOT_LOCATION_IN_INDEX_FILE, RStarTree.LEAF_LEVEL);
        addNewNode(root, true);
    }

    // Works
    public static void initialiseDataFile() {
        if (new File(PATH_TO_DATAFILE).exists()) {
            metaDataBlock = readMetaDataBlock();
            return;
        }
        createDataFile();
        readRecordsFromCSV();
        writeAlteredBlocksToDataFile();
    }

    // Works
    private static void createDataFile() {
        metaDataBlock = new MetaDataBlock(askForDimensions());
        createNewDataBlock();
    }

    // Works
    private static int askForDimensions() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of dimensions: ");
        return scanner.nextInt();
    }

    // Works
    private static int getNewDataBlockId() {
        metaDataBlock.increaseTotalBlockNum();
        int id = metaDataBlock.getTotalBlockNum();
        return id;
    }

    // Works
    public static int findBlockOfRecord(int index) {
        return index / 1000;
    }

    // Works
    public static int findSlotOfRecord(int index) {
        return (index % 1000);
    }

    // Works
    public static int calculateDataFileLocationOfRecord(int block, int slot) {
        return block * 1000 + slot; // ΠΡΟΣΟΧΗ: Το πρώτο slot ενός block έχει τιμή "0" οχι 1""
    }

    // Works
    private static boolean blockInMemory(int blockNum) {
        return dataBlocksInMemory.get(blockNum) != null;
    }

    public static void deleteSlot(int slotLocation) {
        int blockId = findBlockOfRecord(slotLocation);
        int slotNum = findSlotOfRecord(slotLocation);

        if (!blockInMemory(blockId)) {
            loadDataBlock(blockId);
        }

        dataBlocksInMemory.get(blockId).deleteRecord(slotNum);
        metaDataBlock.addAlteredBlock(blockId);
        metaDataBlock.addEmptySlot(slotLocation);
    }

    // Works
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    // Works
    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public static int getTotalRecords() {
        return metaDataBlock.getTotalSlotsNum();
    }

    public static int getMaxEntriesPerBlock() {
        return metaDataBlock.getMaxRecordsPerBlock();
    }

    // Works
    public static int calculateMaxRecordsPerBlock(int dimensions) {

        ArrayList<Record> records = new ArrayList<>();
        int maxNumOfRecordsPerBlock = 0;


        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            ArrayList<Double> coordinates = new ArrayList<>();
            for (int j = 0; j < dimensions; j++) {
                coordinates.add(0.0);
            }

            Record record = new Record(i, coordinates);

            records.add(record);
            DataBlock dataBlock = new DataBlock(1, records);
            byte[] dataBlockInBytes = new byte[0];
            try {
                dataBlockInBytes = serialize(dataBlock);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dataBlockInBytes.length > BLOCK_SIZE)
                break;

            maxNumOfRecordsPerBlock++;
        }
        return maxNumOfRecordsPerBlock;
    }

    // Works (probably)
    private static void calculateMaxEntriesPerNode() {
        ArrayList<Entry> entries = new ArrayList<>();
        int maxNumOfEntriesPerNode = 0;

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            ArrayList<Bounds> bounds = new ArrayList<>();
            for (int j = 0; j < getCurrentDimensions(); j++) {
                bounds.add(new Bounds(0.0, 0.0));
            }

            Entry entry = new LeafEntry(new BoundingRectangle(bounds), 1, 1111);
            entries.add(entry);
            Node node = new Node(entries, 1, 1);

            byte[] nodeInBytes = new byte[0];
            try {
                nodeInBytes = serialize(node);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (nodeInBytes.length > BLOCK_SIZE)
                break;

            maxNumOfEntriesPerNode++;

        }

        metaDataNode.setNodeMaxEntriesNum(maxNumOfEntriesPerNode);
    }

    // Works
    public static void readRecordsFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_CSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 1) {
                    continue;
                }

                insertToDataFile(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Works
    private static void insertToDataFile(String line) {
        int slotLocationForInsertion = findSlotToInsert();
        readyDataBlockForInsertion(slotLocationForInsertion);
        insertRecord(new Record(line.split(",")), slotLocationForInsertion);
    }

    // Works
    private static void readyDataBlockForInsertion(int slotLocationForInsertion) {
        int blockForInsertion;
        if (slotLocationForInsertion < 1000) {
            blockForInsertion = slotLocationForInsertion;
        } else {
            blockForInsertion = findBlockOfRecord(slotLocationForInsertion);
        }

        if (blockForInsertion > metaDataBlock.getTotalBlockNum()) {
            createNewDataBlock();
        } else if (!dataBlocksInMemory.containsKey(blockForInsertion)) {
            loadDataBlock(blockForInsertion);
        }

    }

    // Αν το record είναι να προστεθεί σε στο τέλος των records ενός block επιστρέφει int < 1000
    // Αν το record θα αποθηκευτεί σε θέση που έχει διαγραφεί επιστρέφει int >= 1000
    private static int findSlotToInsert() {
        if (metaDataBlock.freeSlotExist()) {
            return metaDataBlock.getFreeSlot();
        }

        int totalBlocksNum = metaDataBlock.getTotalBlockNum();
        int totalSlotsNum = metaDataBlock.getTotalSlotsNum();
        int maxSlotsPerBlockNum = metaDataBlock.getMaxRecordsPerBlock();

        if (totalBlocksNum < ((totalSlotsNum + 1) / maxSlotsPerBlockNum)) {
            return totalBlocksNum + 1;
        }
        return totalBlocksNum;
    }

    // Works
    private static void insertRecord(Record record, int slotForInsertion) {
        if (slotForInsertion < 1000) {
            dataBlocksInMemory.get(slotForInsertion).addRecord(record);
        } else {
            int blockNum = findBlockOfRecord(slotForInsertion);
            int slotNum = findSlotOfRecord(slotForInsertion);
            dataBlocksInMemory.get(blockNum).addToDeletedSlot(record, slotNum);
        }
        metaDataBlock.increaseTotalSlotsNum();
    }

    //Works
    private static void createNewDataBlock() {
        int id = getNewDataBlockId();
        ArrayList<Record> recordList = new ArrayList<>();
        DataBlock dataBlock = new DataBlock(id, recordList);
        dataBlocksInMemory.put(id, dataBlock);
        metaDataBlock.addAlteredBlock(id);
    }

    // Works
    private static MetaDataNode readMetaDataNode() {
        MetaDataNode metaDataNode = null;
        byte[] byteDataNode = new byte[BLOCK_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(PATH_TO_INDEX_FILE, "rws")) {
            raf.read(byteDataNode, 0, BLOCK_SIZE);
            metaDataNode = (MetaDataNode) deserialize(byteDataNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataNode;
    }

    // Works
    private static MetaDataBlock readMetaDataBlock() {
        MetaDataBlock metaDataBlock = null;
        byte[] byteDataBlock = new byte[BLOCK_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(PATH_TO_DATAFILE, "rws")) {
            raf.read(byteDataBlock, 0, BLOCK_SIZE);
            metaDataBlock = (MetaDataBlock) deserialize(byteDataBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaDataBlock;
    }

    // Works
    private static void writeMetaDataNodeToIndexFile() {
        try (RandomAccessFile raf = new RandomAccessFile(PATH_TO_INDEX_FILE, "rws")) {
            byte[] byteDataNode = new byte[BLOCK_SIZE];
            byte[] serializedBlock = serialize(metaDataNode);
            System.arraycopy(serializedBlock, 0, byteDataNode, 0, serializedBlock.length);
            raf.write(byteDataNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Works
    private static void writeMetaDataBlockToDataFile() {
        try (RandomAccessFile raf = new RandomAccessFile(PATH_TO_DATAFILE, "rws")) {
            byte[] byteDataBlock = new byte[BLOCK_SIZE];
            byte[] serializedBlock = serialize(metaDataBlock);
            System.arraycopy(serializedBlock, 0, byteDataBlock, 0, serializedBlock.length);
            raf.write(byteDataBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Works
    public static void writeAlteredBlocksToDataFile() {
        writeMetaDataBlockToDataFile();
        int alteredBlocksNum = metaDataBlock.getAlteredBlocksNum();
        for (int i = 0; i < alteredBlocksNum; i++) {
            int alteredBlock = metaDataBlock.getMinAlteredBlock();
            DataBlock dataBlock = dataBlocksInMemory.get(alteredBlock);
            writeBlockToDataFile(dataBlock, alteredBlock);
        }
    }

    // Works
    private static void writeBlockToDataFile(DataBlock dataBlock, int blockNum) {
        try (RandomAccessFile f = new RandomAccessFile(PATH_TO_DATAFILE, "rw")) {
            byte[] dataBlockInBytes = serialize(dataBlock);
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(dataBlockInBytes, 0, block, 0, dataBlockInBytes.length);
            f.seek((long) blockNum * BLOCK_SIZE);
            f.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeNodeToIndexFile(Node node, int blockNum) {
        try (RandomAccessFile f = new RandomAccessFile(PATH_TO_INDEX_FILE, "rw")) {
            byte[] nodeInBytes = serialize(node);
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(nodeInBytes, 0, block, 0, nodeInBytes.length);
            f.seek((long) blockNum * BLOCK_SIZE);
            f.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadIndexNode(int blockNum) {
        byte[] byteNode = new byte[BLOCK_SIZE];
        Node node = null;
        try (RandomAccessFile f = new RandomAccessFile(PATH_TO_INDEX_FILE, "rw")) {
            f.seek((long) blockNum * BLOCK_SIZE);
            f.read(byteNode, 0, BLOCK_SIZE);
            node = (Node) deserialize(byteNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != node) {
            nodesInMemory.put(node.getIndexBlockLocation(), node);
        }
    }

    public static void writeAlteredNodesToIndexFile() {
        writeMetaDataNodeToIndexFile();
        int alteredNodesNum = metaDataNode.getAlteredNodesNum();
        for (int i = 0; i < alteredNodesNum; i++) {
            int alteredBlock = metaDataNode.getMinAlteredNode();
            Node node = nodesInMemory.get(alteredBlock);
            writeNodeToIndexFile(node, alteredBlock);
        }
    }

    // Works
    private static void loadDataBlock(int blockNum) {
        byte[] byteDataBlock = new byte[BLOCK_SIZE];
        DataBlock dataBlock = null;
        try (RandomAccessFile f = new RandomAccessFile(PATH_TO_DATAFILE, "rw")) {
            f.seek((long) blockNum * BLOCK_SIZE);
            f.read(byteDataBlock, 0, BLOCK_SIZE);
            dataBlock = (DataBlock) deserialize(byteDataBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dataBlock != null) {
            dataBlocksInMemory.put(dataBlock.getId(), dataBlock);
        }
    }

    public static Record getRecordFromDataFile(int recordLocation) {
        int blockNum = findBlockOfRecord(recordLocation);
        int slotNum = findSlotOfRecord(recordLocation);

        if (!dataBlocksInMemory.containsKey(blockNum)) {
            loadDataBlock(blockNum);
        }

        return dataBlocksInMemory.get(blockNum).getRecord(slotNum);
    }

    // Works
    public static Node getNodeFromIndexFile(int index) {
        if (!nodesInMemory.containsKey(index)) {
            loadIndexNode(index);
        }
        return nodesInMemory.get(index);
    }

    // Works
    public static DataBlock getDataBlock(int i) {
        if (!dataBlocksInMemory.containsKey(i)) {
            loadDataBlock(i);
        }
        return dataBlocksInMemory.get(i);
    }

    public static int getTotalBlockNum() {
        return metaDataBlock.getTotalBlockNum();
    }

    public static int getCurrentDimensions() {
        return metaDataBlock.getCurrentDimensions();
    }

    public static int getMaxEntriesPerNode() {
        return metaDataNode.getMaxEntriesNum();
    }

    public static int getMinEntriesPerNode() {
        return metaDataNode.getMinEntriesNum();
    }
}
