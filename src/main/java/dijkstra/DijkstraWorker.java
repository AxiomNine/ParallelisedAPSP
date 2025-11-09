package dijkstra;

import simulator.ParallelWorker;

import java.util.PriorityQueue;
import simulator.Path;

public class DijkstraWorker extends ParallelWorker {

    private final PriorityQueue<Path> pq = new PriorityQueue<Path>();
    private int origin;

    public DijkstraWorker(int i, int j) {
        super(i, j);
    }
    @Override
    public void run() {
    }
}
