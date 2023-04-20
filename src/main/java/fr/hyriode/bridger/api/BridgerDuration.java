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
        long secondsPart = this.toSecondsPart();
        long millisPart = this.toMillisPart();
        if (millisPart == 1000) {
            millisPart = 0;
            secondsPart += 1;
        }
        return secondsPart + "." + new DecimalFormat("00").format(millisPart) + "0";
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}
