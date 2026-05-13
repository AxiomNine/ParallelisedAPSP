package dijkstra;

import base.ParallelWorker;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import metrics.TimerUnit;
import utils.PathElement;
import simulator.SimulatedCache;

public class DijkstraWorker extends ParallelWorker<Integer> {
    protected final PriorityQueue<PathElement> pq = new PriorityQueue<PathElement>();

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
                timerUnit.addToTimer(row, column, 0.5);
                singleDijkstra(origin);
                timerUnit.addToTimer(row, column, 0.5);
                mainCore.signalReady(this);
            }
        } catch (InterruptedException e){
            return;
        }
    }

    public ArrayBlockingQueue<Integer> getDownChannel() {
        return downChannel;
    }

    protected void singleDijkstra(int origin) {
        PathElement p = new PathElement(origin);
        pq.add(p);
        timerUnit.addToTimer(row, column, 10);
        while (!pq.isEmpty()) {
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
                }
            }
        }
    }
}
