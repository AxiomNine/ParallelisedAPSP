package compressor;
import simulator.SimulatedCache;

import java.util.ArrayList;
import java.util.HashMap;
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
        setAntiGraph(gc.getCompressionMap());
    }

    private void createMatrixFromList(ArrayList<ArrayList<Integer>> graph, ArrayList<ArrayList<Double>> costs, HashMap<Integer, Integer> compressionMap) {
        adjacencyMatrix = new Double[size][size];
        pathCostMatrix = new Double[size][size];
        witnessMatrix = new int[size][size];
        initiateMatrices();
        for (int i = 0; i < graph.size(); i++) {
            ArrayList<Integer> nodeRow = graph.get(i);
            if (nodeRow != null) {
                for (Integer j : nodeRow) {
                    int index = nodeRow.indexOf(j);
                    adjacencyMatrix[i][compressionMap.get(j)] = costs.get(i).get(index);
                    nodeRow.set(index, compressionMap.get(j));
                }
            }
        }
    }

    private void setAntiGraph(HashMap<Integer, Integer> compressionMap){
        int j = 0;
        for (ArrayList<Integer> nodeRow: nodesIn) {
            if (nodeRow != null) {
                for (Integer i : nodeRow) {
                    int index = nodeRow.indexOf(i);
                    nodeRow.set(index, compressionMap.get(i));
                }
            }
        }
    }
}
