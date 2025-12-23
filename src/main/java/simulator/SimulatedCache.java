package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class SimulatedCache {

    private Double[][] adjacencyMatrix;
    private Path[][] iteratedMatrix;

    private int size;

    public SimulatedCache(String fileAddress) {
        File sourceFile = new File(fileAddress);
        try (Scanner reader = new Scanner(sourceFile)){
            LinkedList<Integer> nodeLeft = new LinkedList<Integer>();
            LinkedList<Integer> nodeRight = new LinkedList<Integer>();
            LinkedList<Double> cost = new LinkedList<Double>();
            while (reader.hasNextLine()) {
                String[] linkInfo = reader.nextLine().split(" ");
                nodeLeft.add(Integer.valueOf(linkInfo[1]));
                nodeRight.add(Integer.valueOf(linkInfo[2]));
                cost.add(Double.valueOf(linkInfo[3]));
            }
            size = cost.size();
            adjacencyMatrix = new Double[size][size];
            iteratedMatrix = new Path[size][size];
            for (int i = 0; i < size; i++) {
                Integer l = nodeLeft.pop();
                Integer r = nodeRight.pop();
                Double c = cost.pop();
                adjacencyMatrix[l][r] = c;
                adjacencyMatrix[r][l] = c;
                for (int j = 0; j < size; j++) {
                    Path p = new Path();
                    if (i == j) {
                        p.appendToPath(0.0, i);
                    } else {
                        p.appendToPath(Double.POSITIVE_INFINITY, -1);
                    }
                    iteratedMatrix[i][j] = p;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("The file " + fileAddress + " appears not to exist.");
            e.printStackTrace();
        }
    }
    public Double readVal(int col, int row){
        return adjacencyMatrix[row][col];
    }

    public void writeVal(int col, int row, Path p) {
        iteratedMatrix[row][col] = p;
    }

    public HashMap<Integer, Double> getNeighboursAndPaths(int from){
        HashMap<Integer, Double> neighbours = new HashMap<Integer, Double>();
        int v = 0;
        for (Double d : adjacencyMatrix[from]) {
            if (d != null) {
                neighbours.put(v, d);
            }
            v++;
        }
        return neighbours;
    }

    public Path getCurrentPath(int from, int to){
        return iteratedMatrix[from][to];
    }

    public void printAllPaths(int row){
        System.out.println("FROM: " + row);
        for (Path p : iteratedMatrix[row]) {
            System.out.print("TO: " + p.getHead() + " - ");
                p.print();
        }
    }

    public int getSize(){
        return size;
    }
}
