import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataHandler {
    private static final String PATH_TO_DATAFILE = "data_file.dat";
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

    private static void createDataFile() {
        metaDataBlock = new MetaDataBlock();
        writeMetaDataBlockToDataFile();
    }

    private static int getNewDataBlockId() {
        int id = metaDataBlock.getTotalBlockNum();
        metaDataBlock.increaseTotalBlockNum();
        return id;
    }

    private static int findBlockOfRecord(int index) {
        return index / 1000;
    }

    private static int findSlotOfRecord(int index) {
        return (index % 1000) - 1;
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

        if(!blockInMemory(blockId)) {
            loadDataBlock(blockId);
        }
        
        dataBlocksInMemory.get(blockId).deleteRecord(slotNum);
        metaDataBlock.addAlteredBlock(blockId);
        metaDataBlock.addEmptySlot(slotLocation);
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    private static int calculateMaxNumOfRecordsInBlock(int recordDimensions) {
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
            //byte[] goodPutLengthInBytes = new byte[0];
            try {
                recordArrayInBytes = serialize(dataBlock);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (recordArrayInBytes.length > BLOCK_SIZE)
                break;

            maxNumOfRecordsPerBlock++;
        }
        return maxNumOfRecordsPerBlock;
    }

    private void createNewDataBlock() {
        int id = getNewDataBlockId();
        ArrayList<Record> recordList = new ArrayList<>();
        DataBlock dataBlock = new DataBlock(id, recordList);
        dataBlocksInMemory.put(id, dataBlock);
        metaDataBlock.addAlteredBlock(id);
    }

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

    public static void writeAlteredBlocksToDataFile() {
        int alteredBlocksNum = metaDataBlock.getAlteredBlocksNum();
        for (int i = 0; i < alteredBlocksNum; i++) {
            int alteredBlock = metaDataBlock.getMinAlteredBlock();
            DataBlock dataBlock = dataBlocksInMemory.get(alteredBlock);
            writeBlockToDataFile(dataBlock, alteredBlock);
        }
    }

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

    private static void loadDataBlock(int blockNum) {
        readDataBlock(blockNum);
    }

//TODO: Συγχονευση στο loadDataBlock οταν δεν χρειαζεται πλεον.
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
        int dimensions = 1;
        int recordNum = DataHandler.calculateMaxNumOfRecordsInBlock(dimensions);

        System.out.println(recordNum);
        ArrayList<Double> coordinates = new ArrayList<>();
        for (int i = 0; i < dimensions; i++)
            coordinates.add(0.0);

        ArrayList<Record> records = new ArrayList<>();
        for (int i = 0; i < recordNum; i++) {
            Record record = new Record(i, "test", coordinates);
            records.add(record);

        }
        DataBlock dataBlock = new DataBlock(1, records);
        DataHandler.writeBlockToDataFile(dataBlock, 0);
        DataBlock newDataBlock = DataHandler.readDataBlock(0);
        System.out.println(newDataBlock.getRecords().size());
        System.out.println(newDataBlock.getRecords().get(recordNum - 1).getId());
    }


    public int getCurrentDimensions() {
        return currentDimensions;
    }

    public void setCurrentDimensions(int currentDimensions) {
        this.currentDimensions = currentDimensions;
    }

    public int getTotalBlocksNum() {
        return totalBlocksNum;
    }

    public void setTotalBlocksNum(int totalBlocksNum) {
        this.totalBlocksNum = totalBlocksNum;
    }

    public int getTotalRecordsNum() {
        return totalRecordsNum;
    }

    public void setTotalRecordsNum(int totalRecordsNum) {
        this.totalRecordsNum = totalRecordsNum;
    }
}
