public class MetaDataNode {
    private int totalNodesNum;
    private int nodeMaxEntriesNum;
    private int nodeMinEntriesNum;
    private static PriorityDeQueue alteredNodes;

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
    }

    public int getMinEntriesNum() {
        return nodeMinEntriesNum;
    }

    public void setNodeMinEntriesNum() {
        this.nodeMinEntriesNum = (int) (0.4 *  nodeMaxEntriesNum);
    }
}
