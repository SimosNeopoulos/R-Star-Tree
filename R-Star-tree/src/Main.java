import java.io.*;
import java.util.ArrayList;

// Μόνο για tests
public class Main {

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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        int dimensions = 10;
//        int recordNum = DataHandler.calculateMaxNumOfRecordsInBlock(dimensions);
//
//        System.out.println(recordNum);
//        ArrayList<Double> coordinates = new ArrayList<>();
//        for (int i = 0; i < dimensions; i++)
//            coordinates.add(0.0);
//
//        ArrayList<Record> records = new ArrayList<>();
//        for (int i = 0; i < recordNum; i++) {
//            Record record = new Record(i, "test", coordinates);
//            records.add(record);
//
//        }
//        DataBlock dataBlock = new DataBlock(1, records);
//        DataHandler.writeDataFile(dataBlock, 0);
//        DataBlock newDataBlock = DataHandler.readDataBlock(0);
//        System.out.println(newDataBlock.getRecords().size());
//        System.out.println(newDataBlock.getRecords().get(recordNum-1).getId());
    }
}
