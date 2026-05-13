package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulatedCache {

    protected double[][] adjacencyMatrix;
    private double[][] preparedMatrix;
    protected double[][] pathCostMatrix;
    protected int[][] predMatrix;

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
                Integer l = nodeLeft.get(i);
                Integer r = nodeRight.get(i);
                Double c = cost.get(i);
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
        adjacencyMatrix = new double[size][size];
        initiateMatrices();
        for (int i = 0; i < edgeCount; i++) {
            Integer l = nodeLeft.pop();
            Integer r = nodeRight.pop();
            Double c = cost.pop();
            adjacencyMatrix[l][r] = c;
            predMatrix[l][r] = l;
        }

    }
    protected void initiateMatrices() {
        pathCostMatrix = new double[size][size];
        predMatrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    pathCostMatrix[i][j] = 0.0;
                    adjacencyMatrix[i][j] = 0.0;
                } else {
                    pathCostMatrix[i][j] = Double.POSITIVE_INFINITY;
                    adjacencyMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
                predMatrix[i][j] = j;
            }
        }
    }
    public void prepareToDouble() {
        if (preparedMatrix == null) {
            pathCostMatrix = new double[size][size];
            for (int i = 0; i < size; i++){
                pathCostMatrix[i] = adjacencyMatrix[i].clone();
            }
        }
        preparedMatrix = new double[size][size];
        for (int i = 0; i < size; i++){
            preparedMatrix[i] = pathCostMatrix[i].clone();
        }
    }
    public Double readGraphVal(int row, int col) {
        return preparedMatrix[row][col];
    }
    public Integer readPred(int row, int col) { return predMatrix[row][col]; }
    public void writeVal(int row, int col, Double val, int pred) {
        pathCostMatrix[row][col] = val;
        predMatrix[row][col] = pred;
    }

    public synchronized void writeValIfLess(int row, int col, Double val, int pred) {
        if (val < pathCostMatrix[row][col]) {
            writeVal(row, col, val, pred);
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

    public int getSize(){
        return size;
    }
    public ArrayList<ArrayList<Integer>> getNodesIn(){ return nodesIn; }
    public ArrayList<ArrayList<Integer>> getNodesOut(){ return nodesOut; }
    public ArrayList<ArrayList<Double>> getCostsIn(){ return costsIn; }
    public ArrayList<ArrayList<Double>> getCostsOut(){ return costsOut; }

    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
}
