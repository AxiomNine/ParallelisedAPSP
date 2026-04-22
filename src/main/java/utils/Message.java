package utils;

public class Message {
    private final double val;
    private final int pred;

    public Message(double v, int w) {
        val = v;
        pred = w;
    }

    public double getVal() {
        return val;
    }

    public int getPred() {
        return pred;
    }
}
