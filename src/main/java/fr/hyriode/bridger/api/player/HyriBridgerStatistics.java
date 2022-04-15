package fr.hyriode.bridger.api.player;

import fr.hyriode.bridger.api.duration.HyriBridgerDuration;

public class HyriBridgerStatistics {

    private HyriBridgerDuration personalBest;
    private long blocksPlaced;
    private long bridgesMade;
    private long bridgeFailed;
    private long playedTimeInMs;
    private Medal highestAcquiredMedal;


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

    public Medal getHighestAcquiredMedal() {
        return highestAcquiredMedal;
    }

    public void setHighestAcquiredMedal(Medal highestAcquiredMedal) {
        this.highestAcquiredMedal = highestAcquiredMedal;
    }
}
