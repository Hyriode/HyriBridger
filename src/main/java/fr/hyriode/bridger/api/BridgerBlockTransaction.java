package fr.hyriode.bridger.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.model.IHyriTransactionContent;
import fr.hyriode.bridger.game.blocks.BridgerBlock;

public class BridgerBlockTransaction implements IHyriTransactionContent {

    private BridgerBlock block;

    public BridgerBlockTransaction(BridgerBlock block) {
        this.block = block;
    }

    public BridgerBlockTransaction() {
    }

    public BridgerBlock getBlock() {
        return block;
    }

    @Override
    public void save(MongoDocument document) {
        document.append("block", this.block.getId());
    }

    @Override
    public void load(MongoDocument document) {
        this.block = BridgerBlock.getById(document.getInteger("block"));
    }
}
