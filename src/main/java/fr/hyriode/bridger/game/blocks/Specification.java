package fr.hyriode.bridger.game.blocks;

import fr.hyriode.api.rank.IHyriRankType;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.bridger.api.BridgerMedal;

import java.util.Optional;

public enum Specification {

    DEFAULT("gui.lore.block.buyable-block"),
    VIP("gui.lore.block.rank-needed-block.vip", PlayerRank.VIP),
    VIP_PLUS("gui.lore.block.rank-needed-block.vip+", PlayerRank.VIP_PLUS),
    EPIC("gui.lore.block.rank-needed-block.epic", PlayerRank.EPIC),
    MEDAL_BRONZE("gui.lore.block.medal-needed-block.bronze", BridgerMedal.BRONZE),
    MEDAL_IRON("gui.lore.block.medal-needed-block.iron", BridgerMedal.IRON),
    MEDAL_GOLD("gui.lore.block.medal-needed-block.gold", BridgerMedal.GOLD),
    MEDAL_ULTIMATE("gui.lore.block.medal-needed-block.ultimate", BridgerMedal.ULTIMATE),
    STAFF("gui.lore.block.rank-needed-block.staff", StaffRank.HELPER);

    private final String loreKey;
    private final IHyriRankType rankType;
    private final BridgerMedal medal;

    Specification(String loreKey) {
        this.loreKey = loreKey;
        this.rankType = null;
        this.medal = null;
    }

    Specification(String loreKey, IHyriRankType rank) {
        this.loreKey = loreKey;
        this.rankType = rank;
        this.medal = null;
    }

    Specification(String loreKey, BridgerMedal medal) {
        this.loreKey = loreKey;
        this.rankType = null;
        this.medal = medal;
    }

    public String getLoreKey() {
        return loreKey;
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
