package dijkstra;

import base.MainCore;
import metrics.TimerUnit;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;

public class DijkstraMainCore extends MainCore<Integer> {
    private final ArrayList<DijkstraWorker> pausedQueue;
    private final List<DijkstraWorker> pausedQueueSync;
    public DijkstraMainCore(MainCore core){
        super(core);
        pausedQueue = new ArrayList<DijkstraWorker>();
        pausedQueueSync = Collections.synchronizedList(pausedQueue);
    }
    public DijkstraMainCore(String fileAddress, int torusLength, String compress) {
        super(fileAddress, torusLength, compress);
        pausedQueue = new ArrayList<DijkstraWorker>();
        pausedQueueSync = Collections.synchronizedList(pausedQueue);
        run();
    }
    protected void run() {
        int counter = 0;
        try {
            for (int i = 0; i < getTorusLength(); i++) {
                for (int j = 0; j < getTorusLength(); j++) {
                    this.coreDownChannels[i][j] = new ArrayBlockingQueue<Integer>(1);
                    this.coreWorkers[i][j] = new DijkstraWorker(i, j, getTorusLength(), this.coreDownChannels[i][j], cache, this);
                    Thread core = new Thread(coreWorkers[i][j]);
                    this.coreThreads[i][j] = core;
                    core.start();
                }
            }

            for (int i = 0; i < Math.min(getTorusLength() * getTorusLength(), cache.getSize()); i++) {
                coreDownChannels[i/getTorusLength()][i % getTorusLength()].put(counter);
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
                while (coreCount < Math.min(getTorusLength()*getTorusLength(), cache.getSize())) {
                    if (!pausedQueueSync.isEmpty()) {
                        DijkstraWorker worker = pausedQueueSync.removeFirst();
                        int threadPos = worker.getPosition();
                        coreThreads[threadPos / getTorusLength()][threadPos % getTorusLength()].interrupt();
                        coreCount++;
                    } else {
                        this.wait();
                    }
                }
                if (getTorusLength()*getTorusLength() > cache.getSize()) {
                    for (int i = cache.getSize(); i < getTorusLength()*getTorusLength(); i++) {
                        coreThreads[i / getTorusLength()][i % getTorusLength()].interrupt();
                    }
                }
            }
            getElapsedTimeOfCores();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
    }

    protected void signalReady(DijkstraWorker worker) {
        synchronized (this) {
            pausedQueueSync.add(worker);
            this.notify();
        }
    }
    protected boolean pausedQueueIsEmpty(){
        return pausedQueueSync.isEmpty();
    }
    protected DijkstraWorker pausedQueuePop(){
        return pausedQueueSync.removeFirst();
    }
}
