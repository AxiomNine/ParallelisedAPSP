package compressor;

import simulator.SimulatedCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

public class GraphCompressor {

    private final SimulatedCache cache;
    private ArrayList<ArrayList<Integer>> nodesIn;
    private ArrayList<ArrayList<Double>> costsIn;
    private ArrayList<ArrayList<Integer>> nodesOut;
    private ArrayList<ArrayList<Double>> costsOut;
    private final HashMap<Integer, Integer> compressionMap;

    private int nodeCount;

    public GraphCompressor(CompressedCache c, ArrayList<ArrayList<Integer>> nodesIn, ArrayList<ArrayList<Double>> costsIn, ArrayList<ArrayList<Integer>> nodesOut, ArrayList<ArrayList<Double>> costsOut) {
        cache = c;
        this.nodesOut = nodesOut;
        this.costsOut = costsOut;
        this.nodesIn = nodesIn;
        this.costsIn = costsIn;
        compressionMap = new HashMap<>();
        nodeCount = 0;
    }

    public void compress() {
        try {
            int oldNumbering = 0;
            int newNumbering = 0;
            ArrayList<Integer> listToCheck = new ArrayList<>();
            Iterator<ArrayList<Integer>> nodesOutIterator = nodesOut.iterator();
            Iterator<ArrayList<Integer>> nodesInIterator = nodesIn.iterator();
            Iterator<ArrayList<Double>> costsOutIterator = costsOut.iterator();
            Iterator<ArrayList<Double>> costsInIterator = costsIn.iterator();

            for (int i = 0; i < nodesOut.size(); i++) {
                int j = i;
                while (!listToCheck.isEmpty()) {
                    int possibleCheck = listToCheck.removeFirst();
                    if (possibleCheck < j) {
                        i = possibleCheck;
                        break;
                    }
                }
                ArrayList<Integer> currentNodesOut = nodesOut.get(i);
                ArrayList<Integer> currentNodesIn = nodesIn.get(i);
                if (currentNodesOut != null && nodesIn.get(i) != null) {
                    if (currentNodesOut.size() == 1 && currentNodesIn.size() == 1) {
                        int nodeFrom = currentNodesIn.getFirst();
                        int nodeTo = currentNodesOut.getFirst();
                        ArrayList<Integer> nodesOutFrom = nodesOut.get(nodeFrom);
                        int indexFrom = nodesOutFrom.indexOf(i);
                        if (nodeFrom == nodeTo) {
                            nodesOutFrom.remove(indexFrom);
                            costsOut.get(nodeFrom).remove(indexFrom);
                            if (nodesOutFrom.size() == 1 && nodesIn.get(nodeFrom).size() == 1) {
                                listToCheck.add(nodeFrom);
                            }
                        } else {
                            ArrayList<Integer> nodesInTo = nodesIn.get(nodeTo);
                            ArrayList<Double> costsInTo = costsIn.get(nodeTo);
                            ArrayList<Double> costsOutFrom = costsOut.get(nodeFrom);
                            int indexTo = nodesInTo.indexOf(i);
                            double newCost = costsOutFrom.get(indexFrom) + costsInTo.get(indexTo);
                            if (nodesOutFrom.contains(nodeTo)) {
                                int indexFromTo = nodesOutFrom.indexOf(nodeTo);
                                int indexToFrom = nodesInTo.indexOf(nodeFrom);
                                double altCost = costsOutFrom.get(indexFromTo);
                                costsOutFrom.set(indexFromTo, Math.min(newCost, altCost));
                                costsInTo.set(indexToFrom, Math.min(newCost, altCost));
                                nodesOutFrom.remove(indexFrom);
                                nodesInTo.remove(indexTo);
                                costsOut.get(nodeFrom).remove(indexFrom);
                                costsInTo.remove(indexTo);
                                if (nodesOutFrom.size() == 1 && nodesIn.get(nodeFrom).size() == 1) {
                                    listToCheck.add(nodeFrom);
                                }
                                if (nodesOut.get(nodeTo).size() == 1 && nodesInTo.size() == 1) {
                                    listToCheck.add(nodeTo);
                                }
                            } else {
                                nodesOutFrom.set(indexFrom, nodeTo);
                                costsOutFrom.set(indexFrom, newCost);
                                nodesInTo.set(indexTo, nodeFrom);
                                costsInTo.set(indexTo, newCost);
                            }
                        }
                        nodesOut.set(i, null);
                        nodesIn.set(i, null);
                        costsOut.set(i, null);
                        costsIn.set(i, null);
                    }
                }
                if (i != j) {
                    i = j - 1;
                }
            }
            while (nodesOutIterator.hasNext()) {
                ArrayList<Integer> nodesOutList = nodesOutIterator.next();
                ArrayList<Integer> nodesInList = nodesInIterator.next();
                costsOutIterator.next();
                costsInIterator.next();
                if (nodesOutList == null && nodesInList == null) {
                    nodesOutIterator.remove();
                    nodesInIterator.remove();
                    costsOutIterator.remove();
                    costsInIterator.remove();
                } else {
                    compressionMap.put(oldNumbering, newNumbering);
                    newNumbering++;
                }
                oldNumbering++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Integer>> getGraph(){ return nodesOut;}

    public ArrayList<ArrayList<Double>> getCosts() { return costsOut;}

    public HashMap<Integer, Integer> getCompressionMap() { return compressionMap;}
}