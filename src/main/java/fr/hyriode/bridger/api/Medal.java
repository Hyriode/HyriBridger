package fr.hyriode.bridger.api;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;

public enum Medal {

    BRONZE(12000, 44000, "medal.bronze"),
    IRON(9000, 24000, "medal.iron"),
    GOLD(6000, "medal.gold"),
    ULTIMATE(4500, "medal.ultimate")
    ;

    private final long timeToReachShort;
    private final long timeToReachNormal;
    private final long timeToReachDiagonal;
    private final String languageValue;

    Medal(long timeToReachShort, long timeToReachLong, long timeToReachDiagonal, String languageValue) {
        this.timeToReachShort = timeToReachShort;
        this.timeToReachNormal = timeToReachLong;
        this.timeToReachDiagonal = timeToReachDiagonal;
        this.languageValue = languageValue;
    }

    Medal(long timeToReachShort, String languageValue) {
        this(timeToReachShort, timeToReachShort*2, languageValue);
    }

    Medal(long timeToReachShort, long timeToReachLong, String languageValue) {
        this(timeToReachShort, timeToReachLong, (timeToReachShort/2)+2500, languageValue);
    }

    public long getTimeToReachShort() {
        return this.timeToReachShort;
    }

    public long getTimeToReachNormal() {
        return this.timeToReachNormal;
    }

    public long getTimeToReachDiagonal() {
        return this.timeToReachDiagonal;
    }

    public String getLanguageValue() {
        return languageValue;
    }
}
