package fr.hyriode.bridger.api;

import java.util.HashMap;
import java.util.Map;

public enum Medal {

    BRONZE(1, 12000, 44000, 8500, "medal.bronze"),
    IRON(2, 9000, 24000, 6700, "medal.iron"),
    GOLD(3, 6500, 12000, 5300, "medal.gold"),
    ULTIMATE(4, 4300, 8500, 4300, "medal.ultimate")
    ;

    public static final Map<Integer, Medal> BY_ID = new HashMap<>();
    static {
        for (Medal medal : values()) {
            BY_ID.put(medal.id, medal);
        }
    }

    private final int id;
    private final long timeToReachShort;
    private final long timeToReachNormal;
    private final long timeToReachDiagonal;
    private final String languageValue;

    Medal(int id, long timeToReachShort, long timeToReachLong, long timeToReachDiagonal, String languageValue) {
        this.id = id;
        this.timeToReachShort = timeToReachShort;
        this.timeToReachNormal = timeToReachLong;
        this.timeToReachDiagonal = timeToReachDiagonal;
        this.languageValue = languageValue;
    }

    public static Medal getById(int id) {
        return BY_ID.get(id);
    }

    public long getTimeToReach(String gameType) {
        if (gameType.equalsIgnoreCase("short")) {
            return this.timeToReachShort;
        } else if (gameType.equalsIgnoreCase("normal")) {
            return this.timeToReachNormal;
        } else {
            return this.timeToReachDiagonal;
        }
    }

    public int getId() {
        return id;
    }

    public String getLanguageValue() {
        return languageValue;
    }
}
