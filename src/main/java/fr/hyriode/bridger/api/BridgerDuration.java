package fr.hyriode.bridger.api;

public class BridgerDuration implements Comparable<BridgerDuration> {

    private final long ms;

    public BridgerDuration(long timeInMs) {
        this.ms = timeInMs;
    }

    public String toFormattedTime() {
        double durationInSeconds = ms / 1000.0;
        double roundedNumber = Math.round(durationInSeconds * 20) / 20.0;
        String result = String.valueOf(roundedNumber);
        if (result.split("\\.")[1].length() == 1) {
            result += "0";
        }
        return result + "0";
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }
}