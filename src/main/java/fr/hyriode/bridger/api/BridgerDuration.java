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
        return Math.round((double) this.ms % 1000 / 500) * 500;
    }

    public String toFormattedTime() {
        long roundedMs = Math.round((double) this.ms / 50) * 50;
        long seconds = roundedMs / 1000;
        long millis = roundedMs % 1000;
        return seconds + "." + new DecimalFormat("00").format(millis) + "0";
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}
