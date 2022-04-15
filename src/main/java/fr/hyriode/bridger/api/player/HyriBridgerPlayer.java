package fr.hyriode.bridger.api.player;

import java.util.UUID;

public class HyriBridgerPlayer {

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
