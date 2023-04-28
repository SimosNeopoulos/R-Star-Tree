import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;

// Μόνο για tests
public class Main {

    private int numOfEntries;

    public Main() {
        this.numOfEntries = 0;
    }

    public void numOfLeafEntries(Node node) {
        if (!node.isLeaf()) {
            for (Entry entry: node.getEntries()) {
                NonLeafEntry nonLeafEntry = (NonLeafEntry) entry;
                numOfLeafEntries(DataHandler.getNodeFromIndexFile(nonLeafEntry.getChildPTR()));
            }
        } else {
            for (Entry entry: node.getEntries()) {
                numOfEntries++;
            }
        }
    }

        public static ArrayList<LeafEntry> tempGetEntries(int num) {
        ArrayList<LeafEntry> entries = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            Bounds bound = new Bounds(i, i);
            ArrayList<Bounds> bounds = new ArrayList<>();
            bounds.add(bound);
            entries.add(new LeafEntry(new BoundingRectangle(bounds), i, i));
        }

        return entries;
    }

    public static void main(String[] args) {
        DataHandler.initialiseDataFile();
        System.out.println(DataHandler.getDataBlock(15).getRecordSize());
        System.out.println("Total Block num: " + DataHandler.getTotalBlockNum());
        System.out.println("Total records: " + DataHandler.getTotalRecords());
        System.out.println("Max Records per Block: " + DataHandler.getMaxEntriesPerBlock());
        RStarTree rStarTree = new RStarTree();
        System.out.println(rStarTree.getTotalLevelNum());
        Node root = rStarTree.getRoot();
        Main main = new Main();
        main.numOfLeafEntries(root);
        System.out.println(main.numOfEntries);
    }
}
