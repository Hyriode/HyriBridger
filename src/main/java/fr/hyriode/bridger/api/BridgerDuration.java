package fr.hyriode.bridger.api;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class BridgerDuration implements Comparable<BridgerDuration> {

    private final long ms;

    public BridgerDuration(long timeInMs) {
        ms = timeInMs;
    }

    public long toSecondsPart(long timeInMs) {
        return (this.ms / 1000) % 1000;
    }

    public long toMillisPart(long timeInMs) {
        return this.ms  % 1000;
    }

    public String toFormattedTime() {
        long result;
        if (ThreadLocalRandom.current().nextBoolean()) {
            result = Math.round(ms / 50) * 50L + 50;
        } else {
            result = Math.round(ms / 50) * 50L;
        }
        DecimalFormat format = new DecimalFormat("000");
        return this.toSecondsPart(result) + "." + format.format(this.toMillisPart(result));
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}