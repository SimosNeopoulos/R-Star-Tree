import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataHandler {
    private static final String PATH_TO_DATAFILE = "data_file.dat";
    private static final String PATH_TO_CSV = "nodes.csv";
    private static final int BLOCK_SIZE = 32 * 1024;
    private static int currentDimensions;
    private static int totalBlocksNum;
    private static int totalRecordsNum;
    private static MetaDataBlock metaDataBlock;
    private static HashMap<Integer, DataBlock> dataBlocksInMemory = new HashMap<>();

    public static void initialise() {
        if (new File(PATH_TO_DATAFILE).exists()) {
            metaDataBlock = readMetaDataBlock();
            return;
        }
        createDataFile();
    }

    // Works
    private static void createDataFile() {
        metaDataBlock = new MetaDataBlock();
        createNewDataBlock();
        writeAlteredBlocksToDataFile();
    }

    // Works
    private static int getNewDataBlockId() {
        metaDataBlock.increaseTotalBlockNum();
        int id = metaDataBlock.getTotalBlockNum();
        return id;
    }

    // Works
    private static int findBlockOfRecord(int index) {
        return index / 1000;
    }

    // Works
    private static int findSlotOfRecord(int index) {
        return (index % 1000);
    }

    private static int contractIndexOfRecordLocation(int block, int slot) {
        return block * 1000 + slot; // ΠΡΟΣΟΧΗ: Το πρώτο slot ενός block έχει τιμή "0" οχι 1""
    }

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

    // Works
    private static void calculateMaxNumOfRecordsInBlock(int recordDimensions) {
        ArrayList<Record> records = new ArrayList<>();
        int maxNumOfRecordsPerBlock = 0;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            ArrayList<Double> coordinates = new ArrayList<>();
            for (int j = 0; j < recordDimensions; j++) {
                coordinates.add(0.0);
            }
            Record record = new Record(i, "test", coordinates);

            records.add(record);
            DataBlock dataBlock = new DataBlock(1, records);


            byte[] recordArrayInBytes = new byte[0];
            try {
                recordArrayInBytes = serialize(dataBlock);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (recordArrayInBytes.length > BLOCK_SIZE)
                break;

            maxNumOfRecordsPerBlock++;
        }
        metaDataBlock.setMaxRecordsPerBlock(maxNumOfRecordsPerBlock);
    }

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

    private static void insertToDataFile(String line) {
        int slotLocationForInsertion = findSlotToInsert();
        readyDataBlockForInsertion(slotLocationForInsertion);
        insertRecord(new Record(line.split(",")), slotLocationForInsertion);
    }

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
            f.seek(blockNum * BLOCK_SIZE);
            f.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Works
    private static void loadDataBlock(int blockNum) {
        readDataBlock(blockNum);
    }

    //TODO: Συγχονευση στο loadDataBlock οταν δεν χρειαζεται πλεον.
    // Works
    private static DataBlock readDataBlock(int blockNum) {
        byte[] byteDataBlock = new byte[BLOCK_SIZE];
        DataBlock dataBlock = null;
        try (RandomAccessFile f = new RandomAccessFile(PATH_TO_DATAFILE, "rw")) {
            f.seek(blockNum * BLOCK_SIZE);
            f.read(byteDataBlock, 0, BLOCK_SIZE);
            dataBlock = (DataBlock) deserialize(byteDataBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataBlocksInMemory.put(dataBlock.getId(), dataBlock);
        return dataBlock;
    }

    public static void main(String[] args) throws IOException {
        initialise();
        calculateMaxNumOfRecordsInBlock(2);
        readRecordsFromCSV();
        System.out.println("Max Records: " + metaDataBlock.getMaxRecordsPerBlock());
        System.out.println("Total Slots: " + metaDataBlock.getTotalSlotsNum());
        System.out.println("Total Blocks: " + metaDataBlock.getTotalBlockNum());
        System.out.println("Slot to insert: " + findSlotToInsert());
        System.out.println(Integer.MAX_VALUE);
//        createNewDataBlock();
//        loadDataBlock(997);
//        readyDataBlockForInsertion(1);
//        insertRecord(new Record(-1, "name", new ArrayList<Double>()), 1);
//        System.out.println("Data Block Id: " + dataBlocksInMemory.get(997).getRecord(1).getId());

        writeAlteredBlocksToDataFile();
    }


    public static int getCurrentDimensions() {
        return currentDimensions;
    }

    public static int getTotalBlocksNum() {
        return totalBlocksNum;
    }

    public static int getTotalRecordsNum() {
        return totalRecordsNum;
    }
}
