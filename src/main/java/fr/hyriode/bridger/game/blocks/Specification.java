package fr.hyriode.bridger.game.blocks;

public enum Specification {

    DEFAULT("gui.lore.block.buyable-block"),
    VIP("gui.lore.block.rank-needed-block.vip"),
    VIP_PLUS("gui.lore.block.rank-needed-block.vip+"),
    EPIC("gui.lore.block.rank-needed-block.epic"),
    MEDAL_BRONZE("gui.lore.block.medal-needed-block.bronze"),
    MEDAL_IRON("gui.lore.block.medal-needed-block.iron"),
    MEDAL_GOLD("gui.lore.block.medal-needed-block.gold"),
    MEDAL_ULTIMATE("gui.lore.block.medal-needed-block.ultimate"),
    STAFF("gui.lore.block.rank-needed-block.staff"),
    ;

    private final String loreKey;

    Specification(String loreKey) {
        this.loreKey = loreKey;
    }

    public String getLoreKey() {
        return loreKey;
    }

}
