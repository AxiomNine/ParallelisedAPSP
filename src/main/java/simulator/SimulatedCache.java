package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulatedCache {

    protected Double[][] adjacencyMatrix;
    private Double[][] preparedMatrix;
    protected Double[][] pathCostMatrix;
    protected int[][] witnessMatrix;

    protected int size;
    protected ArrayList<ArrayList<Integer>> nodesIn;
    protected ArrayList<ArrayList<Double>> costsIn;
    protected ArrayList<ArrayList<Integer>> nodesOut;
    protected ArrayList<ArrayList<Double>> costsOut;
    public SimulatedCache(String fileAddress) {
        File sourceFile = new File(fileAddress);
        try (Scanner reader = new Scanner(sourceFile)){
            LinkedList<Integer> nodeLeft = new LinkedList<Integer>();
            LinkedList<Integer> nodeRight = new LinkedList<Integer>();
            LinkedList<Double> cost = new LinkedList<Double>();
            size = 0;
            while (reader.hasNextLine()) {
                String[] linkInfo = reader.nextLine().split(" ");
                int leftNode = Integer.parseInt(linkInfo[linkInfo.length - 3]);
                nodeLeft.add(leftNode);
                int rightNode = Integer.parseInt(linkInfo[linkInfo.length - 2]);
                nodeRight.add(rightNode);
                cost.add(Double.parseDouble(linkInfo[linkInfo.length - 1]));
                size = Math.max(Math.max(size, leftNode + 1), rightNode + 1);
            }
            int edgeCount = cost.size();

            nodesOut = new ArrayList<ArrayList<Integer>>(size);
            costsOut = new ArrayList<ArrayList<Double>>(size);
            nodesIn = new ArrayList<ArrayList<Integer>>(size);
            costsIn = new ArrayList<ArrayList<Double>>(size);
            for (int i = 0; i < edgeCount; i++) {
                Integer l = nodeLeft.pop();
                Integer r = nodeRight.pop();
                Double c = cost.pop();
                while (l >= nodesOut.size() || r >= nodesOut.size()){
                    nodesOut.add(new ArrayList<Integer>());
                    costsOut.add(new ArrayList<Double>());
                    nodesIn.add(new ArrayList<Integer>());
                    costsIn.add(new ArrayList<Double>());
                }
                nodesOut.get(l).add(r);
                costsOut.get(l).add(c);
                nodesIn.get(r).add(l);
                costsIn.get(r).add(c);
            }
            matrixConstruction(edgeCount, nodeLeft, nodeRight, cost);
        } catch (FileNotFoundException e) {
            System.err.println("The file " + fileAddress + " appears not to exist.");
            e.printStackTrace();
        }
    }
    protected void matrixConstruction(int edgeCount, LinkedList<Integer> nodeLeft, LinkedList<Integer> nodeRight,LinkedList<Double> cost){
        initiateMatrices();
        for (int i = 0; i < edgeCount; i++) {
            Integer l = nodeLeft.pop();
            Integer r = nodeRight.pop();
            Double c = cost.pop();
            adjacencyMatrix[l][r] = c;
        }

    }
    protected void initiateMatrices() {
        adjacencyMatrix = new Double[size][size];
        pathCostMatrix = new Double[size][size];
        witnessMatrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    pathCostMatrix[i][j] = 0.0;
                    witnessMatrix[i][j] = i;
                    adjacencyMatrix[i][j] = 0.0;
                } else {
                    pathCostMatrix[i][j] = Double.POSITIVE_INFINITY;
                    witnessMatrix[i][j] = -1;
                    adjacencyMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }
    public void prepareToDouble() {
        if (preparedMatrix == null) {
            preparedMatrix = adjacencyMatrix.clone();
        } else {
            preparedMatrix = pathCostMatrix.clone();
        }
    }
    public Double readGraphVal(int row, int col) {
        return preparedMatrix[row][col];
    }
    public Integer readWitness(int row, int col) { return witnessMatrix[row][col]; }
    public void writeVal(int row, int col, Double val, Integer witness) {
        pathCostMatrix[row][col] = val;
        witnessMatrix[row][col] = witness;
    }

    public void writeValIfLess(int row, int col, Double val, Integer witness) {
        if (val < pathCostMatrix[row][col]) {
            writeVal(row, col, val, witness);
        }
    }

    public HashMap<Integer, Double> getNeighbours(int from){
        HashMap<Integer, Double> neighbours = new HashMap<Integer, Double>();
        int v = 0;
        for (Double d : adjacencyMatrix[from]) {
            if (d != null && d != Double.POSITIVE_INFINITY && from != v) {
                neighbours.put(v, d);
            }
            v++;
        }
        return neighbours;
    }

    public Double getCurrentCost(int from, int to){
        return pathCostMatrix[from][to];
    }

    public void printDijkstraPathFrom(int from, int to) {
        System.out.print("PATH FROM: " + from);
        System.out.print(" TO: " + to + ": ");
        System.out.print(to);
        int mid = to;
        while (mid != witnessMatrix[from][mid] && witnessMatrix[from][mid] != -1) {
            mid = witnessMatrix[from][mid];
            System.out.print("<--"+ mid);
        }
        System.out.println();
    }
    public void printFWPathFrom(int from, int to){
        System.out.print("PATH FROM: " + from);
        System.out.print(" TO: " + to + ": ");
        for (int d : calculatePath(from, to)) {
            System.out.print(d + "-->");
        }
        System.out.println("END");
    }
    public void calculateAndPrintAllPaths(){
        for (int i = 0; i < getSize(); i++){
            System.out.println("PATHS FROM: " + i);
            for (int j = 0; j < getSize(); j++){
                System.out.print("TO:" + j + ": ");
                for (Integer d : calculatePath(i, j)) {
                    System.out.print(d + "-->");
                }
                System.out.println("END\n");
            }
        }
    }

    public int getSize(){
        return size;
    }

    public List<Integer> calculatePath(int from, int to) {
        if (from == -1 || to == -1) {
            return new ArrayList<>();
        }
        else if (from == to) {
            return new ArrayList<>(List.of(to));
        }
        else {
            int witness =  witnessMatrix[from][to];
            if (witness != from && witness != to) {
                return Stream.concat(calculatePath(from, witness).stream(), calculatePath(witness, to).stream().skip(1)).toList();
            } else {
                return new ArrayList<>(Arrays.asList(from, to));
            }
        }
    }
    public ArrayList<ArrayList<Integer>> getNodesIn(){ return nodesIn; }
    public ArrayList<ArrayList<Integer>> getNodesOut(){ return nodesOut; }
    public ArrayList<ArrayList<Double>> getCostsIn(){ return costsIn; }
    public ArrayList<ArrayList<Double>> getCostsOut(){ return costsOut; }

    public Double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
}
