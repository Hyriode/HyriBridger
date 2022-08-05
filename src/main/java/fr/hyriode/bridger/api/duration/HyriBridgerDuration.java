package fr.hyriode.bridger.api.duration;

import java.text.DecimalFormat;
import java.util.Random;

public class HyriBridgerDuration {

    private final long ms;

    public HyriBridgerDuration(long timeInMs) {
        this.ms = timeInMs;
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
}
