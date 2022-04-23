package fr.hyriode.bridger.api.player;

import fr.hyriode.api.player.HyriPlayerData;

import java.util.UUID;

public class HyriBridgerPlayer extends HyriPlayerData {

    private final UUID uniqueId;
    private final HyriBridgerStatistics statistics;

    public HyriBridgerPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.statistics = new HyriBridgerStatistics();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public HyriBridgerStatistics getStatistics() {
        return statistics;
    }
}
