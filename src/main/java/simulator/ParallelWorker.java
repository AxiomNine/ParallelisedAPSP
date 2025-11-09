package simulator;

public abstract class ParallelWorker implements Runnable {

    protected int xOrdinate;
    protected int yOrdinate;
    protected SimulatedCache cache;
    public ParallelWorker(int i, int j){
        xOrdinate = i;
        yOrdinate = j;
    }
    @Override
    public abstract void run();

    private double readFromMemory(int x, int y){
        return cache.readVal(x, y);
    }

    private void writeToMemory(int x, int y, double val){
        cache.writeVal(x, y, val);
    }
}
