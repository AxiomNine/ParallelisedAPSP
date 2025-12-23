package simulator;

import org.example.Main;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class ParallelWorker<T extends Number> implements Runnable {

    protected final int xOrdinate;
    protected final int yOrdinate;
    protected final SimulatedCache cache;
    protected final ArrayBlockingQueue<T> mainChannel;
    protected volatile boolean workDone = false;
    public ParallelWorker(int i, int j, ArrayBlockingQueue<T> mainChannel, SimulatedCache cache){
        xOrdinate = i;
        yOrdinate = j;
        this.cache = cache;
        this.mainChannel = mainChannel;
    }
    @Override
    public abstract void run();

    public void destroy() {
        workDone = false;
    }

    private double readFromMemory(int x, int y){
        return cache.readVal(x, y);
    }

    private void writeToMemory(int x, int y, Path p){
        cache.writeVal(x, y, p);
    }
}
