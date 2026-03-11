package utils;

public class Message {
    private final double val;
    private final int witness;

    public Message(double v, int w) {
        val = v;
        witness = w;
    }

    public double getVal() {
        return val;
    }

    public int getWitness() {
        return witness;
    }
}
