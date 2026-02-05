package cannon;

import simulator.FloydWarshallMainCore;
import java.util.concurrent.ArrayBlockingQueue;

public class CannonMainCore extends FloydWarshallMainCore {
    public CannonMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        for (int i = 0; i < torusLength; i++) {
            for (int j = 0; j < torusLength; j++) {
                coreDownChannels[i][j] = new ArrayBlockingQueue<Double>(1);
                coreWorkers[i][j] = new CannonWorker(i, j, torusLength, coreDownChannels[i][j], coreUpChannels[i][j], hchannels[i][j], vchannels[i][j], hchannels[i][(j + 1) % torusLength], vchannels[(i + 1) % torusLength][j], cache);
                Thread core = new Thread(coreWorkers[i][j]);
                coreThreads[i][j] = core;
                core.start();
            }
        }

        int blockCount = Math.ceilDiv(cache.getSize(), torusLength);

        try {
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++) {
                cache.prepareToDouble();
                for (int i = 0; i < blockCount; i++) {
                    for (int r = 0; r < blockCount; r++) {
                        for (int c = 0; c < blockCount; c++) {
                            Double[][] aBlock = readSubMatrix(r, (i + r + c) % blockCount, torusLength);
                            Double[][] bBlock = readSubMatrix((i + r + c) % blockCount, c, torusLength);
                            Double[][] qBlock = new Double[torusLength][torusLength];
                            Integer[][] witnessBlock = new Integer[torusLength][torusLength];
                            for (int p = 0; p < torusLength; p++) {
                                for (int q = 0; q < torusLength; q++) {
                                    coreDownChannels[p][q].put(aBlock[p][(p + q) % torusLength]);
                                }
                            }
                            for (int p = 0; p < torusLength; p++) {
                                for (int q = 0; q < torusLength; q++) {
                                    coreDownChannels[p][q].put(bBlock[(p + q) % torusLength][q]);
                                    coreDownChannels[p][q].put(((i + r + c)*torusLength) % (blockCount*torusLength) + ((double) p + (double) q) % torusLength);
                                }
                            }
                            for (int p = 0; p < torusLength; p++) {
                                for (int q = 0; q < torusLength; q++) {
                                    qBlock[p][q] = coreUpChannels[p][q].take();
                                    witnessBlock[p][q] = coreUpChannels[p][q].take().intValue();
                                }
                            }
                            writeSubMatrix(r, c, torusLength, qBlock, witnessBlock);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        cache.calculateAndPrintAllPaths();
        for (int a = 0; a < torusLength; a++) {
            for (int b = 0; b < torusLength; b++) {
                coreWorkers[a][b].destroy();
                coreThreads[a][b].interrupt();
            }
        }
    }
    @Override
    public void execute() {

    }
}
