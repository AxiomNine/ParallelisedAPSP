package simulator;

public abstract class FloydWarshallWorker extends ParallelWorker {

    private Channel westChannel;
    private Channel northChannel;
    private Channel eastChannel;
    private Channel southChannel;
    public FloydWarshallWorker(int i, int j, Channel w, Channel n, Channel e, Channel s) {
        super(i, j);
        westChannel = w;
        northChannel = n;
        eastChannel = e;
        southChannel = s;
    }
    @Override
    public abstract void run();

    protected void writeWest(double val) {
        westChannel.writeIn(val);
    }
    protected void writeNorth(double val) {
        northChannel.writeIn(val);
    }
    protected double readEast() {
        return eastChannel.readOut();
    }
    protected double readSouth() {
        return southChannel.readOut();
    }
}
