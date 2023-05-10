import java.io.Serializable;
import java.util.ArrayList;

// Κλάση που αναπαριστά το ένα data block των 32KB που περιέχει τα record που αποθηκεύονται στο datafile
public class DataBlock implements Serializable {
    private int id;
    private ArrayList<Record> records;

    public DataBlock(int id, ArrayList<Record> records) {
        this.id = id;
        this.records = records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    public Record getRecord(int i) {
        return this.records.get(i);
    }

    public void addRecord(Record record) {
        this.records.add(record);
    }

    public int getId() {
        return id;
    }

    public void addToDeletedSlot(Record record, int slotNum) {
        this.records.set(slotNum, record);
    }

    public void deleteRecord(int slotNum) {
        // Τα records που έχουν για id -1 είναι dummy records.
        // Αυτό σημαίνει οτι το record που υπήρχει σε αυτή τη θέση έχει διαγραφεί
        Record record = new Record(-1, new ArrayList<>());
        this.records.set(slotNum, record);
    }

    public int getRecordSize() {
        return records.size();
    }

    public ArrayList<Record> getRecords() {
        return records;
    }
}
