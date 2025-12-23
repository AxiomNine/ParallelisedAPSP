package dijkstra;

import simulator.MainCore;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;

public class DijkstraMainCore extends MainCore {
    private final List<DijkstraWorker> pausedQueueSync;
    public DijkstraMainCore(String fileAddress, int torusLength) {
        super(fileAddress, torusLength);
        int counter = 0;
        List<DijkstraWorker> pausedQueue = new ArrayList<DijkstraWorker>();
        pausedQueueSync = Collections.synchronizedList(pausedQueue);
        try {
            for (int i = 0; i < torusLength; i++) {
                for (int j = 0; j < torusLength; j++) {
                    this.coreChannels[i][j] = new ArrayBlockingQueue<Integer>(1);
                    this.coreWorkers[i][j] = new DijkstraWorker(i, j, this.coreChannels[i][j], cache, this);
                    Thread core = new Thread(coreWorkers[i][j]);
                    this.coreThreads[i][j] = core;
                    core.start();
                }
            }

            for (int i = 0; i < torusLength * torusLength; i++) {
                coreChannels[i / torusLength][i % torusLength].put(counter);
                counter++;
            }

            while (counter < cache.getSize()) {
                synchronized (this) {
                    this.wait();
                    DijkstraWorker worker = pausedQueueSync.removeFirst();
                    worker.getChannel().put(counter);
                    counter++;
                }
            }
            for (int i = 0; i < torusLength; i++) {
                for (int j = 0; j < torusLength; j++) {
                    coreWorkers[i][j].destroy();
                    coreThreads[i][j].interrupt();
                }
                cache.printAllPaths(i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void signalReady(DijkstraWorker worker) {
        synchronized (this) {
            pausedQueueSync.add(worker);
            this.notify();
        }
    }

    private void destroyCores() {

    }
    @Override
    public void execute() {
    }
}
