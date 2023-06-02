package fr.hyriode.bridger.game.blocks;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import org.bukkit.Material;

import java.util.*;

import static fr.hyriode.bridger.game.blocks.Specification.*;
import static org.bukkit.Material.*;

public enum BridgerBlock {

    SANDSTONE(Material.SANDSTONE),
    SANDSTONE_SLAB(STEP, 1, 0),
    SANDSTONE_STAIRS(Material.SANDSTONE_STAIRS),
    WHITE_WOOL(WOOL, 750),
    ORANGE_WOOL(WOOL, 1, 750),
    MAGENTA_WOOL(WOOL, 2, 750),
    LIGHT_BLUE_WOOL(WOOL, 3, 750),
    YELLOW_WOOL(WOOL, 4, 750),
    LIME_WOOL(WOOL, 5, 750),
    PINK_WOOL(WOOL, 6, 750),
    GRAY_WOOL(WOOL, 7, 750),
    LIGHT_GRAY_WOOL(WOOL, 8, 750),
    CYAN_WOOL(WOOL, 9, 750),
    PURPLE_WOOL(WOOL, 10, 750),
    BLUE_WOOL(WOOL, 11, 750),
    BROWN_WOOL(WOOL, 12, 750),
    GREEN_WOOL(WOOL, 13, 750),
    RED_WOOL(WOOL, 14, 750),
    BLACK_WOOL(WOOL, 15, 750),
    WHITE_CLAY(STAINED_CLAY, 750),
    ORANGE_CLAY(STAINED_CLAY, 1, 750),
    MAGENTA_CLAY(STAINED_CLAY, 2, 750),
    LIGHT_BLUE_CLAY(STAINED_CLAY, 3, 750),
    YELLOW_CLAY(STAINED_CLAY, 4, 750),
    LIME_CLAY(STAINED_CLAY, 5, 750),
    PINK_CLAY(STAINED_CLAY, 6, 750),
    GRAY_CLAY(STAINED_CLAY, 7, 750),
    LIGHT_GRAY_CLAY(STAINED_CLAY, 8, 750),
    CYAN_CLAY(STAINED_CLAY, 9, 750),
    PURPLE_CLAY(STAINED_CLAY, 10, 750),
    BLUE_CLAY(STAINED_CLAY, 11, 750),
    BROWN_CLAY(STAINED_CLAY, 12, 750),
    GREEN_CLAY(STAINED_CLAY, 13, 750),
    RED_CLAY(STAINED_CLAY, 14, 750),
    BLACK_CLAY(STAINED_CLAY, 15, 750),
    COAL_ORE(Material.COAL_ORE,1500),
    IRON_ORE(Material.IRON_ORE,1500),
    REDSTONE_ORE(Material.REDSTONE_ORE,1500),
    LAPIS_ORE(Material.LAPIS_ORE,1500),
    QUARTZ_ORE(Material.QUARTZ_ORE,1500),
    GOLD_ORE(Material.GOLD_ORE, 1500),
    DIAMOND_ORE(Material.DIAMOND_ORE, 0, 1500),
    EMERALD_ORE(Material.EMERALD_ORE, 0, 1500),
    BEDROCK(Material.BEDROCK, 10000),
    CHISELED_SANDSTONE(Material.SANDSTONE, 1, VIP),
    SMOOTH_SANDSTONE(Material.SANDSTONE, 2, VIP),
    RED_SANDSTONE(Material.RED_SANDSTONE, VIP),
    CHISELED_RED_SANDSTONE(Material.RED_SANDSTONE, 1, VIP),
    SMOOTH_RED_SANDSTONE(Material.RED_SANDSTONE, 2, VIP),
    WHITE_GLASS(STAINED_GLASS, VIP_PLUS),
    ORANGE_GLASS(STAINED_GLASS, 1, VIP_PLUS),
    MAGENTA_GLASS(STAINED_GLASS, 2, VIP_PLUS),
    LIGHT_BLUE_GLASS(STAINED_GLASS, 3, VIP_PLUS),
    YELLOW_GLASS(STAINED_GLASS, 4, VIP_PLUS),
    LIME_GLASS(STAINED_GLASS, 5, VIP_PLUS),
    PINK_GLASS(STAINED_GLASS, 6, VIP_PLUS),
    GRAY_GLASS(STAINED_GLASS, 7, VIP_PLUS),
    LIGHT_GRAY_GLASS(STAINED_GLASS, 8, VIP_PLUS),
    CYAN_GLASS(STAINED_GLASS, 9, VIP_PLUS),
    PURPLE_GLASS(STAINED_GLASS, 10, VIP_PLUS),
    BLUE_GLASS(STAINED_GLASS, 11, VIP_PLUS),
    BROWN_GLASS(STAINED_GLASS, 12, VIP_PLUS),
    GREEN_GLASS(STAINED_GLASS, 13, VIP_PLUS),
    RED_GLASS(STAINED_GLASS, 14, VIP_PLUS),
    BLACK_GLASS(STAINED_GLASS, 15, VIP_PLUS),
    COAL_BLOCK(Material.COAL_BLOCK, MEDAL_BRONZE),
    IRON_BLOCK(Material.IRON_BLOCK, MEDAL_IRON),
    GOLD_BLOCK(Material.GOLD_BLOCK, MEDAL_GOLD),
    DIAMOND_BLOCK(Material.DIAMOND_BLOCK, MEDAL_ULTIMATE),
    BRIDGER_TNT(TNT, STAFF),
    ;

