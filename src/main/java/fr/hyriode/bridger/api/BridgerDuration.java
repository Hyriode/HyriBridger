package fr.hyriode.bridger.api;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class BridgerDuration implements Comparable<BridgerDuration> {

    private final long ms;

    public BridgerDuration(long timeInMs) {
        if (timeInMs == 0) {
            ms = 0;
        } else {
            if (ThreadLocalRandom.current().nextBoolean()) {
                this.ms = Math.round(timeInMs / 50) * 50L + 50;
            } else {
                this.ms = Math.round(timeInMs / 50) * 50L;
            }
        }
    }

    public long toSecondsPart() {
        return (this.ms / 1000) % 1000;
    }

    public long toMillisPart() {
        return this.ms  % 1000;
    }

    public String toFormattedTime() {
        DecimalFormat format = new DecimalFormat("000");
        return this.toSecondsPart() + "." + format.format(this.toMillisPart());
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}