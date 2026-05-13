package compressor;
import simulator.SimulatedCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
public class CompressedCache extends SimulatedCache {
    public CompressedCache(String fileAddress) {
        super(fileAddress);
    }

    @Override
    protected void matrixConstruction(int edgeCount, LinkedList<Integer> nodeLeft, LinkedList<Integer> nodeRight,LinkedList<Double> cost){
        GraphCompressor gc = new GraphCompressor(this, nodesIn, costsIn, nodesOut, costsOut);
        gc.compress();
        nodesOut = gc.getGraph();
        size = nodesOut.size();
        costsOut = gc.getCosts();
        createMatrixFromList(nodesOut, costsOut, gc.getCompressionMap());
        createListsFromMatrix();
    }

    private void createMatrixFromList(ArrayList<ArrayList<Integer>> graph, ArrayList<ArrayList<Double>> costs, HashMap<Integer, Integer> compressionMap) {
        adjacencyMatrix = new double[size][size];
        predMatrix = new int[size][size];
        initiateMatrices();
        for (int i = 0; i < graph.size(); i++) {
            ArrayList<Integer> nodeRow = graph.get(i);
            if (nodeRow != null) {
                for (Integer j : nodeRow) {
                    int index = nodeRow.indexOf(j);
                    adjacencyMatrix[i][compressionMap.get(j)] = costs.get(i).get(index);
                    predMatrix[i][compressionMap.get(j)] = i;
                    nodeRow.set(index, compressionMap.get(j));
                }
            }
        }
    }

    private void createListsFromMatrix(){

        nodesOut = new ArrayList<ArrayList<Integer>>(size);
        costsOut = new ArrayList<ArrayList<Double>>(size);
        nodesIn = new ArrayList<ArrayList<Integer>>(size);
        costsIn = new ArrayList<ArrayList<Double>>(size);
        for (int i = 0; i < size; i++) {
            nodesOut.add(new ArrayList<Integer>());
            costsOut.add(new ArrayList<Double>());
            nodesIn.add(new ArrayList<Integer>());
            costsIn.add(new ArrayList<Double>());
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double cost = adjacencyMatrix[i][j];
                if (cost != 0.0 && cost != Double.POSITIVE_INFINITY){
                    nodesOut.get(i).add(j);
                    nodesIn.get(j).add(i);
                    costsOut.get(i).add(cost);
                    costsIn.get(j).add(cost);
                }
            }
        }
    }
}
