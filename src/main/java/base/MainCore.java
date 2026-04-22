package base;

import compressor.CompressedCache;
import metrics.TimerUnit;
import simulator.SimulatedCache;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class MainCore<T> {

    protected final SimulatedCache cache;
    protected final Thread[][] coreThreads;
    protected final ParallelWorker<T>[][] coreWorkers;
    protected final ArrayBlockingQueue<T>[][] coreDownChannels;
    private final int torusLength;
    protected MainCore(MainCore core){
        cache = core.cache;
        coreThreads = core.coreThreads;
        coreWorkers = core.coreWorkers;
        coreDownChannels = core.coreDownChannels;
        this.torusLength = core.torusLength;
    }
    public MainCore(String fileAddress, int torusLength){
        cache = new SimulatedCache(fileAddress);
        coreThreads = new Thread[torusLength][torusLength];
        coreWorkers = new ParallelWorker[torusLength][torusLength];
        coreDownChannels = new ArrayBlockingQueue[torusLength][torusLength];
        this.torusLength = torusLength;
    }

    public void getElapsedTimeOfCores(){
        double totalTime = TimerUnit.getTimerUnit(0).returnMaxTime();
        System.out.printf("%f\n", totalTime);
    }

    public ArrayList<ArrayList<Integer>> getNodesIn() {
        return cache.getNodesIn();
    }

    public ArrayList<ArrayList<Integer>> getNodesOut() {
        return cache.getNodesOut();
    }

    public ArrayList<ArrayList<Double>> getCostsIn() {
        return cache.getCostsIn();
    }

    public ArrayList<ArrayList<Double>> getCostsOut() {
        return cache.getCostsOut();
    }
    public double[][] getAdjacencyMatrix() {
        return cache.getAdjacencyMatrix();
    }
    public int getTorusLength(){
        return torusLength;
    }

    public Double getCurrentCost(int from, int to) {return cache.getCurrentCost(from, to); }
}

