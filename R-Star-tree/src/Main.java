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
//        DataHandler.initialiseIndexFile();
        Bounds bounds = new Bounds(1,1);
        ArrayList<Bounds> boundingRectangles = new ArrayList<>();
        boundingRectangles.add(bounds);
        BoundingRectangle boundingRectangle = new BoundingRectangle(boundingRectangles);
        RStarTree rStarTree = new RStarTree();

        ArrayList<LeafEntry> entries = tempGetEntries(5);
        for (LeafEntry entry: entries) {
            rStarTree.insertData(entry);
        }

        ArrayList<LeafEntry> queryEntries = rStarTree.kNNQuery(5, boundingRectangle);
        for (LeafEntry leafEntry: queryEntries) {
            System.out.println("Entry id: " + leafEntry.getObjectId());
            rStarTree.deleteData(leafEntry);
            System.out.println("Total level num of RTree: " + rStarTree.getTotalLevelNum());
            Node root = rStarTree.getRoot();
            Main main = new Main();
            main.numOfLeafEntries(root);
            System.out.println("Num of Entries: " + main.numOfEntries);
            System.out.println("Num of nodes in RTree: " + DataHandler.gettotalNodesInIndex());
            System.out.println();
        }




    }
}
