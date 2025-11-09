package simulator;

import cannon.CannonWorker;
import dijkstra.DijkstraWorker;
import fox.FoxWorker;

public abstract class MainCore {

    protected SimulatedCache cache;
    protected ParallelWorker[][] cores;

    public MainCore(String fileAddress, int torusLength){
        cache = new SimulatedCache(fileAddress);
        cores = new ParallelWorker[torusLength][torusLength];
    }
    public abstract void execute();
}
