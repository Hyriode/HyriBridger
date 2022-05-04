package fr.hyriode.bridger.api;

public enum Medal {

    BRONZE(12000, 44000, 8500, 1, "medal.bronze"),
    IRON(9000, 24000, 6700, 2, "medal.iron"),
    GOLD(6500, 12000, 5300, 3, "medal.gold"),
    PLATINUM(4300, 8500, 4300, 4, "medal.ultimate")
    ;

    private final long timeToReachShort;
    private final long timeToReachNormal;
    private final long timeToReachDiagonal;
    private final int id;
    private final String languageValue;

    Medal(long timeToReachShort, long timeToReachLong, long timeToReachDiagonal, int id, String languageValue) {
        this.timeToReachShort = timeToReachShort;
        this.timeToReachNormal = timeToReachLong;
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

    public long getTimeToReach(String gameType) {
        if(gameType.equalsIgnoreCase("short")) {
            return this.timeToReachShort;
        }else if(gameType.equalsIgnoreCase("normal")) {
            return this.timeToReachNormal;
        }else {
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
