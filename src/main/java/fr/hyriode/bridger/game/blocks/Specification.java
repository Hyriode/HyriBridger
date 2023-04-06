package fr.hyriode.bridger.game.blocks;

import fr.hyriode.api.rank.IHyriRankType;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.bridger.api.BridgerMedal;
import fr.hyriode.bridger.language.BridgerMessage;

import java.util.Optional;

import static fr.hyriode.bridger.language.BridgerMessage.*;

public enum Specification {

    DEFAULT(GUI_LORE_BLOCK_BUYABLE_BLOCK),
    VIP(GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_VIP, PlayerRank.VIP),
    VIP_PLUS(GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_VIP_PLUS, PlayerRank.VIP_PLUS),
    EPIC(GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_EPIC, PlayerRank.EPIC),
    MEDAL_BRONZE(GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_BRONZE, BridgerMedal.BRONZE),
    MEDAL_IRON(GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_IRON, BridgerMedal.IRON),
    MEDAL_GOLD(GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_GOLD, BridgerMedal.GOLD),
    MEDAL_ULTIMATE(GUI_LORE_BLOCK_MEDAL_NEEDED_BLOCK_ULTIMATE, BridgerMedal.ULTIMATE),
    STAFF(GUI_LORE_BLOCK_RANK_NEEDED_BLOCK_STAFF, StaffRank.HELPER);

    private final BridgerMessage message;
    private final IHyriRankType rankType;
    private final BridgerMedal medal;

    Specification(BridgerMessage message) {
        this.message = message;
        this.rankType = null;
        this.medal = null;
    }

    Specification(BridgerMessage message, IHyriRankType rank) {
        this.message = message;
        this.rankType = rank;
        this.medal = null;
    }

    Specification(BridgerMessage message, BridgerMedal medal) {
        this.message = message;
        this.rankType = null;
        this.medal = medal;
    }

    public BridgerMessage getMessage() {
        return message;
    }

    public Optional<IHyriRankType> getOptionalRankType() {
        return Optional.ofNullable(rankType);
    }

    public Optional<BridgerMedal> getOptionalMedal() {
        return Optional.ofNullable(medal);
    }


    public IHyriRankType getRankType() {
        return rankType;
    }

    public BridgerMedal getMedal() {
        return medal;
    }
}
