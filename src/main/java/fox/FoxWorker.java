package fox;

import java.util.concurrent.ArrayBlockingQueue;
import base.FloydWarshallWorker;
import simulator.SimulatedCache;
import utils.Message;

public class FoxWorker extends FloydWarshallWorker {

    public FoxWorker(int i, int j, int l, ArrayBlockingQueue<Boolean> downChannel, ArrayBlockingQueue<Boolean> upChannel, ArrayBlockingQueue<Message> w, ArrayBlockingQueue<Message> n, ArrayBlockingQueue<Message> e, ArrayBlockingQueue<Message> s, SimulatedCache cache, int blockCount){
        super(i, j, l, downChannel, upChannel, w, n, e, s, cache, blockCount);
    }
    public void run() {
        try {
            for (int j = 0; j < Math.ceil(Math.log(cache.getSize() - 1)/Math.log(2)); j++){
                readDown();
                for (int r = 0; r < getBlockCount(); r++){
                    for (int i = 0; i < getBlockCount(); i++){
                        Double a;
                        Double b;
                        int minirow1 = r*getTorusLength() + row;
                        int minicol1 = ((i + r) % getBlockCount())*getTorusLength() + column;
                        if (minirow1 < cache.getSize() && minicol1 < cache.getSize()) {
                            a = cache.readGraphVal(minirow1, minicol1);
                            timerUnit.addToTimer(row, column, 1);
                        } else {
                            a = Double.POSITIVE_INFINITY;
                        }
                        for (int c = 0; c < getBlockCount(); c++){
                            int minirow2 = (i + r) % getBlockCount()*getTorusLength() + row;
                            int minicol2 = c*getTorusLength() + column;
                            if (minirow2 < cache.getSize() && minicol2 < cache.getSize()) {
                                b = cache.readGraphVal(minirow2, minicol2);
                                timerUnit.addToTimer(row, column, 1);
                            } else {
                                b = Double.POSITIVE_INFINITY;
                            }
                            int witness = ((i+r)*getTorusLength())%(getBlockCount()*getTorusLength()) + column;

                            timerUnit.addToTimer(row, column, 14);
                            singleFW(a, b, witness);
                            if (minirow1 < cache.getSize() && minicol2 < cache.getSize()) {
                                cache.writeValIfLess(minirow1, minicol2, q, outWitness);
                                timerUnit.addToTimer(row, column, 5);
                            }
                            timerUnit.addToTimer(row, column, 3);
                        }
                        timerUnit.addToTimer(row, column, 13);
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
    protected void singleFW(double xVal, double yVal, int witness) throws InterruptedException {
        Double runningVal = Double.POSITIVE_INFINITY;
        Double broadcastVal;
        int broadcastWitness;
        int finalWitness = witness;
        timerUnit.addToTimer(row, column, 3);
        for (int i = 0; i < getTorusLength(); i++) {
            timerUnit.addToTimer(row, column, 6);
            if (Math.floorMod(column - row, getTorusLength()) == i) {
                writeWest(new Message(xVal, witness));
                timerUnit.synchroniseRow(row, column);
                writeNorth(new Message(yVal, -1));
                Message message = readEast();
                broadcastVal = message.getVal();
                broadcastWitness = message.getWitness();
            } else {
                Message message = readEast();
                broadcastVal = message.getVal();
                broadcastWitness = message.getWitness();
                writeWest(new Message(broadcastVal, broadcastWitness));
                timerUnit.commitTime(row, column);
                writeNorth(new Message(yVal, -1));
            }
            Double newVal = broadcastVal + yVal;
            timerUnit.addToTimer(row, column, 5);
            if (newVal < runningVal) {
                runningVal = newVal;
                finalWitness = broadcastWitness;
                timerUnit.addToTimer(row, column, 4);
            }
            Message message = readSouth();
            yVal = message.getVal();
            timerUnit.synchroniseMessage(row, column, false);
        }
        q = runningVal;
        outWitness = finalWitness;
        timerUnit.addToTimer(row, column, 3);
    }
}
