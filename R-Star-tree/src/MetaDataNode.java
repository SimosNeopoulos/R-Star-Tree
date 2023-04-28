import java.io.Serializable;

public class MetaDataNode implements Serializable {
    private int totalNodesNum;
    private int totalLevelNum;
    private int nodeMaxEntriesNum;
    private int nodeMinEntriesNum;
    private int reInsertPEntries;
    private PriorityDeQueue alteredNodes;

    public MetaDataNode() {
        totalNodesNum = 0;
        totalLevelNum = 1;
        alteredNodes = new PriorityDeQueue();
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

    public void setNodeMaxEntriesNum(int nodeMaxEntriesNum) {
        this.nodeMaxEntriesNum = nodeMaxEntriesNum;
        this.nodeMinEntriesNum = (int) (0.4 * nodeMaxEntriesNum);
        this.reInsertPEntries = (int) (0.4 * nodeMaxEntriesNum); // TODO: Να κανω το 0.4 σε 0.3 οταν τελειωσω με τα tests
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

    public void decreaseTotalNodesNum() {
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
