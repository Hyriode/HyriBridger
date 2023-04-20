package fr.hyriode.bridger.api;

import java.text.DecimalFormat;

public class BridgerDuration implements Comparable<BridgerDuration> {

    private final long ms;

    public BridgerDuration(long timeInMs) {
        this.ms = timeInMs;
    }

    public long toSecondsPart() {
        return (this.ms / 1000) % 1000;
    }

    public long toMillisPart() {
        return this.ms % 1000;
    }

    public String toFormattedTime() {
        long roundedMs = (long) (Math.ceil((double) this.ms / 50.0) * 50);
        long seconds = roundedMs / 1000;
        long millis = roundedMs % 1000;
        return seconds + "." + new DecimalFormat("00").format(millis);
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}