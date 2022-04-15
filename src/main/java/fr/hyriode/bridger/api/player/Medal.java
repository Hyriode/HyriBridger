package fr.hyriode.bridger.api.player;

public enum Medal {

    BRONZE(22000, 0, "medal.bronze"),
    IRON(12000, 1, "medal.iron"),
    GOLD(6000, 2, "medal.gold"),
    PLATINUM(4250, 3, "medal.ultimate")
    ;

    private final long timeToReach;
    private final int id;
    private final String languageValue;

    Medal(long timeToReach, int id, String languageValue) {
        this.timeToReach = timeToReach;
        this.id = id;
        this.languageValue = languageValue;
    }

    public long getTimeToReach() {
        return timeToReach;
    }

    public static Medal getById(int id) {
        for (Medal medal : Medal.values()) {
            if(medal.getId() == id) {
                return medal;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getLanguageValue() {
        return languageValue;
    }
}
