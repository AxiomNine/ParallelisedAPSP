package cannon;

import simulator.Channel;
import simulator.FloydWarshallWorker;

public class CannonWorker extends FloydWarshallWorker {

    public CannonWorker(int i, int j, Channel w, Channel n, Channel e, Channel s){
        super(i, j, w, n, e, s);
    }
    public void run(){

    }
}
