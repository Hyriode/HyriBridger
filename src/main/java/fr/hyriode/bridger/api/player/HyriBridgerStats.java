package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;

import java.util.UUID;

public class HyriBridgerStats extends HyriPlayerData {

    private final UUID uuid;
    private HyriBridgerDuration personalShortBest;
    private HyriBridgerDuration personalNormalBest;
    private HyriBridgerDuration personalDiagonalBest;
    private Medal highestAcquiredShortMedal;
    private Medal highestAcquiredNormalMedal;
    private Medal highestAcquiredDiagonalMedal;
    private long blocksPlaced;
    private long bridgesMade;
    private long bridgeFailed;
    private long playedTimeInMs;

    public HyriBridgerStats(UUID uuid) {
        this.uuid = uuid;
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

    public HyriBridgerDuration getPersonalShortBest() {
        return personalShortBest;
    }

    public void setPersonalShortBest(HyriBridgerDuration personalShortBest) {
        this.personalShortBest = personalShortBest;
    }

    public HyriBridgerDuration getPersonalNormalBest() {
        return personalNormalBest;
    }

    public void setPersonalNormalBest(HyriBridgerDuration personalNormalBest) {
        this.personalNormalBest = personalNormalBest;
    }

    public HyriBridgerDuration getPersonalDiagonalBest() {
        return personalDiagonalBest;
    }

    public void setPersonalDiagonalBest(HyriBridgerDuration personalDiagonalBest) {
        this.personalDiagonalBest = personalDiagonalBest;
    }

    public Medal getHighestAcquiredShortMedal() {
        return highestAcquiredShortMedal;
    }

    public void setHighestAcquiredShortMedal(Medal highestAcquiredShortMedal) {
        this.highestAcquiredShortMedal = highestAcquiredShortMedal;
    }

    public Medal getHighestAcquiredNormalMedal() {
        return highestAcquiredNormalMedal;
    }

    public void setHighestAcquiredNormalMedal(Medal highestAcquiredNormalMedal) {
        this.highestAcquiredNormalMedal = highestAcquiredNormalMedal;
    }

    public Medal getHighestAcquiredDiagonalMedal() {
        return highestAcquiredDiagonalMedal;
    }

    public void setHighestAcquiredDiagonalMedal(Medal highestAcquiredDiagonalMedal) {
        this.highestAcquiredDiagonalMedal = highestAcquiredDiagonalMedal;
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