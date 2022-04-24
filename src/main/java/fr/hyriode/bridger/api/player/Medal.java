package fr.hyriode.bridger.api.player;

public enum Medal {

    BRONZE(22000, 44000, 44000, 1, "medal.bronze"),
    IRON(12000, 24000, 24000, 2, "medal.iron"),
    GOLD(6000, 12000, 12000, 3, "medal.gold"),
    PLATINUM(4250, 8500, 8500, 4, "medal.ultimate")
    ;

    private final long timeToReachShort;
    private final long timeToReachLong;
    private final long timeToReachDiagonal;
    private final int id;
    private final String languageValue;

    Medal(long timeToReachShort, long timeToReachLong, long timeToReachDiagonal, int id, String languageValue) {
        this.timeToReachShort = timeToReachShort;
        this.timeToReachLong = timeToReachLong;
        this.timeToReachDiagonal = timeToReachDiagonal;
        this.id = id;
        this.languageValue = languageValue;
    }

    public static Medal getById(int id) {
        for (Medal medal : Medal.values()) {
            if(medal.getId() == id) {
                return medal;
            }
        }
        return null;
    }

    public long getTimeToReachShort() {
        return timeToReachShort;
    }

    public long getTimeToReachLong() {
        return timeToReachLong;
    }

    public long getTimeToReachDiagonal() {
        return timeToReachDiagonal;
    }

    public int getId() {
        return id;
    }

    public String getLanguageValue() {
        return languageValue;
    }
}
