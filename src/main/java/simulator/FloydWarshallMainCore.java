package simulator;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class FloydWarshallMainCore extends MainCore {
    protected final ArrayBlockingQueue<Double>[][] hchannels;
    protected final ArrayBlockingQueue<Double>[][] vchannels;

    public FloydWarshallMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        hchannels = new ArrayBlockingQueue[torusLength][torusLength];
        vchannels = new ArrayBlockingQueue[torusLength][torusLength];
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                hchannels[i][j] = new ArrayBlockingQueue<Double>(1);
                vchannels[i][j] = new ArrayBlockingQueue<Double>(1);
            }
        }

    }
}