    private static final Map<Integer, BridgerBlock> BY_ID = new HashMap<>();

    static {
        for (BridgerBlock bridgerBlock : values()) {
            BY_ID.put(bridgerBlock.getId(), bridgerBlock);
        }
    }

    private final String nameKey;
    private final Material material;
    private final short meta;
    private final int cost;
    private final Specification specificationNeeded;

    BridgerBlock(Material material) {
        this(material, 0, -1, DEFAULT);
    }

    BridgerBlock(Material material, Specification specificationNeeded) {
        this(material, 0, 0, specificationNeeded);
    }

    BridgerBlock(Material material, int cost) {
        this(material, 0, cost, DEFAULT);
    }

    BridgerBlock(Material material, int meta, int cost) {
        this(material, meta, cost, DEFAULT);
    }

    BridgerBlock(Material material, int meta, Specification specificationNeeded) {
        this(material, meta, 0, specificationNeeded);
    }

    BridgerBlock(Material material, int meta, int cost, Specification specificationNeeded) {
        this.nameKey = "block." + this.name().toLowerCase().replace("_", "-");
        this.material = material;
        this.meta = (short) meta;
        this.cost = cost;
        this.specificationNeeded = specificationNeeded;
    }

    public int getId() {
        return this.ordinal();
    }

    public String getItemStackName(UUID player) {
        try {
            return HyriLanguageMessage.get(this.nameKey).getValue(player);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAbsoluteName(UUID player) {
        if (this.nameKey != null) {
            return HyriLanguageMessage.get(this.nameKey).getValue(player);
        }
        return this.material.name().toLowerCase();
    }

    public List<String> getNotPossessedLore(UUID player)  {
        System.out.println(this);
        System.out.println(this.specificationNeeded.getMessage());
        System.out.println(this.cost);
        String baseString = this.specificationNeeded.getMessage().asString(player).replace("%cost%", String.valueOf(this.cost));
        return Arrays.asList(baseString.split("\n"));
    }

    public List<String> getNotBuyableLore(IHyriPlayer account)  {
        String baseString = BridgerMessage.GUI_LORE_BLOCK_NOT_BUYABLE_BLOCK.asString(account)
                .replace("%cost%", String.valueOf(this.cost))
                .replace("%missing%", String.valueOf(this.getCost() - account.getHyris().getAmount()));
        return Arrays.asList(baseString.split("\n"));
    }

    public Material getMaterial() {
        return material;
    }

    public short getMeta() {
        return meta;
    }

    public int getCost() {
        return cost;
    }

    public Specification getSpecificationNeeded() {
        return specificationNeeded;
    }

    public static BridgerBlock getById(int id) {
        return BY_ID.get(id);
    }
}
