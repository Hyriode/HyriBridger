package fr.hyriode.bridger.api;

import fr.hyriode.bridger.game.BridgerGameType;

import java.util.HashMap;
import java.util.Map;

public enum BridgerMedal {

    BRONZE("medal.bronze", new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 12000L);
        put(BridgerGameType.NORMAL, 44000L);
        put(BridgerGameType.DIAGONAL, 8500L);
    }}),
    IRON("medal.iron", new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 9000L);
        put(BridgerGameType.NORMAL, 24000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }}),
    GOLD("medal.gold", new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 6000L);
        put(BridgerGameType.NORMAL, 12000L);
        put(BridgerGameType.DIAGONAL, 5500L);
    }}),
    ULTIMATE("medal.ultimate", new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 4500L);
        put(BridgerGameType.NORMAL, 9000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }});

    private final Map<BridgerGameType, Long> timeToReach;
    private final String languageValue;

    BridgerMedal(String languageValue, Map<BridgerGameType, Long> timeToReach) {
        this.timeToReach = timeToReach;
        this.languageValue = languageValue;
    }

    public long getTimeToReach(BridgerGameType gameType) {
        return this.timeToReach.get(gameType);
    }

    public String getLanguageValue() {
        return languageValue;
    }
}