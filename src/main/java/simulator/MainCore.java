package simulator;

import cannon.CannonWorker;
import dijkstra.DijkstraWorker;
import fox.FoxWorker;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class MainCore<T extends Number> {

    protected final SimulatedCache cache;
    protected final Thread[][] coreThreads;
    protected final ParallelWorker<T>[][] coreWorkers;

    protected final ArrayBlockingQueue<T>[][] coreDownChannels;
    public MainCore(String fileAddress, int torusLength){
        cache = new SimulatedCache(fileAddress);
        coreThreads = new Thread[torusLength][torusLength];
        coreWorkers = new ParallelWorker[torusLength][torusLength];
        coreDownChannels = new ArrayBlockingQueue[torusLength][torusLength];
    }
    public abstract void execute();
}
