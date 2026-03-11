package cannon;

import base.FloydWarshallWorker;
import metrics.TimerUnit;
import simulator.SimulatedCache;
import utils.Message;

import java.util.concurrent.ArrayBlockingQueue;

public class CannonWorker extends FloydWarshallWorker {

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
                            if (multInt< cache.getSize() && minicol < cache.getSize()) {
                                b = cache.readGraphVal(multInt, minicol);
                                timerUnit.addToTimer(row, column, 1);
                            } else {
                                b = Double.POSITIVE_INFINITY;
                            }
                            int witness = ((i + r + c)*getTorusLength()) % (getBlockCount()*getTorusLength()) + offset;
                            timerUnit.addToTimer(row, column, 25);
                            singleFW(a, b, witness);
                            if (minirow < cache.getSize() && minicol < cache.getSize()) {
                                cache.writeValIfLess(minirow, minicol, q, outWitness);
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
    protected void singleFW(double xVal, double yVal, int witness) throws InterruptedException{
        Double runningVal = Double.POSITIVE_INFINITY;
        int finalWitness = witness;
        timerUnit.addToTimer(row, column, 5);
        for (int i = 0; i < getTorusLength(); i++) {
            Double newVal = xVal + yVal;
            if (newVal < runningVal) {
                runningVal = newVal;
                finalWitness = witness;
                timerUnit.addToTimer(row, column, 2);
            }
            writeNorth(new Message(yVal, -1));
            writeWest(new Message(xVal, witness));
            timerUnit.addToTimer(row, column, 9);
            Message message = readSouth();
            yVal = message.getVal();
            timerUnit.synchroniseMessage(row, column, true);
            message = readEast();
            xVal = message.getVal();
            witness = message.getWitness();
            timerUnit.synchroniseMessage(row, column, false);
        }
        q = runningVal;
        outWitness = finalWitness;
    }
}
