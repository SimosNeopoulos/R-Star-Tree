import java.util.Collections;
import java.util.HashSet;

public class PriorityDeQueue {
    private HashSet<Integer> set;

    public PriorityDeQueue() {
        this.set = new HashSet<Integer>();
    }

    public PriorityDeQueue(PriorityDeQueue oldSet) {
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

    public int getAvailableSlotNum() {
        return this.set.size();
    }

    public int getMinEntry() {
        if(set.isEmpty())
            return 0;
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
