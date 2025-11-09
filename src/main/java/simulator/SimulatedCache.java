package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class SimulatedCache {

    private Double[][] adjacencyMatrix;
    private Path[][] iteratedMatrix;

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
            int width = cost.size();
            this.adjacencyMatrix = new Double[width][width];
            iteratedMatrix = new Path[width][width];
            for (int i = 0; i < width; i++) {
                adjacencyMatrix[nodeLeft.pop()][nodeRight.pop()] = cost.pop();
                for (int j = 0; j < width; j++) {
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
    Double readVal(int col, int row){
        return adjacencyMatrix[row][col];
    }

    void writeVal(int col, int row, Double val) {
        adjacencyMatrix[row][col] = val;
    }
}
