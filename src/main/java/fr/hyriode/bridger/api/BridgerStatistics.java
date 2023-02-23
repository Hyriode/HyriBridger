package fr.hyriode.bridger.api;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.mongodb.MongoDocument;
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
        final Document dataDocument = new Document();
        for (Map.Entry<BridgerGameType, Data> entry : this.data.entrySet()) {
            dataDocument.append(entry.getKey().name(), entry.getValue().toDocument());
        }
        document.append("data", dataDocument);
    }

    @Override
    public void load(MongoDocument document) {
        this.data.clear();
        for (Map.Entry<String, Object> entry : document.get("data", Document.class).entrySet()) {
            this.data.put(BridgerGameType.valueOf(entry.getKey()), Data.fromDocument((Document) entry.getValue()));
        }
    }

    public Data getData(BridgerGameType gameType) {
        Data data = this.data.get(gameType);

        if (data == null) {
            data = new Data();
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
        BridgerStatistics statistics = account.getStatistics().get("bridger");

        if (statistics == null) {
            statistics = new BridgerStatistics();
            statistics.update(account);
        }

        return statistics;
    }

    public static BridgerStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data {

        private BridgerDuration personalBest;
        private BridgerMedal highestAcquiredBridgerMedal;
        private int blocksPlaced;
        private int bridgesMade;
        private int bridgeFailed;
        private long playedTime;

        public Data() {
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

        public long getPlayedTime() {
            return playedTime;
        }

        public void setPlayedTime(long playedTime) {
            this.playedTime = playedTime;
        }

        public Document toDocument() {
            final Document document = new Document();
            document.append("personalBest", this.personalBest != null ? this.personalBest.getExactTime() : 0);
            document.append("highestAcquiredMedal", this.highestAcquiredBridgerMedal != null ? this.highestAcquiredBridgerMedal.name() : null);
            document.append("blocksPlaced", this.blocksPlaced);
            document.append("bridgesMade", this.bridgesMade);
            document.append("bridgeFailed", this.bridgeFailed);
            document.append("playedTime", this.playedTime);
            return document;
        }

        public static Data fromDocument(Document document) {
            final Data data = new Data();
            data.personalBest = new BridgerDuration(document.getLong("personalBest"));
            data.highestAcquiredBridgerMedal = document.getString("highestAcquiredMedal") != null ? BridgerMedal.valueOf(document.getString("highestAcquiredMedal")) : null;
            data.blocksPlaced = document.getInteger("blocksPlaced");
            data.bridgesMade = document.getInteger("bridgesMade");
            data.bridgeFailed = document.getInteger("bridgeFailed");
            data.playedTime = document.getLong("playedTime");
            return data;
        }
    }
}
