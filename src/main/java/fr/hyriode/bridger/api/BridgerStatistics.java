package fr.hyriode.bridger.api;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.bridger.game.BridgerGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BridgerStatistics implements IHyriStatistics {

    private final Map<BridgerGameType, Data> data;

    public BridgerStatistics() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<BridgerGameType, Data> entry : this.data.entrySet()) {
            final MongoDocument dataDocument = new MongoDocument();

            entry.getValue().save(dataDocument);

            document.append(entry.getKey().name(), dataDocument);
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final Data data = new Data();

            data.load(MongoDocument.of((Document) entry.getValue()));

            this.data.put(BridgerGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(BridgerGameType gameType) {
        Data data = this.data.get(gameType);

        if (data == null) {
            data = new Data(null, null, 0, 0, 0, 0);
            this.data.put(gameType, data);
        }

        return data;
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add("bridger", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player));
    }

    public static BridgerStatistics get(IHyriPlayer account) {
        if (!account.getStatistics().has("bridger")) {
            BridgerStatistics statistics = new BridgerStatistics();
            statistics.update(account);
        }

        return account.getStatistics().read("bridger", new BridgerStatistics());
    }

    public static BridgerStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private BridgerDuration personalBest;
        private BridgerMedal highestAcquiredBridgerMedal;
        private int blocksPlaced;
        private int bridgesMade;
        private int bridgeFailed;

        public Data() {
        }

        public Data(BridgerDuration personalBest, BridgerMedal highestAcquiredBridgerMedal, int blocksPlaced, int bridgesMade, int bridgeFailed, long playedTime) {
            this.personalBest = personalBest;
            this.highestAcquiredBridgerMedal = highestAcquiredBridgerMedal;
            this.blocksPlaced = blocksPlaced;
            this.bridgesMade = bridgesMade;
            this.bridgeFailed = bridgeFailed;
        }

        public BridgerDuration getPersonalBest() {
            return personalBest;
        }

        public void setPersonalBest(BridgerDuration personalBest) {
            this.personalBest = personalBest;
        }

        public BridgerMedal getHighestAcquiredMedal() {
            return highestAcquiredBridgerMedal;
        }

        public void setHighestAcquiredMedal(BridgerMedal highestAcquiredBridgerMedal) {
            this.highestAcquiredBridgerMedal = highestAcquiredBridgerMedal;
        }

        public long getBlocksPlaced() {
            return blocksPlaced;
        }

        public void setBlocksPlaced(int blocksPlaced) {
            this.blocksPlaced = blocksPlaced;
        }

        public void addBlocksPlaced(int blocksPlaced) {
            this.blocksPlaced += blocksPlaced;
        }

        public long getBridgesMade() {
            return bridgesMade;
        }

        public void setBridgesMade(int bridgesMade) {
            this.bridgesMade = bridgesMade;
        }

        public void addBridgesMade(int bridgesMade) {
            this.bridgesMade += bridgesMade;
        }

        public long getBridgeFailed() {
            return bridgeFailed;
        }

        public void setBridgeFailed(int bridgeFailed) {
            this.bridgeFailed = bridgeFailed;
        }

        public void addBridgeFailed(int bridgeFailed) {
            this.bridgeFailed += bridgeFailed;
        }

        @Override
        public void save(MongoDocument document) {
            document.append("personalBest", this.personalBest != null ? this.personalBest.getExactTime() : 0);
            document.append("highestAcquiredMedal", this.highestAcquiredBridgerMedal != null ? this.highestAcquiredBridgerMedal.name() : null);
            document.append("blocksPlaced", this.blocksPlaced);
            document.append("bridgesMade", this.bridgesMade);
            document.append("bridgeFailed", this.bridgeFailed);
        }

        @Override
        public void load(MongoDocument document) {
            this.personalBest = document.containsKey("personalBest") ? new BridgerDuration(document.getLong("personalBest")) : null;
            this.highestAcquiredBridgerMedal = document.getString("highestAcquiredMedal") != null ? BridgerMedal.valueOf(document.getString("highestAcquiredMedal")) : null;
            this.blocksPlaced = document.containsKey("blocksPlaced") ? document.getInteger("blocksPlaced") : 0;
            this.bridgesMade = document.containsKey("bridgesMade") ? document.getInteger("bridgesMade") : 0;
            this.bridgeFailed = document.containsKey("bridgeFailed") ? document.getInteger("bridgeFailed") : 0;
        }
    }
}



