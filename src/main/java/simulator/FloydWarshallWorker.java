package simulator;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class FloydWarshallWorker extends ParallelWorker<Double> {

    private final ArrayBlockingQueue<Double> westChannel;
    private final ArrayBlockingQueue<Double> northChannel;
    private final ArrayBlockingQueue<Double> eastChannel;
    private final ArrayBlockingQueue<Double> southChannel;
    private final ArrayBlockingQueue<Double> upChannel;
    public FloydWarshallWorker(int i, int j, int l, ArrayBlockingQueue<Double> downChannel, ArrayBlockingQueue<Double> upChannel, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache) {
        super(i, j, l, downChannel, cache);
        westChannel = w;
        northChannel = n;
        eastChannel = e;
        southChannel = s;
        this.upChannel = upChannel;
    }
    @Override
    public abstract void run();

    protected void writeWest(Double val) throws InterruptedException {
        westChannel.put(val);
    }
    protected void writeNorth(Double val) throws InterruptedException {
        northChannel.put(val);
    }
    protected Double readEast() throws InterruptedException {
        return eastChannel.take();
    }
    protected Double readSouth() throws InterruptedException {
        return southChannel.take();
    }
    protected void writeUp(Double input) throws InterruptedException {
        upChannel.put(input);
    }
}
