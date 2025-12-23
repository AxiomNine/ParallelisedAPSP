package dijkstra;

import simulator.Channel;
import simulator.ParallelWorker;

import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import simulator.Path;
import simulator.SimulatedCache;

public class DijkstraWorker extends ParallelWorker<Integer> {

    private final PriorityQueue<Path> pq = new PriorityQueue<Path>();

    private final DijkstraMainCore mainCore;

    public DijkstraWorker(int i, int j, ArrayBlockingQueue<Integer> mainChannel, SimulatedCache cache, DijkstraMainCore mainCore) {
        super(i, j, mainChannel, cache);
        this.mainCore = mainCore;
    }
    @Override
    public void run() {
        try {
            while (true) {
                int origin = mainChannel.take();
                singleDijkstra(origin);
                mainCore.signalReady(this);
            }
        } catch (InterruptedException e){
            return;
        }
    }

    public ArrayBlockingQueue<Integer> getChannel() {
        return mainChannel;
    }

    private void singleDijkstra(int origin) {
        Path p = new Path();
        p.appendToPath(0, origin);
        pq.add(p);

        while (!pq.isEmpty()) {
            Path uPath = pq.poll();
            int u = uPath.getHead();

            HashMap<Integer, Double> neighbours = cache.getNeighboursAndPaths(u);
            for (Integer v : neighbours.keySet()) {
                double alt = uPath.getLength() + neighbours.get(v);
                if (alt < cache.getCurrentPath(origin, v).getLength()) {
                    Path vPath = new Path(uPath);
                    vPath.appendToPath(neighbours.get(v), v);
                    Iterator<Path> i = pq.iterator();
                    boolean changed = false;
                    while (i.hasNext() && !changed) {
                        Path potentialPath = i.next();
                        if (potentialPath.getHead() == v) {
                            changed = true;
                            pq.remove(potentialPath);
                        }
                    }
                    pq.add(vPath);
                    cache.writeVal(v, origin, vPath);
                }
            }
        }
        cache.printAllPaths(origin);
    }
}
