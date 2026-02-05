package cannon;

import simulator.FloydWarshallWorker;
import simulator.SimulatedCache;

import java.util.concurrent.ArrayBlockingQueue;

public class CannonWorker extends FloydWarshallWorker {

    public CannonWorker(int i, int j, int l, ArrayBlockingQueue<Double> downChannel, ArrayBlockingQueue<Double> upChannel, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache){
        super(i, j, l, downChannel, upChannel, w, n, e, s, cache);
    }
    public void run(){
        try {
            while (true) {
                Double xVal = readDown();
                Double yVal = readDown();
                Double witness = readDown();
                Double runningVal = Double.POSITIVE_INFINITY;
                Double finalWitness = witness;
                for (int i = 0; i < getTorusLength(); i++) {
                    Double newVal = (xVal * 100000 + yVal * 100000) / 100000;
                    if (newVal < runningVal) {
                        runningVal = newVal;
                        finalWitness = witness;
                    }
                    writeNorth(yVal);
                    yVal = readSouth();
                    writeWest(xVal);
                    xVal = readEast();
                    writeWest(witness);
                    witness = readEast();
                }
                writeUp(runningVal);
                writeUp(finalWitness);
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
