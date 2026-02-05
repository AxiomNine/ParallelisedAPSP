package simulator;

public class PathElement implements Comparable<PathElement>{
    private double totalLength;
    private final int headNode;
    public PathElement(int headNode) {
        this.totalLength = 0;
        this.headNode = headNode;
    }
    public PathElement(int headNode, double totalLength){
        this.totalLength = totalLength;
        this.headNode = headNode;
    }

    public double getLength(){
        return totalLength;
    }

    public int compareTo(PathElement pathB){
        return (int)(this.totalLength*100000 - pathB.totalLength*100000);
    }
    public int getHead(){
        return headNode;
    }
}
