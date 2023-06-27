public interface Entry {
    BoundingRectangle getBoundingRectangle();
    boolean isDominatedByEntry(Entry entry);
}
