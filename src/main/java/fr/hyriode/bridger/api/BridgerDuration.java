package fr.hyriode.bridger.api;

import java.text.DecimalFormat;

public class BridgerDuration implements Comparable<BridgerDuration> {

    private final long ms;

    public BridgerDuration(long timeInMs) {
        this.ms = timeInMs;
    }
    
    public String toFormattedTime() {
        double seconds = (double) this.ms / 1000;
        double roundedSeconds = Math.ceil(seconds * 20) / 20;
        long wholeSeconds = (long) roundedSeconds;
        long millisPart = (long) ((roundedSeconds - wholeSeconds) * 100);

        return wholeSeconds + "." + new DecimalFormat("00").format(millisPart);
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}