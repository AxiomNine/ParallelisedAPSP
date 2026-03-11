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
    protected MainCore(MainCore<T> core){
        cache = core.cache;
        coreThreads = core.coreThreads;
        coreWorkers = core.coreWorkers;
        coreDownChannels = core.coreDownChannels;
    }
    public MainCore(String fileAddress, int torusLength){
        cache = new CompressedCache(fileAddress);
        coreThreads = new Thread[torusLength][torusLength];
        coreWorkers = new ParallelWorker[torusLength][torusLength];
        coreDownChannels = new ArrayBlockingQueue[torusLength][torusLength];
    }

    public void getElapsedTimeOfCores(){
        double totalTime = TimerUnit.getTimerUnit(0).returnMaxTime();
        System.out.printf("%f", totalTime);
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
        public Double[][] getAdjacencyMatrix() {
            return cache.getAdjacencyMatrix();
        }
    }

