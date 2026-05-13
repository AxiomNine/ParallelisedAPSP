package dynamic;

import base.MainCore;
import dijkstra.DijkstraMainCore;
import dijkstra.DijkstraWorker;
import metrics.TimerUnit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class DijkstraDynamicMainCore extends DijkstraMainCore {
    public DijkstraDynamicMainCore(MainCore core, HashSet<Integer> sources, HashSet<Integer> sinks){
        super(core);
        run(sources, sinks);
    }

    private void run(HashSet<Integer> sources, HashSet<Integer> sinks) {
        try {
            TimerUnit.getTimerUnit(getTorusLength()).synchroniseZero();
            for (int i = 0; i < getTorusLength(); i++) {
                for (int j = 0; j < getTorusLength(); j++) {
                    this.coreDownChannels[i][j] = new ArrayBlockingQueue<Integer>(1);
                    this.coreWorkers[i][j] = new DijkstraDynamicWorker(i, j, getTorusLength(), this.coreDownChannels[i][j], cache, this, sinks);
                    Thread core = new Thread(coreWorkers[i][j]);
                    this.coreThreads[i][j] = core;
                    core.start();
                }
            }
            for (int source : sources){
                for (int sink : sinks) {
                    cache.writeVal(source, sink, Double.POSITIVE_INFINITY, -1);
                }
            }
            Iterator<Integer> it = sources.iterator();

            int s = sources.size();
            for (int i = 0; i < Math.min(getTorusLength() * getTorusLength(), s); i++) {
                int source = it.next();
                coreDownChannels[i/getTorusLength()][i % getTorusLength()].put(source);
                it.remove();
            }

            synchronized (this) {
                while (it.hasNext()) {
                    if (!pausedQueueIsEmpty()) {
                        DijkstraWorker worker = pausedQueuePop();
                        worker.getDownChannel().put(it.next());
                        it.remove();
                    } else {
                        this.wait();
                    }
                }
            }

            int coreCount = 0;

            synchronized (this) {
                while (coreCount < Math.min(getTorusLength()*getTorusLength(), s)) {
                    if (!pausedQueueIsEmpty()) {
                        DijkstraWorker worker = pausedQueuePop();
                        int threadPos = worker.getPosition();
                        coreThreads[threadPos / getTorusLength()][threadPos % getTorusLength()].interrupt();
                        coreCount++;
                    } else {
                        this.wait();
                    }
                }
                if (getTorusLength()*getTorusLength() > s) {
                    for (int i = s; i < getTorusLength()*getTorusLength(); i++) {
                        coreThreads[i / getTorusLength()][i % getTorusLength()].interrupt();
                    }
                }
            }

            getElapsedTimeOfCores();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
