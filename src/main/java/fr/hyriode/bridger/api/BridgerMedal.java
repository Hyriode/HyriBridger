package fr.hyriode.bridger.api;

import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.bridger.game.blocks.BridgerBlock;

import java.util.*;

import static fr.hyriode.bridger.game.blocks.BridgerBlock.*;

public enum BridgerMedal {

    BRONZE("medal.bronze", COAL_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 12000L);
        put(BridgerGameType.NORMAL, 44000L);
        put(BridgerGameType.DIAGONAL, 8500L);
    }}),
    IRON("medal.iron", IRON_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 9000L);
        put(BridgerGameType.NORMAL, 24000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }}),
    GOLD("medal.gold", GOLD_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 6000L);
        put(BridgerGameType.NORMAL, 12000L);
        put(BridgerGameType.DIAGONAL, 5500L);
    }}),
    ULTIMATE("medal.ultimate", DIAMOND_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 4500L);
        put(BridgerGameType.NORMAL, 9000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }});

    private final String languageValue;
    private BridgerBlock rewardBlock;
    private final Map<BridgerGameType, Long> timeToReach;

    BridgerMedal(String languageValue, Map<BridgerGameType, Long> timeToReach) {
        this.languageValue = languageValue;
        this.timeToReach = timeToReach;
    }

    BridgerMedal(String languageValue, BridgerBlock rewardBlock, Map<BridgerGameType, Long> timeToReach) {
        this.languageValue = languageValue;
        this.timeToReach = timeToReach;
        this.rewardBlock = rewardBlock;
    }

    public long getTimeToReach(BridgerGameType gameType) {
        return this.timeToReach.get(gameType);
    }

    public String getLanguageValue() {
        return languageValue;
    }

    public BridgerBlock getRewardBlock() {
        return rewardBlock;
    }
}