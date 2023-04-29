package fr.hyriode.bridger.api;

//import java.text.DecimalFormat;
//
//public class BridgerDuration implements Comparable<BridgerDuration> {
//
//    private final long ms;
//
//    public BridgerDuration(long timeInMs) {
//        this.ms = timeInMs;
//    }
//
//    public long toSecondsPart() {
//        return (this.ms / 1000) % 1000;
//    }
//
//    public long toMillisPart() {
//        return this.ms  % 1000;
//    }
//
//    public String toFormattedTime() {
//        return this.toSecondsPart() + "." + new DecimalFormat("000").format(this.toMillisPart());
//    }
//
//    public long getExactTime() {
//        return ms;
//    }
//
//    @Override
//    public int compareTo(BridgerDuration other) {
//        return Long.compare(this.getExactTime(), other.getExactTime());
//    }
//}


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
        return toFormattedTimeWithRounding();
//        return this.toSecondsPart() + "." + new DecimalFormat("000").format(this.toMillisPart());
    }

    public String toFormattedTimeWithRounding() {
        long roundedDurationInMillis = roundToNearestFiveCents(this.ms);
        long roundedSecondsPart = (roundedDurationInMillis / 1000) % 1000;
        long roundedMillisPart = roundedDurationInMillis % 1000;
        return roundedSecondsPart + "." + new DecimalFormat("000").format(roundedMillisPart);
    }

    public long getExactTime() {
        return ms;
    }

    @Override
    public int compareTo(BridgerDuration other) {
        return Long.compare(this.getExactTime(), other.getExactTime());
    }

    public static long roundToNearestFiveCents(long durationInMillis) {
        double durationInSeconds = durationInMillis / 1000.0; // convertit en secondes
        double roundedDurationInSeconds = Math.round(durationInSeconds * 20) / 20.0; // arrondit à 0,05 près
        long roundedDurationInMillis = Math.round(roundedDurationInSeconds * 1000); // convertit en millisecondes
        return roundedDurationInMillis;
    }
}