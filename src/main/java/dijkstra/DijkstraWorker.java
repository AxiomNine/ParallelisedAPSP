package dijkstra;

import simulator.ParallelWorker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import simulator.PathElement;
import simulator.SimulatedCache;

public class DijkstraWorker extends ParallelWorker<Integer> {

    private final PriorityQueue<PathElement> pq = new PriorityQueue<PathElement>();

    private final DijkstraMainCore mainCore;

    public int getPosition(){
        return row * getTorusLength() + column;
    }

    public DijkstraWorker(int i, int j, int l, ArrayBlockingQueue<Integer> downChannel, SimulatedCache cache, DijkstraMainCore mainCore) {
        super(i, j, l, downChannel, cache);
        this.mainCore = mainCore;
    }
    @Override
    public void run() {
        try {
            while (true) {
                int origin = readDown();
                singleDijkstra(origin);
                mainCore.signalReady(this);
            }
        } catch (InterruptedException e){
            return;
        }
    }

    public ArrayBlockingQueue<Integer> getDownChannel() {
        return downChannel;
    }

    private void singleDijkstra(int origin) {
        PathElement p = new PathElement(origin);
        pq.add(p);
        while (!pq.isEmpty()) {
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
                }
            }
        }
    }
}
