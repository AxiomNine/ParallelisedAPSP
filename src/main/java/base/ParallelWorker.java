package base;

import metrics.TimerUnit;
import simulator.SimulatedCache;

import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class ParallelWorker<T> implements Runnable {

    protected final int row;
    protected final int column;
    protected final SimulatedCache cache;
    protected final ArrayBlockingQueue<T> downChannel;
    private final int torusLength;
    protected volatile boolean workDone = false;
    protected TimerUnit timerUnit;

    public ParallelWorker(int i, int j, int l, ArrayBlockingQueue<T> downChannel, SimulatedCache cache) {
        row = i;
        column = j;
        this.cache = cache;
        this.downChannel = downChannel;
        this.torusLength = l;
        timerUnit = TimerUnit.getTimerUnit(torusLength);
    }

    @Override
    public abstract void run();

    public void destroy() {
        workDone = false;
    }

    protected T readDown() throws InterruptedException {
        return downChannel.take();
    }

    protected int getTorusLength() {
        return torusLength;
    }
}
