package simulator;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class FloydWarshallWorker extends ParallelWorker<Double> {

    private final ArrayBlockingQueue<Double> westChannel;
    private final ArrayBlockingQueue<Double> northChannel;
    private final ArrayBlockingQueue<Double> eastChannel;
    private final ArrayBlockingQueue<Double> southChannel;
    public FloydWarshallWorker(int i, int j, ArrayBlockingQueue<Double> p, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache) {
        super(i, j, p, cache);
        westChannel = w;
        northChannel = n;
        eastChannel = e;
        southChannel = s;
    }
    @Override
    public abstract void run();

    protected void writeWest(double val) {
        try {
            westChannel.put(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void writeNorth(double val) {
        try {
            northChannel.put(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected double readEast() {
        double retVal = 0.0;
        try {
            retVal = eastChannel.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return retVal;
    }
    protected double readSouth() {
        double retVal = 0.0;
        try {
            retVal = southChannel.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return retVal;
    }
}
