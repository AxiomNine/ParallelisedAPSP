package fox;

import java.util.concurrent.ArrayBlockingQueue;
import simulator.FloydWarshallWorker;
import simulator.SimulatedCache;

public class FoxWorker extends FloydWarshallWorker {

    public FoxWorker(int i, int j, int l, ArrayBlockingQueue<Double> downChannel, ArrayBlockingQueue<Double> upChannel, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache){
        super(i, j, l, downChannel, upChannel, w, n, e, s, cache);
    }
    public void run() {
        try {
            while (true) {
                Double xVal = readDown();
                Double yVal = readDown();
                Double witness = readDown();
                Double runningVal = Double.POSITIVE_INFINITY;
                Double broadcastVal;
                Double broadcastWitness;
                Double finalWitness = witness;
                for (int i = 0; i < getTorusLength(); i++) {
                    if (Math.floorMod(column - row, getTorusLength()) == i) {
                        writeWest(xVal);
                        broadcastVal = readEast();
                        writeWest(witness);
                        broadcastWitness = readEast();
                    } else {
                        broadcastVal = readEast();
                        writeWest(broadcastVal);
                        broadcastWitness = readEast();
                        writeWest(broadcastWitness);
                    }
                    Double newVal = (broadcastVal * 100000 + yVal * 100000) / 100000;
                    if (newVal < runningVal) {
                        runningVal = newVal;
                        finalWitness = broadcastWitness;
                    }
                    writeNorth(yVal);
                    yVal = readSouth();
                }
                writeUp(runningVal);
                writeUp(finalWitness);
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
