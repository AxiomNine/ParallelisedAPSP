package cannon;

import simulator.Channel;
import simulator.FloydWarshallWorker;
import simulator.SimulatedCache;

import java.util.concurrent.ArrayBlockingQueue;

public class CannonWorker extends FloydWarshallWorker {

    public CannonWorker(int i, int j, ArrayBlockingQueue<Double> p, ArrayBlockingQueue<Double> w, ArrayBlockingQueue<Double> n, ArrayBlockingQueue<Double> e, ArrayBlockingQueue<Double> s, SimulatedCache cache){
        super(i, j, p, w, n, e, s, cache);
    }
    public void run(){

    }
}
