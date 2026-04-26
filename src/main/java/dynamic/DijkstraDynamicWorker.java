package dynamic;

import dijkstra.DijkstraMainCore;
import dijkstra.DijkstraWorker;
import simulator.SimulatedCache;
import utils.PathElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class DijkstraDynamicWorker extends DijkstraWorker {

    private HashSet<Integer> sinks;
    private HashSet<Integer> sinksLeft;
    public DijkstraDynamicWorker(int i, int j, int l, ArrayBlockingQueue<Integer> downChannel, SimulatedCache cache, DijkstraMainCore mainCore, HashSet<Integer> sinks){
        super(i, j, l, downChannel, cache, mainCore);
        this.sinks = sinks;
        sinksLeft = new HashSet<>(sinks);
    }

    @Override
    protected void singleDijkstra(int origin) {
        sinksLeft = new HashSet<>(sinks);
        PathElement p = new PathElement(origin);
        pq.add(p);
        while (!pq.isEmpty() && !sinksLeft.isEmpty()) {
            PathElement uElement = pq.poll();
            int u = uElement.getHead();
            HashMap<Integer, Double> neighbours = cache.getNeighbours(u);
            for (Integer v : neighbours.keySet()) {
                double alt = uElement.getLength() + neighbours.get(v);
                if (alt < cache.getCurrentCost(origin, v)) {
                    PathElement vElement = new PathElement(v, alt);
                    Iterator<PathElement> i = pq.iterator();
                    boolean changed = false;
                    while (i.hasNext() && !changed) {
                        PathElement potentialElement = i.next();
                        if (potentialElement.getHead() == v) {
                            changed = true;
                            pq.remove(potentialElement);
                        }
                    }
                    pq.add(vElement);
                    cache.writeVal(origin, v, vElement.getLength(), u);
                    sinksLeft.remove(v);
                }
            }
        }
    }
}
