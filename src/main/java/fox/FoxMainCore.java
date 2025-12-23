package fox;

import simulator.FloydWarshallMainCore;
import simulator.MainCore;
import java.util.concurrent.ArrayBlockingQueue;

public class FoxMainCore extends FloydWarshallMainCore {
    public FoxMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        for (int i = 0; i < torusLength; i++) {
            for (int j = 0; j < torusLength; j++) {
                coreChannels[i][j] = new ArrayBlockingQueue<Integer>(1);
                coreWorkers[i][j] = new FoxWorker(i, j, new ArrayBlockingQueue<Double>(1), hchannels[i][j], vchannels[i][(j + 1) % torusLength], hchannels[(j + 1) % torusLength][j], vchannels[i][j], cache);
                Thread core = new Thread(coreWorkers[i][j]);
                coreThreads[i][j] = core;
                core.start();
            }
        }


        for (int i = 0; i < torusLength; i++) {
            for (int j = 0; j < torusLength; j++) {
                coreWorkers[i][j].destroy();
                coreThreads[i][j].interrupt();
            }
            cache.printAllPaths(i);
        }
    }
    @Override
    public void execute() {

    }
}
