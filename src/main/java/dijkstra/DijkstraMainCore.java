package dijkstra;

import base.MainCore;
import metrics.TimerUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;

public class DijkstraMainCore extends MainCore<Integer> {
    private final List<DijkstraWorker> pausedQueueSync;
    public DijkstraMainCore(String fileAddress, int torusLength) {
        super(fileAddress, torusLength);
        int counter = 0;
        List<DijkstraWorker> pausedQueue = new ArrayList<DijkstraWorker>();
        pausedQueueSync = Collections.synchronizedList(pausedQueue);
        try {
            for (int i = 0; i < torusLength; i++) {
                for (int j = 0; j < torusLength; j++) {
                    this.coreDownChannels[i][j] = new ArrayBlockingQueue<Integer>(1);
                    this.coreWorkers[i][j] = new DijkstraWorker(i, j, torusLength, this.coreDownChannels[i][j], cache, this);
                    Thread core = new Thread(coreWorkers[i][j]);
                    this.coreThreads[i][j] = core;
                    core.start();
                }
            }

            for (int i = 0; i < Math.min(torusLength * torusLength, cache.getSize()); i++) {
                coreDownChannels[i/torusLength][i % torusLength].put(counter);
                counter++;
            }



            synchronized (this) {
                while (counter < cache.getSize()) {
                    if (!pausedQueueSync.isEmpty()) {
                        DijkstraWorker worker = pausedQueueSync.removeFirst();
                        worker.getDownChannel().put(counter);
                        counter++;
                    } else {
                        this.wait();
                    }
                }
            }

            int coreCount = 0;

            synchronized (this) {
                while (coreCount < Math.min(torusLength*torusLength, cache.getSize())) {
                    if (!pausedQueueSync.isEmpty()) {
                        DijkstraWorker worker = pausedQueueSync.removeFirst();
                        int threadPos = worker.getPosition();
                        coreThreads[threadPos / torusLength][threadPos % torusLength].interrupt();
                        coreCount++;
                    } else {
                        this.wait();
                    }
                }
                if (torusLength*torusLength > cache.getSize()) {
                    for (int i = cache.getSize(); i < torusLength*torusLength; i++) {
                        coreThreads[i / torusLength][i % torusLength].interrupt();
                    }
                }
            }

            for (int i = 0; i < cache.getSize(); i++) {
                for (int j = 0; j < cache.getSize(); j++) {
                    //cache.printDijkstraPathFrom(i, j);
                }
            }
            getElapsedTimeOfCores();
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
}
