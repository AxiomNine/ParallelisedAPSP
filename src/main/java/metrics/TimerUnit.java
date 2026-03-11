package metrics;

import java.util.Arrays;
import java.util.Random;

public class TimerUnit {

    private static TimerUnit timerUnit;
    private double[][] timers;
    private double[][] uncommittedTimes;

    private double maxTime;
    private int torusSize;
    private final Random random;
    private final static Object lock = new Object();
    private TimerUnit(int torusSize){
        this.torusSize = torusSize;
        timers = new double[torusSize][torusSize];
        uncommittedTimes = new double[torusSize][torusSize];
        double[] row = new double[torusSize];
        Arrays.fill(row, 0.0);
        for (int i = 0; i < timers.length; i++){
            timers[i] = row.clone();
            uncommittedTimes[i] = row.clone();
        }
        random = new Random();
    }
    public static synchronized TimerUnit getTimerUnit(int torusSize) {
        if (timerUnit == null) {
            timerUnit = new TimerUnit(torusSize);
        }
        return timerUnit;
    }

    private void updateMaxTime(double t){
        maxTime = Math.max(maxTime, t);
    }

    public synchronized void addToTimer(int i, int j, double val){
        double newVal = random.nextGaussian(val, val / 5.0);
        timers[i][j] += newVal;
        updateMaxTime(timers[i][j]);
    }

    public synchronized void synchroniseMessage(int toI, int toJ, boolean up){
            double latency = random.nextGaussian(30, 6);
            double time = Math.max(timers[toI][toJ], latency + (up ? timers[Math.floorMod(toI + 1, torusSize)][toJ] : timers[toI][Math.floorMod(toJ + 1, torusSize)]));
            timers[toI][toJ] = time;
            updateMaxTime(time);
    }

    public synchronized void synchroniseRow(int i, int j0){
            double time = timers[i][j0];
            double leftDelay = 0.0;
            double rightDelay = 0.0;
            for (int x = 0; x < Math.max(j0, torusSize - j0); x++) {
                if (j0 >= x) {
                    leftDelay += random.nextGaussian(j0 == x ? 36 : 33, j0 == x ? 7.2 : 6.6);
                    uncommittedTimes[i][j0 - x] = time + leftDelay;
                }
                if (j0 + x < torusSize) {
                    rightDelay += random.nextGaussian(j0 + x == torusSize - 1 ? 36 : 33, j0 + x == torusSize - 1 ? 7.2 : 6.6);
                    uncommittedTimes[i][j0 + x] = time + rightDelay;
                }
            }
            updateMaxTime(time + Math.max(leftDelay, rightDelay));
    }

    public synchronized void commitTime(int i, int j){
        timers[i][j] = Math.max(timers[i][j], uncommittedTimes[i][j]);
    }

    public void synchroniseArray(){
        synchronized (lock) {
            double[] row = new double[torusSize];
            Arrays.fill(row, maxTime);
            Arrays.fill(timers, row.clone());
        }
    }
    public double returnMaxTime() {
        return maxTime;
    }
}
