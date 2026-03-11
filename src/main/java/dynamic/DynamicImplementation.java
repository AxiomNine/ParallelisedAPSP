package dynamic;

import base.MainCore;
import simulator.SimulatedCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DynamicImplementation {
    private final MainCore core;
    private final ArrayList<ArrayList<Double>>  costsIn;
    private final ArrayList<ArrayList<Double>> costsOut;
    private final ArrayList<ArrayList<Integer>> nodesIn;
    private final ArrayList<ArrayList<Integer>> nodesOut;

    public DynamicImplementation(MainCore core) {
        this.core = core;
        nodesIn = core.getNodesIn();
        nodesOut = core.getNodesOut();
        costsIn = core.getCostsIn();
        costsOut = core.getCostsOut();
    }

    public void removeEdge(int u, int v){
        if (core.getAdjacencyMatrix()[u][v] != Double.POSITIVE_INFINITY){
            core.getAdjacencyMatrix()[u][v] = Double.POSITIVE_INFINITY;
            ArrayList<Integer> inList = nodesIn.get(v);
            int inListIndex = inList.indexOf(u);
            inList.remove(inListIndex);
            costsIn.get(v).remove(inListIndex);
            ArrayList<Integer> outList = nodesOut.get(u);
            int outListIndex = outList.indexOf(v);
            outList.remove(outListIndex);
            costsOut.get(u).remove(outListIndex);
        }
    }

    public void insertOrUpdateEdge(int u, int v, double c){
        if (core.getAdjacencyMatrix()[u][v] == Double.POSITIVE_INFINITY){
            core.getAdjacencyMatrix()[u][v] = c;
            ArrayList<Integer> outList = nodesOut.get(u);
            int outListIndex = outList.indexOf(v);
            ArrayList<Integer> inList = nodesIn.get(v);
            int inListIndex = inList.indexOf(u);
            if (outList.contains(v)){
                costsIn.get(v).set(inListIndex, c);
                costsOut.get(u).set(outListIndex, c);
            } else {
                inList.add(u);
                costsIn.get(v).add(c);
                outList.add(v);
                costsOut.get(u).add(c);
            }
        }
    }

    public void getSourcesAndSinks(int u, int v){
        HashSet<Integer> sources = getTransitiveClosure(nodesIn, u);;
        HashSet<Integer> sinks = getTransitiveClosure(nodesOut, v);

    }
    public HashSet<Integer> getTransitiveClosure(ArrayList<ArrayList<Integer>> graph, int u){
        HashSet<Integer> set = new HashSet<Integer>();
        HashSet<Integer> workSet = new HashSet<Integer>();
        workSet.add(u);
        Iterator<Integer> it = workSet.iterator();
        while (it.hasNext()){
            int v = it.next();
            ArrayList<Integer> nodeRow = graph.get(v);
            set.add(v);
            if (!nodeRow.isEmpty()){
                for (int w : nodeRow){
                    if (!set.contains(w)){
                        workSet.add(w);
                    }
                }
            }
            it.remove();
        }
        return set;
    }
}
