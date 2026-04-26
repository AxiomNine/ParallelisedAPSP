package fox;

import base.MatMulMainCore;

import java.util.concurrent.ArrayBlockingQueue;

public class FoxMainCore extends MatMulMainCore {
    public FoxMainCore(String fileAddress, int torusLength, String compress) {
        super(fileAddress, torusLength, compress);

        int blockCount = Math.ceilDiv(cache.getSize(), torusLength);
        for (int i = 0; i < torusLength; i++) {
            for (int j = 0; j < torusLength; j++) {
                coreDownChannels[i][j] = new ArrayBlockingQueue<Boolean>(1);
                coreWorkers[i][j] = new FoxWorker(i, j, torusLength, coreDownChannels[i][j], coreUpChannels[i][j], hchannels[i][j], vchannels[i][j], hchannels[i][(j + 1) % torusLength], vchannels[(i + 1) % torusLength][j], cache, blockCount);
                Thread core = new Thread(coreWorkers[i][j]);
                coreThreads[i][j] = core;
                core.start();
            }
        }
        run();
    }
}
