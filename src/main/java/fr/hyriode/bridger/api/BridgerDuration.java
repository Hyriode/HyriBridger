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
        return this.ms  % 1000;
    }

    public String toFormattedTime() {
        return this.toSecondsPart() + "." + new DecimalFormat("000").format(this.toMillisPart());
        /*float durationInSeconds = (float) (ms / 1000.0);
        double roundedNumber = Math.round(durationInSeconds * 20) / 20.0;
        String result = String.valueOf(roundedNumber);
        if (result.split("\\.")[1].length() == 1) {
            result += "0";
        }
        return result + "0";*/
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}