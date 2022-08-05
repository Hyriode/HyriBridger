package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.api.BlockTransaction;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.game.blocks.BridgerBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HyriBridgerData extends HyriPlayerData {

    private int actualBlockId;
    private final List<Integer> possessedBlocksId;
    private int oldRankId;

    public HyriBridgerData() {
        this.actualBlockId = 0;
        this.possessedBlocksId = new ArrayList<>(Arrays.asList(0, 1, 2));
        this.oldRankId = -1;
    }

    public static void updatePossessedBlocks(UUID uuid) {
        HyriBridgerData account =  HyriBridgerData.get(uuid);
        IHyriPlayer hyriAccount = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        if (account.oldRankId == -1 || account.oldRankId != hyriAccount.getRank().getPlayerType().getId()) {
            account.setOldRankId(hyriAccount.getRank().getPlayerType().getId());
            int rankId = hyriAccount.getRank().getPlayerType().getId();
            switch(rankId) {
                case 4:
                case 3:
                    /*for (int i = 65; i < 67; i++) {
                        account.addPossessedBlock(i);
                    }*/
                case 2:
                    for (int i = 49; i < 65; i++) {
                        account.addPossessedBlock(i, uuid);
                    }
                case 1:
                    for (int i = 44; i < 49; i++) {
                        account.addPossessedBlock(i, uuid);
                    }
                    break;
            }
        }
        if (hyriAccount.getRank().isStaff()) {
            account.addPossessedBlock(69, uuid);
        }
        account.update(uuid);
    }

    public void addBlockForMedal(Medal medal, UUID uuid) {
        switch (medal) {
            case ULTIMATE:
                this.addPossessedBlock(68, uuid);
            case GOLD:
                this.addPossessedBlock(67, uuid);
            case IRON:
                this.addPossessedBlock(66, uuid);
            case BRONZE:
                this.addPossessedBlock(65, uuid);
        }
    }

    public void addPossessedBlock(int id, UUID uuid) {
        if (!this.possessedBlocksId.contains(id)) {
            IHyriPlayer account = IHyriPlayer.get(uuid);
            account.addTransaction("bridger", String.valueOf(id), new BlockTransaction(id));
            if(BridgerBlock.getById(id).getCost() > 0) {
                account.getHyris().remove(BridgerBlock.getById(id).getCost());
            }
            account.update();
        }
    }

    public int getActualBlockId() {
        return actualBlockId;
    }

    public void setActualBlockId(int actualBlockId) {
        this.actualBlockId = actualBlockId;
    }

    public void setOldRankId(int oldRankId) {
        this.oldRankId = oldRankId;
    }

    public boolean hasBlock(UUID uuid, int id) {
        if(this.possessedBlocksId.contains(id)) {
            return true;
        }
        return IHyriPlayer.get(uuid).getTransaction("bridger", String.valueOf(id)) != null;
    }

    public void update(IHyriPlayer account) {
        account.addData("bridger", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player));
    }

    public static HyriBridgerData get(IHyriPlayer account) {
        HyriBridgerData data = account.getData("bridger", HyriBridgerData.class);

        if (data == null) {
            data = new HyriBridgerData();
            data.update(account);
        }
        return data;
    }

    public static HyriBridgerData get(UUID playerId) {
        return get(HyriAPI.get().getPlayerManager().getPlayer(playerId));
    }

}
