import java.io.*;
import java.util.ArrayList;

// Μόνο για tests
public class Main {

    public static Entry getEntry() {
        return new LeafEntry(new BoundingRectangle(new ArrayList<>()),0, 0);
    }

    public static void main(String[] args) {
        Entry entry = getEntry();
        System.out.println(entry instanceof NonLeafEntry);
    }
}
