package base;

import metrics.TimerUnit;
import utils.Message;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class MatMulMainCore extends MainCore<Boolean> {
    protected final ArrayBlockingQueue<Message>[][] hchannels;
    protected final ArrayBlockingQueue<Message>[][] vchannels;
    protected final ArrayBlockingQueue<Boolean>[][] coreUpChannels;
    protected final int blockCount;

    public MatMulMainCore(String fileAddress, int torusLength, String compress){
        super(fileAddress, torusLength, compress);
        hchannels = new ArrayBlockingQueue[torusLength][torusLength];
        vchannels = new ArrayBlockingQueue[torusLength][torusLength];
        coreUpChannels = new ArrayBlockingQueue[torusLength][torusLength];
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                hchannels[i][j] = new ArrayBlockingQueue<Message>(1);
                vchannels[i][j] = new ArrayBlockingQueue<Message>(1);
                coreUpChannels[i][j] = new ArrayBlockingQueue<Boolean>(1);
            }
        }
        blockCount = Math.ceilDiv(cache.getSize(), torusLength);
    }

    protected void run(){
        try {
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++){
                cache.prepareToDouble();
                for (ArrayBlockingQueue<Boolean>[] channelRow: coreDownChannels){
                    for (ArrayBlockingQueue<Boolean> channel : channelRow){
                        channel.put(true);
                    }
                }
                for (ArrayBlockingQueue<Boolean>[] channelRow: coreUpChannels){
                    for (ArrayBlockingQueue<Boolean> channel : channelRow){
                        channel.take();
                    }
                }
                TimerUnit.getTimerUnit(coreWorkers.length).synchroniseArray();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (int a = 0; a < coreWorkers.length; a++) {
            for (int b = 0; b < coreWorkers.length; b++) {
                coreWorkers[a][b].destroy();
                coreThreads[a][b].interrupt();
            }
        }
        getElapsedTimeOfCores();
    }
}
