package dynamic;

import base.MainCore;
import java.util.*;

public class DynamicImplementation {

    private class Edge {
        public int v;
        public int w;
        Edge(int v, int w){
            this.v = v;
            this.w = w;
        }
        public boolean is(Edge e){
            return this.v == e.v && this.w == e.w;
        }
    }
    private MainCore core;
    private final ArrayList<ArrayList<Double>> costsIn;
    private final ArrayList<ArrayList<Double>> costsOut;
    private final ArrayList<ArrayList<Integer>> nodesIn;
    private final ArrayList<ArrayList<Integer>> nodesOut;

    private int totalEdgeCount;

    public DynamicImplementation(MainCore core, int changes, String typeOfChange) {
        this.core = core;
        nodesIn = core.getNodesIn();
        nodesOut = core.getNodesOut();
        costsIn = core.getCostsIn();
        costsOut = core.getCostsOut();
        totalEdgeCount = 0;
        for (ArrayList<Integer> node : nodesOut){
            totalEdgeCount += node.size();
        }
        makeGraphDynamic(changes, typeOfChange);
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
        } else {
            throw(new IndexOutOfBoundsException() );
        }
    }

    public void insertEdge(int u, int v, double c){
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
    private boolean shortestPath(int a, int b, int c, boolean invert){
        if (invert){
            return core.getCurrentCost(c, a) == core.getCurrentCost(c, b) + core.getAdjacencyMatrix()[b][a] && core.getCurrentCost(c, a) != Double.POSITIVE_INFINITY;
        }
        return core.getCurrentCost(a, c) == core.getCurrentCost(b, c) + core.getAdjacencyMatrix()[a][b] && core.getCurrentCost(a, c) != Double.POSITIVE_INFINITY;
    }
    public HashSet<Integer> getAffectedFromDelete(ArrayList<ArrayList<Integer>> graph, ArrayList<ArrayList<Integer>> antiGraph, int v, int w, boolean invert){
        HashSet<Integer> affected = new HashSet<Integer>();
        HashSet<Integer> workSet = new HashSet<Integer>();
        workSet.add(v);
        while (!workSet.isEmpty()){
            Iterator<Integer> it = workSet.iterator();
            int u = it.next();
            it.remove();
            affected.add(u);
            for (int x: antiGraph.get(u)){
                if (shortestPath(x, u, w, invert)) {
                    boolean addToWorkSet = true;
                    for (int y : graph.get(x)) {
                        if (shortestPath(x, y, w, invert)) {
                            addToWorkSet = addToWorkSet && affected.contains(y);
                        }
                    }
                    if (addToWorkSet){
                        workSet.add(x);
                    }
                }
            }
        }
        return affected;
    }
    public HashSet<Integer> getAffectedFromInsert(ArrayList<ArrayList<Integer>> antiGraph, int v, int w, boolean invert){
        HashSet<Integer> visited = new HashSet<Integer>();
        HashSet<Integer> affected = new HashSet<Integer>();
        HashSet<Edge> workSet = new HashSet<Edge>();
        HashMap<Edge, Double> editedCosts = new HashMap<Edge, Double>();
        workSet.add(new Edge(v, w));
        visited.add(v);

        while (!workSet.isEmpty()){
            Iterator<Edge> it = workSet.iterator();
            Edge edge = it.next();
            it.remove();
            if (invert ? core.getAdjacencyMatrix()[edge.w][edge.v] + getCost(editedCosts, w, edge.w) < getCost(editedCosts, w, edge.v) : core.getAdjacencyMatrix()[edge.v][edge.w] + getCost(editedCosts, edge.w, w) < getCost(editedCosts, edge.v, w)){
                affected.add(edge.v);
                if (invert){
                    editedCosts.put(new Edge(w, edge.v), core.getAdjacencyMatrix()[edge.w][edge.v] + getCost(editedCosts, w, edge.w));
                } else {
                    editedCosts.put(new Edge(edge.v, w), core.getAdjacencyMatrix()[edge.v][edge.w] + getCost(editedCosts, edge.w, w));
                }
                for (int y: antiGraph.get(edge.v)){
                    if (shortestPath(y, edge.v, v, invert) && !visited.contains(y)){
                        workSet.add(new Edge(y, edge.v));
                        visited.add(y);
                    }
                }
            }
        }
        return affected;
    }
    private double getCost(HashMap<Edge, Double> editedCosts, int u, int v){
        for (Edge edge : editedCosts.keySet()){
            if (edge.is(new Edge(u, v))){
                return editedCosts.get(edge);
            }
        }
        return core.getCurrentCost(u, v);
    }

    private Edge selectRandomEdge(){
        Random random = new Random();
        int edgeIndex = random.nextInt(0, totalEdgeCount);
        Iterator<ArrayList<Integer>> iterator = nodesOut.iterator();
        ArrayList<Integer> node = iterator.next();
        int v = 0;
        while (edgeIndex >= node.size()){
            edgeIndex -= node.size();
            node = iterator.next();
            v++;
        }
        int w = node.get(edgeIndex);
        return new Edge(v, w);
    }
    public void makeGraphDynamic(int changes, String typeOfChange) {
        Random random = new Random();
        HashSet<Integer> sources = new HashSet<Integer>();
        HashSet<Integer> sinks = new HashSet<Integer>();
        HashMap<Edge, Double> deadEdges = new HashMap<Edge, Double>();
        int edges = totalEdgeCount;
        if (typeOfChange.equals("insert")){
            for (int i = 0; i < Math.floor(edges * 0.05); i++){
                Edge e = selectRandomEdge();
                deadEdges.put(e, core.getAdjacencyMatrix()[e.v][e.w]);
                removeEdge(e.v, e.w);
                sinks.addAll(getAffectedFromDelete(nodesIn, nodesOut, e.w, e.v, true));
                sources.addAll(getAffectedFromDelete(nodesOut, nodesIn, e.v, e.w, false));
                totalEdgeCount -= 1;
            }
            core = new DijkstraDynamicMainCore(core, sources, sinks);
            sources.clear();
            sinks.clear();
        }
        Iterator<Edge> edgeIterator = deadEdges.keySet().iterator();
        for (int i = 0; i < changes; i++) {
            if (typeOfChange.equals("delete") || typeOfChange.equals("reweight")) {
                int edgeIndex = random.nextInt(0, totalEdgeCount);
                Iterator<ArrayList<Integer>> iterator = nodesOut.iterator();
                ArrayList<Integer> node = iterator.next();
                int v = 0;
                while (edgeIndex >= node.size()){
                    edgeIndex -= node.size();
                    node = iterator.next();
                    v++;
                }
                int w = node.get(edgeIndex);
                double c = costsOut.get(v).get(edgeIndex);
                removeEdge(v, w);
                sinks.addAll(getAffectedFromDelete(nodesIn, nodesOut, w, v, true));
                sources.addAll(getAffectedFromDelete(nodesOut, nodesIn, v, w, false));
                if (typeOfChange.equals("reweight")) {
                    c += random.nextGaussian(c, c/3);
                    insertEdge(v, w, c);
                    sinks.addAll(getAffectedFromInsert(nodesOut, w, v, true));
                    sources.addAll(getAffectedFromInsert(nodesIn, v, w, false));
                } else {
                    totalEdgeCount -= 1;
                }
            } else if (typeOfChange.equals("insert")){
                Edge edge = edgeIterator.next();
                int v = edge.v;
                int w = edge.w;
                double c = deadEdges.get(edge);
                edgeIterator.remove();
                insertEdge(v, w, c);
                sinks.addAll(getAffectedFromInsert(nodesOut, w, v, true));
                sources.addAll(getAffectedFromInsert(nodesIn, v, w, false));
                totalEdgeCount += 1;
            } else {
                throw new IllegalArgumentException("For the dynamic parameter, please select either \"insert\", \"delete\" or \"reweight\". Thank you.");
            }
        }
        core = new DijkstraDynamicMainCore(core, sources, sinks);
    }
}