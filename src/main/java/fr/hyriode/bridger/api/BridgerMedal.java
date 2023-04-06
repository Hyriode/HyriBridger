package fr.hyriode.bridger.api;

import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.language.BridgerMessage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.hyriode.bridger.game.blocks.BridgerBlock.*;

public enum BridgerMedal {

    BRONZE(BridgerMessage.MEDAL_BRONZE, COAL_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 12000L);
        put(BridgerGameType.NORMAL, 44000L);
        put(BridgerGameType.DIAGONAL, 8500L);
    }}),
    IRON(BridgerMessage.MEDAL_IRON, IRON_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 9000L);
        put(BridgerGameType.NORMAL, 24000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }}),
    GOLD(BridgerMessage.MEDAL_GOLD, GOLD_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 6000L);
        put(BridgerGameType.NORMAL, 12000L);
        put(BridgerGameType.DIAGONAL, 5500L);
    }}),
    ULTIMATE(BridgerMessage.MEDAL_ULTIMATE, DIAMOND_BLOCK, new HashMap<BridgerGameType, Long>(){{
        put(BridgerGameType.SHORT, 4500L);
        put(BridgerGameType.NORMAL, 9000L);
        put(BridgerGameType.DIAGONAL, 7000L);
    }});

    private final BridgerMessage messageValue;
    private final BridgerBlock rewardBlock;
    private final Map<BridgerGameType, Long> timeToReach;

    BridgerMedal(BridgerMessage messageValue, BridgerBlock rewardBlock, Map<BridgerGameType, Long> timeToReach) {
        this.messageValue = messageValue;
        this.timeToReach = timeToReach;
        this.rewardBlock = rewardBlock;
    }

    public long getTimeToReach(BridgerGameType gameType) {
        return this.timeToReach.get(gameType);
    }

    public BridgerMessage getMessageValue() {
        return messageValue;
    }

    public BridgerBlock getRewardBlock() {
        return rewardBlock;
    }

    public static BridgerMedal getMedalByTime(long time, BridgerGameType gameType) {
        for(BridgerMedal medal : BridgerMedal.values()) {
            if(medal.getTimeToReach(gameType) > time) return medal;
        }
        return null;
    }

    public static List<BridgerMedal> getMedalsBefore(BridgerMedal medal) {
        if (medal == null) return Collections.emptyList();
        if (medal == BridgerMedal.ULTIMATE) return Collections.singletonList(BridgerMedal.ULTIMATE);
        return Stream.of(BridgerMedal.values()).filter(m -> m.ordinal() <= medal.ordinal()).collect(Collectors.toList());
    }
}