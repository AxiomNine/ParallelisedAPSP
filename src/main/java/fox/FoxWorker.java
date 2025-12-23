package fox;

import java.util.concurrent.ArrayBlockingQueue;
import simulator.FloydWarshallWorker;
import simulator.SimulatedCache;

public class FoxWorker extends FloydWarshallWorker {

    public FoxWorker(int i, int j, ArrayBlockingQueue<Double> p, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache){
        super(i, j, p, w, n, e, s, cache);
    }
    public void run() {
        try {
            while (true) {
                double xVal = mainChannel.take();
                double yVal = mainChannel.take();
                double runningVal = Double.POSITIVE_INFINITY;
                for (int i = 0; i < cache.getSize(); i++) {
                    if (yOrdinate - xOrdinate % cache.getSize() == i) {
                        writeWest(xVal);
                        readEast();
                    } else {
                        xVal = readEast();
                        writeWest(xVal);
                    }
                    runningVal = Math.min(runningVal, (xVal * 100000 + yVal * 100000) / 100000);
                    writeNorth(yVal);
                    yVal = readSouth();
                }
                mainChannel.put(runningVal);
            }
        } catch (InterruptedException e) {
            return;
        }
    }


}
