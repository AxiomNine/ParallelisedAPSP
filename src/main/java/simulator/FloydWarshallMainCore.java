package simulator;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class FloydWarshallMainCore extends MainCore<Double> {
    protected final ArrayBlockingQueue<Double>[][] hchannels;
    protected final ArrayBlockingQueue<Double>[][] vchannels;
    protected final ArrayBlockingQueue<Double>[][] coreUpChannels;

    public FloydWarshallMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        hchannels = new ArrayBlockingQueue[torusLength][torusLength];
        vchannels = new ArrayBlockingQueue[torusLength][torusLength];
        coreUpChannels = new ArrayBlockingQueue[torusLength][torusLength];
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                hchannels[i][j] = new ArrayBlockingQueue<Double>(1);
                vchannels[i][j] = new ArrayBlockingQueue<Double>(1);
                coreUpChannels[i][j] = new ArrayBlockingQueue<Double>(1);
            }
        }
    }
    protected Double[][] readSubMatrix(int blockRow, int blockCol, int blockSize) {
        Double[][] subMatrix = new Double[blockSize][blockSize];
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (blockRow*blockSize + i < cache.getSize() && blockCol*blockSize + j < cache.getSize()) {
                    subMatrix[i][j] = cache.readGraphVal(blockRow*blockSize + i, blockCol*blockSize + j);
                } else {
                    subMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return subMatrix;
    }
    protected void writeSubMatrix(int blockRow, int blockCol, int blockSize, Double[][] costMatrix, Integer[][] witnessMatrix) {
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (blockRow*blockSize + i < cache.getSize() && blockCol*blockSize + j < cache.getSize()) {
                cache.writeValIfLess(blockRow*blockSize + i, blockCol*blockSize + j, costMatrix[i][j], witnessMatrix[i][j]);
                }
            }
        }
    }
}
