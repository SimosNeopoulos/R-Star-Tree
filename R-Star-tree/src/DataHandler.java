import java.io.*;
import java.util.ArrayList;

public class DataHandler {
    private final String PATH_TO_DATAFILE = "";
    private static final int BLOCK_SIZE = 32 * 1024;
    private AvailableSlotForRecords freeSlots;
    private int currentDimensions;
    private int totalBlocksNum;
    private int totalRecordsNum;

//    private int findLocationOfRecord(int index) {
//        int block = index / 10000;
//        int slot = index % 10000;
//
//        return (block * BLOCK_SIZE) + (slot * recordLengthInBytes);
//    }
//
//    private int contractIndexOfRecordLocation(int block, int slot) {
//        return block * 10000 + slot; // ΠΡΟΣΟΧΗ: Το πρώτο slot ενός block έχει τιμή "0" οχι 1""
//    }

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
// TODO: Να δω αν χρειάζεται το goodPutLengthInBytes αφού φτιάξω λειτουργία που να δημιουργεί block
    private static int calculateMaxNumOfRecordsInBlock(int recordDimensions) {
        ArrayList<Double> coordinates = new ArrayList<>();
        for (int i = 0; i < recordDimensions; i++) {
            coordinates.add(0.0);
        }
        Record record = new Record(-1, "test", coordinates);
        ArrayList<Record> records = new ArrayList<>();
        records.add(record);
        int maxNumOfRecordsPerBlock = 1;
        for(int i=0; i < Integer.MAX_VALUE; i++) {
            byte[] recordArrayInBytes = new byte[0];
            byte[] goodPutLengthInBytes = new byte[0];
            try {
                recordArrayInBytes = serialize(records);
                goodPutLengthInBytes = serialize(recordArrayInBytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (goodPutLengthInBytes.length + recordArrayInBytes.length > DataHandler.BLOCK_SIZE)
                return maxNumOfRecordsPerBlock;

            records.add(record);
            maxNumOfRecordsPerBlock++;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(DataHandler.calculateMaxNumOfRecordsInBlock(1));
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
