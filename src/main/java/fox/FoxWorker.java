package fox;

import java.util.concurrent.ArrayBlockingQueue;
import base.MatMulWorker;
import simulator.SimulatedCache;
import utils.Message;

public class FoxWorker extends MatMulWorker {

    public FoxWorker(int i, int j, int l, ArrayBlockingQueue<Boolean> downChannel, ArrayBlockingQueue<Boolean> upChannel, ArrayBlockingQueue<Message> w, ArrayBlockingQueue<Message> n, ArrayBlockingQueue<Message> e, ArrayBlockingQueue<Message> s, SimulatedCache cache, int blockCount){
        super(i, j, l, downChannel, upChannel, w, n, e, s, cache, blockCount);
    }
    public void run() {
        try {
            timerUnit.addToTimer(row, column, 4);
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++){
                timerUnit.addToTimer(row, column, 0.5);
                readDown();
                timerUnit.addToTimer(row, column, 0.5);
                for (int r = 0; r < getBlockCount(); r++){
                    timerUnit.addToTimer(row, column, 0.5);
                    for (int i = 0; i < getBlockCount(); i++){
                        timerUnit.addToTimer(row, column, 0.5);
                        Double a;
                        Double b;
                        int minirow1 = r*getTorusLength() + row;
                        timerUnit.addToTimer(row, column, 2);
                        int minicol1 = ((i + r) % getBlockCount())*getTorusLength() + column;
                        timerUnit.addToTimer(row, column, 4);
                        if (minirow1 < cache.getSize() && minicol1 < cache.getSize()) {
                            a = cache.readGraphVal(minirow1, minicol1);
                        } else {
                            a = Double.POSITIVE_INFINITY;
                        }
                        timerUnit.addToTimer(row, column, 4.5);
                        for (int c = 0; c < getBlockCount(); c++){
                            timerUnit.addToTimer(row, column, 0.5);
                            int minirow2 = (i + r) % getBlockCount()*getTorusLength() + row;
                            timerUnit.addToTimer(row, column, 4);
                            int minicol2 = c*getTorusLength() + column;
                            timerUnit.addToTimer(row, column, 2);
                            int pred;
                            if (minirow2 < cache.getSize() && minicol2 < cache.getSize()) {
                                b = cache.readGraphVal(minirow2, minicol2);
                                pred = cache.readPred(minirow2, minicol2);
                            } else {
                                b = Double.POSITIVE_INFINITY;
                                pred = -1;
                            }
                            timerUnit.addToTimer(row, column, 5);

                            singleMM(a, b, pred);
                            if (minirow1 < cache.getSize() && minicol2 < cache.getSize()) {
                                cache.writeValIfLess(minirow1, minicol2, q, outPred);
                                timerUnit.addToTimer(row, column, 2);
                            }
                            timerUnit.addToTimer(row, column, 3);
                            timerUnit.addToTimer(row, column, 1);
                        }
                        timerUnit.addToTimer(row, column, 1.5);
                    }
                    timerUnit.addToTimer(row, column, 1.5);
                }
                writeUp(true);
                timerUnit.addToTimer(row, column, 1.5);
            }
            timerUnit.addToTimer(row, column, 0.5);
        } catch (InterruptedException e) {
            return;
        }
    }
    
    @Override
    protected void singleMM(double xVal, double yVal, int pred) throws InterruptedException {
        Double runningVal = Double.POSITIVE_INFINITY;
        Double broadcastVal;
        int finalPred = pred;
        timerUnit.addToTimer(row, column, 2);
        for (int i = 0; i < getTorusLength(); i++) {
            timerUnit.addToTimer(row, column, 0.5);
            if (Math.floorMod(column - row, getTorusLength()) == i) {
                timerUnit.addToTimer(row, column, 2.5);
                writeWest(new Message(xVal, -1));
                timerUnit.synchroniseRow(row, column);
                writeNorth(new Message(yVal, pred));
                timerUnit.addToTimer(row, column, 3);
                Message message = readEast();
                broadcastVal = message.getVal();
                timerUnit.addToTimer(row, column, 3);
            } else {
                timerUnit.addToTimer(row, column, 2.5);
                Message message = readEast();
                broadcastVal = message.getVal();
                timerUnit.addToTimer(row, column, 3);
                writeWest(new Message(broadcastVal, -1));
                timerUnit.commitTime(row, column);
                writeNorth(new Message(yVal, pred));
                timerUnit.addToTimer(row, column, 3);
            }
            Double newVal = broadcastVal + yVal;
            timerUnit.addToTimer(row, column, 1);
            if (newVal < runningVal) {
                runningVal = newVal;
                finalPred = pred;
                timerUnit.addToTimer(row, column, 2);
            }
            timerUnit.addToTimer(row, column, 0.5);
            Message message = readSouth();
            yVal = message.getVal();
            pred = message.getPred();
            timerUnit.synchroniseMessage(row, column, false);
            timerUnit.addToTimer(row, column, 1);
        }
        timerUnit.addToTimer(row, column, 0.5);
        q = runningVal;
        outPred = finalPred;
    }
}
