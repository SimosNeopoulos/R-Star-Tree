import java.io.*;
import java.util.ArrayList;

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
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(5);
        byte[] recordInBytes = new byte[0];
        byte[] goodPutLengthInBytes = new byte[0];
        try {
            recordInBytes = serialize(integers);
            goodPutLengthInBytes = serialize(recordInBytes.length);
            System.out.println(recordInBytes);
            System.out.println(goodPutLengthInBytes);
            System.out.println(recordInBytes.length);
            System.out.println(goodPutLengthInBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
