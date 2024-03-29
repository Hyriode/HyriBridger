package fr.hyriode.bridger.api;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BridgerData implements IHyriPlayerData {

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


    public int getSelectedBlockId() {
        return selectedBlockId;
    }

    public BridgerBlock getSelectedBlock() {
        return BridgerBlock.getById(this.selectedBlockId);
    }

    public void setSelectedBlockId(int selectedBlockId) {
        this.selectedBlockId = selectedBlockId;
    }

    public void setSelectedBlock(BridgerBlock block, Player player) {
        this.selectedBlockId = block.getId();
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId()));
    }

    public void update(IHyriPlayer account) {
        account.getData().add("bridger", this);
        account.update();
    }

    public static BridgerData get(IHyriPlayer account) {
        if (!account.getData().has("bridger")) {
            BridgerData data = new BridgerData();
            data.update(account);
        }
        return account.getData().read("bridger", new BridgerData());
    }

    public static BridgerData get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

}
