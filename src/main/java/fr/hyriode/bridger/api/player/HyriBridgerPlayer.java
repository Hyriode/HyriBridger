package fr.hyriode.bridger.api.player;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;

import java.util.UUID;

public class HyriBridgerPlayer extends HyriPlayerData {

    private HyriBridgerDuration personalBest;
    private long blocksPlaced;
    private long bridgesMade;
    private long bridgeFailed;
    private long playedTimeInMs;
    private int highestAcquiredShortMedal;
    private int highestAcquiredLongMedal;
    private int highestAcquiredDiagonalMedal;
    private final UUID uuid;

    public HyriBridgerPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public HyriBridgerDuration getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(HyriBridgerDuration personalBest) {
        this.personalBest = personalBest;
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

    public int getHighestAcquiredShortMedal() {
        return highestAcquiredShortMedal;
    }

    public void setHighestAcquiredShortMedal(int highestAcquiredShortMedal) {
        this.highestAcquiredShortMedal = highestAcquiredShortMedal;
    }

    public int getHighestAcquiredLongMedal() {
        return highestAcquiredLongMedal;
    }

    public void setHighestAcquiredLongMedal(int highestAcquiredLongMedal) {
        this.highestAcquiredLongMedal = highestAcquiredLongMedal;
    }

    public int getHighestAcquiredDiagonalMedal() {
        return highestAcquiredDiagonalMedal;
    }

    public void setHighestAcquiredDiagonalMedal(int highestAcquiredDiagonalMedal) {
        this.highestAcquiredDiagonalMedal = highestAcquiredDiagonalMedal;
    }

    public UUID getUUID() {
        return uuid;
    }
}
