package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.game.BridgerGameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HyriBridgerStats extends HyriPlayerData {

    private final UUID uuid;
    private Map<BridgerGameType, HyriBridgerDuration> personalBests;
    private Map<BridgerGameType, Medal> highestAcquiredMedals;
    private long blocksPlaced;
    private long bridgesMade;
    private long bridgeFailed;
    private long playedTimeInMs;

    public HyriBridgerStats(UUID uuid) {
        this.uuid = uuid;
        this.personalBests = new HashMap<>();
        this.highestAcquiredMedals = new HashMap<>();
    }

    public void setPersonalBest(BridgerGameType gameType, HyriBridgerDuration duration) {
        this.personalBests.remove(gameType);
        this.personalBests.put(gameType, duration);
    }

    public HyriBridgerDuration getPersonalBest(BridgerGameType gameType) {
        return this.personalBests.get(gameType);
    }

    public Map<BridgerGameType, HyriBridgerDuration> getPersonalBests() {
        return personalBests;
    }

    public void setHighestAcquiredMedal(BridgerGameType gameType, Medal medal) {
        this.highestAcquiredMedals.remove(gameType);
        this.highestAcquiredMedals.put(gameType, medal);
    }

    public Medal getHighestAcquiredMedal(BridgerGameType gameType) {
        return this.highestAcquiredMedals.get(gameType);
    }

    public Map<BridgerGameType, Medal> getHighestAcquiredMedals() {
        return highestAcquiredMedals;
    }

    public long getBlocksPlaced() {
        return blocksPlaced;
    }

    public void addBlocksPlaced(long blocksPlaced) {
        this.blocksPlaced += blocksPlaced;
    }

    public long getBridgesMade() {
        return bridgesMade;
    }

    public void addBridgesMade(long bridgesMade) {
        this.bridgesMade += bridgesMade;
    }

    public long getBridgeFailed() {
        return bridgeFailed;
    }

    public void addBridgeFailed(long bridgeFailed) {
        this.bridgeFailed += bridgeFailed;
    }

    public long getPlayedTimeInMs() {
        return playedTimeInMs;
    }

    public void addPlayedTimeInMs(long playedTimeInMs) {
        this.playedTimeInMs += playedTimeInMs;
    }

    public void update() {
        IHyriPlayer player = IHyriPlayer.get(this.uuid);
        player.addStatistics("bridger", this);
        player.update();
    }

    public static HyriBridgerStats get(IHyriPlayer account) {
        HyriBridgerStats statistics = account.getStatistics("bridger", HyriBridgerStats.class);

        if (statistics == null) {
            statistics = new HyriBridgerStats(account.getUniqueId());
            statistics.update();
        }

        return statistics;
    }

    public static HyriBridgerStats get(UUID playerId) {
        return get(HyriAPI.get().getPlayerManager().getPlayer(playerId));
    }
}
