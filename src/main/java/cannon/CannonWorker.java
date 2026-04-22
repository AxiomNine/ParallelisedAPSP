package cannon;

import base.MatMulWorker;
import simulator.SimulatedCache;
import utils.Message;

import java.util.concurrent.ArrayBlockingQueue;

public class CannonWorker extends MatMulWorker {

    public CannonWorker(int i, int j, int l, ArrayBlockingQueue<Boolean> downChannel, ArrayBlockingQueue<Boolean> upChannel, ArrayBlockingQueue<Message> w, ArrayBlockingQueue<Message> n, ArrayBlockingQueue<Message> e, ArrayBlockingQueue<Message> s, SimulatedCache cache, int blockCount){
        super(i, j, l, downChannel, upChannel, w, n, e, s, cache, blockCount);
    }
    public void run(){
        try {
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++) {
                readDown();
                for (int r = 0; r < getBlockCount(); r++) {
                    for (int i = 0; i < getBlockCount(); i++) {
                        for (int c = 0; c < getBlockCount(); c++) {
                            double a;
                            double b;
                            int offset = ((row + column) % getTorusLength());
                            int multInt = ((i + r + c) % getBlockCount())*getTorusLength() + offset;
                            int minirow = r*getTorusLength() + row;
                            int minicol = c*getTorusLength() + column;
                            if (minirow < cache.getSize() && multInt < cache.getSize()) {
                                a = cache.readGraphVal(minirow, multInt);
                                timerUnit.addToTimer(row, column, 1);
                            } else {
                                a = Double.POSITIVE_INFINITY;
                            }
                            int pred;
                            if (multInt< cache.getSize() && minicol < cache.getSize()) {
                                b = cache.readGraphVal(multInt, minicol);
                                pred = cache.readPred(multInt, minicol);
                                timerUnit.addToTimer(row, column, 1);
                            } else {
                                b = Double.POSITIVE_INFINITY;
                                pred = -1;
                            }
                            timerUnit.addToTimer(row, column, 25);
                            singleMM(a, b, pred);
                            if (minirow < cache.getSize() && minicol < cache.getSize()) {
                                cache.writeValIfLess(minirow, minicol, q, outPred);
                                timerUnit.addToTimer(row, column, 5);
                            }
                            timerUnit.addToTimer(row, column, 3);
                        }
                        timerUnit.addToTimer(row, column, 3);
                    }
                    timerUnit.addToTimer(row, column, 3);
                }
                timerUnit.addToTimer(row, column, 1);
                writeUp(true);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    @Override
    protected void singleMM(double xVal, double yVal, int pred) throws InterruptedException{
        Double runningVal = Double.POSITIVE_INFINITY;
        int finalPred = pred;
        timerUnit.addToTimer(row, column, 5);
        for (int i = 0; i < getTorusLength(); i++) {
            Double newVal = xVal + yVal;
            if (newVal < runningVal) {
                runningVal = newVal;
                finalPred = pred;
                timerUnit.addToTimer(row, column, 2);
            }
            writeNorth(new Message(yVal, pred));
            writeWest(new Message(xVal, -1));
            timerUnit.addToTimer(row, column, 9);
            Message message = readSouth();
            yVal = message.getVal();
            pred = message.getPred();
            timerUnit.synchroniseMessage(row, column, true);
            message = readEast();
            xVal = message.getVal();
            timerUnit.synchroniseMessage(row, column, false);
        }
        q = runningVal;
        outPred = finalPred;
    }
}
