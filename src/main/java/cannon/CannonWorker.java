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
            timerUnit.addToTimer(row, column, 4);
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++) {
                timerUnit.addToTimer(row, column, 0.5);
                readDown();
                timerUnit.addToTimer(row, column, 0.5);
                for (int r = 0; r < getBlockCount(); r++) {
                    timerUnit.addToTimer(row, column, 0.5);
                    for (int i = 0; i < getBlockCount(); i++) {
                        timerUnit.addToTimer(row, column, 0.5);
                        for (int c = 0; c < getBlockCount(); c++) {
                            timerUnit.addToTimer(row, column, 0.5);
                            double a;
                            double b;
                            int offset = ((row + column) % getTorusLength());
                            timerUnit.addToTimer(row, column, 2);
                            int multInt = ((i + r + c) % getBlockCount())*getTorusLength() + offset;
                            timerUnit.addToTimer(row, column, 5);
                            int minirow = r*getTorusLength() + row;
                            timerUnit.addToTimer(row, column, 2);
                            int minicol = c*getTorusLength() + column;
                            timerUnit.addToTimer(row, column, 2);
                            if (minirow < cache.getSize() && multInt < cache.getSize()) {
                                timerUnit.addToTimer(row, column, 3);
                                a = cache.readGraphVal(minirow, multInt);
                                timerUnit.addToTimer(row, column, 1);
                            } else {
                                timerUnit.addToTimer(row, column, 3);
                                a = Double.POSITIVE_INFINITY;
                                timerUnit.addToTimer(row, column, 1);
                            }
                            int pred;
                            if (multInt < cache.getSize() && minicol < cache.getSize()) {
                                timerUnit.addToTimer(row, column, 3);
                                b = cache.readGraphVal(multInt, minicol);
                                pred = cache.readPred(multInt, minicol);
                                timerUnit.addToTimer(row, column, 2);
                            } else {
                                timerUnit.addToTimer(row, column, 3);
                                b = Double.POSITIVE_INFINITY;
                                pred = -1;
                                timerUnit.addToTimer(row, column, 2);
                            }
                            singleMM(a, b, pred);
                            if (minirow < cache.getSize() && minicol < cache.getSize()) {
                                timerUnit.addToTimer(row, column, 3);
                                cache.writeValIfLess(minirow, minicol, q, outPred);
                                timerUnit.addToTimer(row, column, 2);
                            }
                            timerUnit.addToTimer(row, column, 3);
                            timerUnit.addToTimer(row, column, 1.5);
                        }
                        timerUnit.addToTimer(row, column, 1.5);
                    }
                    timerUnit.addToTimer(row, column, 1.5);
                }
                timerUnit.addToTimer(row, column, 1.5);
                writeUp(true);
            }
            timerUnit.addToTimer(row, column, 0.5);
        } catch (InterruptedException e) {
            return;
        }
    }

    @Override
    protected void singleMM(double xVal, double yVal, int pred) throws InterruptedException{
        Double runningVal = Double.POSITIVE_INFINITY;
        int finalPred = pred;
        timerUnit.addToTimer(row, column, 2);
        for (int i = 0; i < getTorusLength(); i++) {
            timerUnit.addToTimer(row, column, 0.5);
            Double newVal = xVal + yVal;
            timerUnit.addToTimer(row, column, 1);
            if (newVal < runningVal) {
                runningVal = newVal;
                finalPred = pred;
                timerUnit.addToTimer(row, column, 2);
            }
            timerUnit.addToTimer(row, column, 0.5);
            writeNorth(new Message(yVal, pred));
            timerUnit.addToTimer(row, column, 3);
            writeWest(new Message(xVal, -1));
            timerUnit.addToTimer(row, column, 3);
            Message message = readSouth();
            yVal = message.getVal();
            pred = message.getPred();
            timerUnit.synchroniseMessage(row, column, true);
            message = readEast();
            xVal = message.getVal();
            timerUnit.synchroniseMessage(row, column, false);
            timerUnit.addToTimer(row, column, 1);
        }
        timerUnit.addToTimer(row, column, 0.5);
        q = runningVal;
        outPred = finalPred;
        timerUnit.addToTimer(row, column, 2);
    }
}
