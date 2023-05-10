import java.io.Serializable;

// Κλάση που αναπαριστά το ένα meta data node των 32KB που περιέχει πληροφορίες σχετικά με το index file
public class MetaDataNode implements Serializable {
    private int totalNodesNum;
    private int indexFileLocationForNewNode;
    private int totalLevelNum;
    private int nodeMaxEntriesNum;
    private int nodeMinEntriesNum;
    private int reInsertPEntries;
    private PriorityDeQueue alteredNodes;
    private PriorityDeQueue emptyBlocksInIndexFile;

    public MetaDataNode() {
        totalNodesNum = 0;
        indexFileLocationForNewNode = 0;
        totalLevelNum = 1;
        alteredNodes = new PriorityDeQueue();
        emptyBlocksInIndexFile = new PriorityDeQueue();
    }

    public int getIndexFileLocationForNewNode() {
        return indexFileLocationForNewNode;
    }

    public void increaseIndexFileLocationForNewNode() {
        indexFileLocationForNewNode++;
    }

    public void addEmptyIndexNode(int locationInIndexFile) {
        emptyBlocksInIndexFile.add(locationInIndexFile);
    }

    public boolean emptyIndexNodeExists() {
        return !emptyBlocksInIndexFile.isEmpty();
    }

    public int getEmptyIndexLocation() {
        return emptyBlocksInIndexFile.getMinEntry();
    }

    public void addAlteredNode(int i) {
        alteredNodes.add(i);
    }

    public int getAlteredNodesNum() {
        return alteredNodes.getSet().size();
    }

    public int getMinAlteredNode() {
        return alteredNodes.getMinEntry();
    }

    public int getMaxEntriesNum() {
        return nodeMaxEntriesNum;
    }

    public void setNodeMaxEntriesNum(int maxEntriesNum) {
        this.nodeMaxEntriesNum = maxEntriesNum;
        this.nodeMinEntriesNum = (int) (0.4 * nodeMaxEntriesNum);
        this.reInsertPEntries = (int) (0.4 * nodeMaxEntriesNum);
    }

    public int getTotalNodesNum() {
        return totalNodesNum;
    }

    public int getReInsertPEntries() {
        return reInsertPEntries;
    }

    public void increaseTotalNodesNum() {
        this.totalNodesNum++;
    }

    public void decreaseTotalNodeNum() {
        this.totalNodesNum--;
    }

    public int getTotalLevelNum() {
        return totalLevelNum;
    }

    public void increaseTotalLevelNum() {
        this.totalLevelNum++;
    }

    public void decreaseTotalLevelNum() {
        this.totalLevelNum--;
    }

    public int getMinEntriesNum() {
        return nodeMinEntriesNum;
    }
}
