package fr.hyriode.bridger.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.model.IHyriTransactionContent;

public class BridgerBlockTransaction implements IHyriTransactionContent {

    private int blockId;

    public BridgerBlockTransaction(int blockId) {
        this.blockId = blockId;
    }

    public BridgerBlockTransaction() {
    }

    public int getBlockId() {
        return blockId;
    }

    @Override
    public void save(MongoDocument document) {
        document.append("blockId", this.blockId);
    }

    @Override
    public void load(MongoDocument document) {
        this.blockId = document.getInteger("blockId");
    }
}
