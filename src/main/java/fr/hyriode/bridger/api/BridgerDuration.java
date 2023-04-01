package fr.hyriode.bridger.api;

import java.text.DecimalFormat;

public class BridgerDuration {

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
    }

    public long getExactTime() {
        return ms;
    }
}
