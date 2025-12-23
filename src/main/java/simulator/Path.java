package simulator;

import java.util.Comparator;
import java.util.ArrayList;

import static java.util.Collections.addAll;

public class Path implements Comparable<Path>{
    private double totalLength;

    private final ArrayList<Integer> nodes = new ArrayList<Integer>();

    public Path() {
        this.totalLength = 0;
    }

    public Path(Path original){
        nodes.addAll(original.getPath());
        this.totalLength = original.totalLength;
    }

    public double getLength(){
        return totalLength;
    }

    public ArrayList<Integer> getPath(){
        return nodes;
    }

    public void appendToPath(double cst, int node) {
        nodes.add(node);
        totalLength += cst;
    }

    public int compareTo(Path pathB){
        return (int)(this.totalLength*100000 - pathB.totalLength*100000);
    }

    public int getHead(){
        return nodes.getLast();
    }

    public void print() {
        for (Integer i : getPath()) {
            System.out.print(i + " --> ");
        }
        System.out.println("END");
    }
}
