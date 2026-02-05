package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulatedCache {

    private Double[][] adjacencyMatrix;
    private Double[][] preparedMatrix;
    private Double[][] pathCostMatrix;
    private int[][] witnessMatrix;

    private int size;

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
            int edgeCount = cost.size();
            for (int i = 0; i < edgeCount; i++) {
                Integer l = nodeLeft.pop();
                Integer r = nodeRight.pop();
                Double c = cost.pop();
                adjacencyMatrix[l][r] = c;
            }
        } catch (FileNotFoundException e) {
            System.err.println("The file " + fileAddress + " appears not to exist.");
            e.printStackTrace();
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

    public void printAllCosts(int row){
        System.out.println("FROM: " + row);
        int c = 0;
        for (Double d : pathCostMatrix[row]) {
            System.out.print("TO: " + c + " - ");
                System.out.println(d);
                c++;
        }
    }

    public void printDijkstraPathFrom(int from, int to) {
        if (witnessMatrix[from][to] != -1 && from != to) {
            System.out.print("PATH FROM: " + from);
            System.out.print(" TO: " + to + ": ");
            int mid = to;
            while (mid != witnessMatrix[from][mid]) {
                System.out.print(mid + "<--");
                mid = witnessMatrix[from][mid];
            }
            System.out.print(from);
            System.out.println(" - DIST: " + pathCostMatrix[from][to]);
        }
    }
    public void printFWPathFrom(int from, int to){
        System.out.print("PATH FROM: " + from);
        System.out.print(" TO: " + to + ": ");
        for (int d : calculatePath(from, to)) {
            System.out.print(d + "-->");
        }
        System.out.println("END");
    }
    public void calculateAndPrintAllPaths() {
        for (int i = 0; i < getSize(); i++){
            System.out.println("PATHS FROM: " + i);
            for (int j = 0; j < getSize(); j++){
                if (pathCostMatrix[i][j] != Double.POSITIVE_INFINITY && pathCostMatrix[i][j] != 0.0) {
                    System.out.print("TO " + j + ": ");
                    for (Integer d : calculatePath(i, j)) {
                        System.out.print(d + "-->");
                    }
                    System.out.print("END");
                    System.out.printf(" - DIST: %.4f\n\n", pathCostMatrix[i][j]);
                }
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
    public void compressGraph() {
        HashMap<ArrayList<Integer>, Double> pathLengthMap = new HashMap<ArrayList<Integer>, Double>();
        HashMap<Integer, ArrayList<Integer>> nodePathMap = new HashMap<Integer, ArrayList<Integer>>();
        for (int node = 0; node < adjacencyMatrix.length; node++){
            HashMap<Integer, Double> neighbours = getNeighbours(node);
            if (neighbours.size() == 2) {
                ArrayList<Integer> existingPathLeft = null;
                ArrayList<Integer> existingPathRight = null;
                Double totalLength = neighbours.values().stream().mapToDouble(Double::doubleValue).sum();
                Set<Integer> neighbourIds = neighbours.keySet();
                for (ArrayList<Integer> path : pathLengthMap.keySet()) {
                    if (neighbourIds.contains(path.getFirst())){
                        existingPathRight = path;
                        neighbourIds.remove(path.getFirst());
                    }
                    else if (neighbourIds.contains(path.getLast())){
                        existingPathLeft = path;
                        neighbourIds.remove(path.getLast());
                    }
                }

                if (existingPathLeft == null && existingPathRight == null) {
                    ArrayList<Integer> neighbourIdList = new ArrayList<>(node);
                    nodePathMap.put(node, neighbourIdList);
                    pathLengthMap.put(neighbourIdList, totalLength);
                } else if (existingPathLeft == null) {
                    existingPathRight.addFirst(node);
                    nodePathMap.put(node, existingPathRight);
                    pathLengthMap.put(existingPathRight, neighbours.get(existingPathRight.getFirst()) + pathLengthMap.get(existingPathRight));
                } else if (existingPathRight == null) {
                    existingPathLeft.addLast(node);
                    nodePathMap.put(node, existingPathLeft);
                    pathLengthMap.put(existingPathLeft, neighbours.get(existingPathLeft.getLast()) + pathLengthMap.get(existingPathLeft));
                } else {
                    if (existingPathLeft.size() <= existingPathRight.size()) {
                        existingPathRight.addFirst(node);
                        existingPathRight.addAll(0, existingPathLeft);
                        for (Integer leftPathNode : existingPathLeft) {
                            nodePathMap.put(leftPathNode, existingPathRight);
                        }
                        nodePathMap.put(node, existingPathRight);
                        pathLengthMap.put(existingPathRight, totalLength + pathLengthMap.get(existingPathLeft) + pathLengthMap.get(existingPathRight));
                        pathLengthMap.remove(existingPathLeft);
                    } else {
                        existingPathLeft.addLast(node);
                        existingPathLeft.addAll(existingPathRight);
                        for (Integer rightPathNode : existingPathRight) {
                            nodePathMap.put(rightPathNode, existingPathLeft);
                        }
                        nodePathMap.put(node, existingPathLeft);
                        pathLengthMap.put(existingPathLeft, totalLength + pathLengthMap.get(existingPathLeft) + pathLengthMap.get(existingPathRight));
                        pathLengthMap.remove(existingPathRight);
                    }
                }
            }
        }

        int newMatrixSize = getSize() - nodePathMap.keySet().size();
        Double[][] newAdjacencyMatrix = new Double[newMatrixSize][newMatrixSize];
        
    }
}
