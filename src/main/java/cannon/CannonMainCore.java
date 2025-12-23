package cannon;

import simulator.Channel;
import simulator.MainCore;

import java.util.concurrent.ArrayBlockingQueue;

public class CannonMainCore extends MainCore {
    public CannonMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        ArrayBlockingQueue<Double>[][] hchannels = new ArrayBlockingQueue[torusLength][torusLength];
        ArrayBlockingQueue<Double>[][] vchannels = new ArrayBlockingQueue[torusLength][torusLength];
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                hchannels[i][j] = new ArrayBlockingQueue<Double>(1);
                vchannels[i][j] = new ArrayBlockingQueue<Double>(1);
            }
        }
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                coreThreads[i][j] = new Thread(new CannonWorker(i, j, new ArrayBlockingQueue<Double>(1), hchannels[i][j], vchannels[i][(j + 1) % torusLength], hchannels[(j + 1) % torusLength][j], vchannels[i][j], cache));
            }
        }
    }
    @Override
    public void execute() {

    }
}
