package fr.hyriode.bridger.api;

import fr.hyriode.api.transaction.IHyriTransactionContent;

public class BlockTransaction implements IHyriTransactionContent {

    private final int blockId;

    public BlockTransaction(int blockId) {
        this.blockId = blockId;
    }

    public int getBlockId() {
        return blockId;
    }
}
