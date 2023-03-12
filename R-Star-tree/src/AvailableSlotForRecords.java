import java.util.Collections;
import java.util.HashSet;

public class AvailableSlotForRecords {
    private HashSet<Integer> set;

    public AvailableSlotForRecords() {
        this.set = new HashSet<Integer>();
    }

    public AvailableSlotForRecords(AvailableSlotForRecords oldSet) {
        this.set = oldSet.getSet();
    }

    public HashSet<Integer> getSet() {
        return this.set;
    }

    public void add(int x) {
        set.add(x);
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public int getLocationForEntryInsertion() {
        if(set.isEmpty())
            return -1;
        int min = Collections.min(set, null);
        set.remove(min);
        return min;
    }

//    public static void main(String[] args) {
//        AvailableSlotForRecords freeSlots = new AvailableSlotForRecords();
//         freeSlots.add(1021);
//         freeSlots.add(1000);
//         freeSlots.add(1025);
//         freeSlots.add(1022);
//         for(int i=0; i<5; i++) {
//             System.out.println(freeSlots.getLocationForEntryInsertion());
//             System.out.println(freeSlots.getSet());
//         }
//    }
}