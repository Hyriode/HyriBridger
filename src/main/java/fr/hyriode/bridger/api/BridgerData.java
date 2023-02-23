package fr.hyriode.bridger.api;

import com.google.gson.annotations.Expose;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.bridger.game.blocks.BridgerBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BridgerData implements IHyriPlayerData {

    @Expose private final List<BridgerBlock> unlockedBlocks = new ArrayList<>();
    private int selectedBlockId;

    public BridgerData(int selectedBlockId) {
        this.selectedBlockId = selectedBlockId;
    }

    public BridgerData() {
    }

    @Override
    public void save(MongoDocument document) {
        document.append("selectedBlockId", this.selectedBlockId);
    }

    @Override
    public void load(MongoDocument document) {
        this.selectedBlockId = document.getInteger("selectedBlockId");
    }

    public boolean hasUnlocked(BridgerBlock block) {
        return this.unlockedBlocks.contains(block);
    }

    public int getSelectedBlockId() {
        return selectedBlockId;
    }

    public void setSelectedBlockId(int selectedBlockId) {
        this.selectedBlockId = selectedBlockId;
    }

    public void update(IHyriPlayer account) {
        account.getData().add("bridger", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player));
    }

    public static BridgerData get(IHyriPlayer account) {
        BridgerData data = account.getData().get("bridger");

        if (data == null) {
            data = new BridgerData();
            data.update(account);
        }

        return data;
    }

    public static BridgerData get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }
}
