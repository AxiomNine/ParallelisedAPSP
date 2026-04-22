package base;

import simulator.SimulatedCache;
import utils.Message;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class MatMulWorker extends ParallelWorker<Boolean> {

    private final ArrayBlockingQueue<Message> westChannel;
    private final ArrayBlockingQueue<Message> northChannel;
    private final ArrayBlockingQueue<Message> eastChannel;
    private final ArrayBlockingQueue<Message> southChannel;
    private final ArrayBlockingQueue<Boolean> upChannel;
    private final int blockCount;
    protected double q;
    protected int outPred;
    public MatMulWorker(int i, int j, int l, ArrayBlockingQueue<Boolean> downChannel, ArrayBlockingQueue<Boolean> upChannel, ArrayBlockingQueue<Message> w, ArrayBlockingQueue<Message> n, ArrayBlockingQueue<Message> e, ArrayBlockingQueue<Message> s, SimulatedCache cache, int blockCount) {
        super(i, j, l, downChannel, cache);
        westChannel = w;
        northChannel = n;
        eastChannel = e;
        southChannel = s;
        this.upChannel = upChannel;
        this.blockCount = blockCount;
        q = -1.0;
        outPred = -1;
    }
    @Override
    public abstract void run();

    protected void writeWest(Message val) throws InterruptedException {
        westChannel.put(val);
    }
    protected void writeNorth(Message val) throws InterruptedException {
        northChannel.put(val);
    }
    protected Message readEast() throws InterruptedException {
        return eastChannel.take();
    }
    protected Message readSouth() throws InterruptedException {
        return southChannel.take();
    }
    protected void writeUp(Boolean input) throws InterruptedException {
        upChannel.put(input);
    }

    protected abstract void singleMM(double xVal, double yVal, int witness) throws InterruptedException;
    protected int getBlockCount(){ return blockCount; }
}
