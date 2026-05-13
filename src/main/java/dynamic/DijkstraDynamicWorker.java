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
        timerUnit.addToTimer(row, column, 10);
        while (!pq.isEmpty() && !sinksLeft.isEmpty()) {
            timerUnit.addToTimer(row, column, 0.5);
            PathElement uElement = pq.poll();
            timerUnit.addToTimer(row, column, Math.log(1 + pq.size())/Math.log(2));
            int u = uElement.getHead();
            timerUnit.addToTimer(row, column, 0.5);
            HashMap<Integer, Double> neighbours = cache.getNeighbours(u);
            timerUnit.addToTimer(row, column, neighbours.size() + 0.5);
            for (Integer v : neighbours.keySet()) {
                timerUnit.addToTimer(row, column, 0.5);
                double alt = uElement.getLength() + neighbours.get(v);
                timerUnit.addToTimer(row, column, 3);
                if (alt < cache.getCurrentCost(origin, v)) {
                    timerUnit.addToTimer(row, column, 1);
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
                    timerUnit.addToTimer(row, column, 11);
                    sinksLeft.remove(v);
                }
            }
        }
    }
}
