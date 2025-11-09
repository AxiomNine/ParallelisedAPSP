package simulator;

import java.util.Comparator;
import java.util.ArrayList;

public class Path implements Comparator<Path>{
    private double totalLength;

    private final ArrayList<Integer> nodes = new ArrayList<Integer>();

    public Path() {
        this.totalLength = 0;
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

    public int compare(Path pathA, Path pathB){
        return (int)(pathA.totalLength*100000 - pathB.totalLength*100000);
    }
}
