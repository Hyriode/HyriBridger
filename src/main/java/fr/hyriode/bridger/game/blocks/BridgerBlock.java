package fr.hyriode.bridger.game.blocks;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.Material;

import java.util.*;

import static fr.hyriode.bridger.game.blocks.Specification.*;
import static org.bukkit.Material.*;

public enum BridgerBlock {

    SANDSTONE(0, Material.SANDSTONE),
    SANDSTONE_SLAB(1, STEP, 1, 0),
    SANDSTONE_STAIRS(2, Material.SANDSTONE_STAIRS),
    WHITE_WOOL(3, WOOL, 750),
    ORANGE_WOOL(4, WOOL, 1, 750),
    MAGENTA_WOOL(5, WOOL, 2, 750),
    LIGHT_BLUE_WOOL(6, WOOL, 3, 750),
    YELLOW_WOOL(7, WOOL, 4, 750),
    LIME_WOOL(8, WOOL, 5, 750),
    PINK_WOOL(9, WOOL, 6, 750),
    GRAY_WOOL(10, WOOL, 7, 750),
    LIGHT_GRAY_WOOL(11, WOOL, 8, 750),
    CYAN_WOOL(12, WOOL, 9, 750),
    PURPLE_WOOL(13, WOOL, 10, 750),
    BLUE_WOOL(14, WOOL, 11, 750),
    BROWN_WOOL(15, WOOL, 12, 750),
    GREEN_WOOL(16, WOOL, 13, 750),
    RED_WOOL(17, WOOL, 14, 750),
    BLACK_WOOL(18, WOOL, 15, 750),
    WHITE_CLAY(19, STAINED_CLAY, 750),
    ORANGE_CLAY(20, STAINED_CLAY, 1, 750),
    MAGENTA_CLAY(21, STAINED_CLAY, 2, 750),
    LIGHT_BLUE_CLAY(22, STAINED_CLAY, 3, 750),
    YELLOW_CLAY(23, STAINED_CLAY, 4, 750),
    LIME_CLAY(24, STAINED_CLAY, 5, 750),
    PINK_CLAY(25, STAINED_CLAY, 6, 750),
    GRAY_CLAY(26, STAINED_CLAY, 7, 750),
    LIGHT_GRAY_CLAY(27, STAINED_CLAY, 8, 750),
    CYAN_CLAY(28, STAINED_CLAY, 9, 750),
    PURPLE_CLAY(29, STAINED_CLAY, 10, 750),
    BLUE_CLAY(30, STAINED_CLAY, 11, 750),
    BROWN_CLAY(31, STAINED_CLAY, 12, 750),
    GREEN_CLAY(32, STAINED_CLAY, 13, 750),
    RED_CLAY(33, STAINED_CLAY, 14, 750),
    BLACK_CLAY(34, STAINED_CLAY, 15, 750),
    COAL_ORE(35, Material.COAL_ORE,1500),
    IRON_ORE(36, Material.IRON_ORE,1500),
    REDSTONE_ORE(37, Material.REDSTONE_ORE,1500),
    LAPIS_ORE(38, Material.LAPIS_ORE,1500),
    QUARTZ_ORE(39, Material.QUARTZ_ORE,1500),
    GOLD_ORE(40, Material.GOLD_ORE, 1500),
    DIAMOND_ORE(41, Material.DIAMOND_ORE, 0, 1500),
    EMERALD_ORE(42, Material.EMERALD_ORE, 0, 1500),
    BEDROCK(43, Material.BEDROCK, 10000),
    CHISELED_SANDSTONE(44, Material.SANDSTONE, 1, VIP),
    SMOOTH_SANDSTONE(45, Material.SANDSTONE, 2, VIP),
    RED_SANDSTONE(46, Material.RED_SANDSTONE, VIP),
    CHISELED_RED_SANDSTONE(47, Material.RED_SANDSTONE, 1, VIP),
    SMOOTH_RED_SANDSTONE(48, Material.RED_SANDSTONE, 2, VIP),
    WHITE_GLASS(49, STAINED_GLASS, VIP_PLUS),
    ORANGE_GLASS(50, STAINED_GLASS, 1, VIP_PLUS),
    MAGENTA_GLASS(51, STAINED_GLASS, 2, VIP_PLUS),
    LIGHT_BLUE_GLASS(52, STAINED_GLASS, 3, VIP_PLUS),
    YELLOW_GLASS(53, STAINED_GLASS, 4, VIP_PLUS),
    LIME_GLASS(54, STAINED_GLASS, 5, VIP_PLUS),
    PINK_GLASS(55, STAINED_GLASS, 6, VIP_PLUS),
    GRAY_GLASS(56, STAINED_GLASS, 7, VIP_PLUS),
    LIGHT_GRAY_GLASS(57, STAINED_GLASS, 8, VIP_PLUS),
    CYAN_GLASS(58, STAINED_GLASS, 9, VIP_PLUS),
    PURPLE_GLASS(59, STAINED_GLASS, 10, VIP_PLUS),
    BLUE_GLASS(60, STAINED_GLASS, 11, VIP_PLUS),
    BROWN_GLASS(61, STAINED_GLASS, 12, VIP_PLUS),
    GREEN_GLASS(62, STAINED_GLASS, 13, VIP_PLUS),
    RED_GLASS(63, STAINED_GLASS, 14, VIP_PLUS),
    BLACK_GLASS(64, STAINED_GLASS, 15, VIP_PLUS),
    COAL_BLOCK(65, Material.COAL_BLOCK, MEDAL_BRONZE),
    IRON_BLOCK(66, Material.IRON_BLOCK, MEDAL_IRON),
    GOLD_BLOCK(67, Material.GOLD_BLOCK, MEDAL_GOLD),
    DIAMOND_BLOCK(68, Material.DIAMOND_BLOCK, MEDAL_ULTIMATE),
    BRIDGER_TNT(69, TNT, STAFF),
    ;

    private static final Map<Integer, BridgerBlock> BY_ID = new HashMap<>();
    static {
        for (BridgerBlock bridgerBlock : values()) {
            BY_ID.put(bridgerBlock.getId(), bridgerBlock);
        }
    }

    private final int id;
    private final String nameKey;
    private final Material material;
    private final short meta;
    private final int cost;
    private final Specification specificationNeeded;

    BridgerBlock(int id, Material material) {
        this(id, material, 0, 0, DEFAULT);
    }

    BridgerBlock(int id, Material material, Specification specificationNeeded) {
        this(id, material, 0, 0, specificationNeeded);
    }

    BridgerBlock(int id, Material material, int cost) {
        this(id, material, 0, cost, DEFAULT);
    }

    BridgerBlock(int id, Material material, int meta, int cost) {
        this(id, material, meta, cost, DEFAULT);
    }

    BridgerBlock(int id, Material material, int meta, Specification specificationNeeded) {
        this(id, material, meta, 0, specificationNeeded);
    }

    BridgerBlock(int id, Material material, int meta, int cost, Specification specificationNeeded) {
        this.id = id;
        this.nameKey = "block." + this.name().toLowerCase().replace("_", "-");
        this.material = material;
        this.meta = (short) meta;
        this.cost = cost;
        this.specificationNeeded = specificationNeeded;
    }

    public int getId() {
        return id;
    }

    public String getItemStackName(UUID player) {
        try {
            return HyriLanguageMessage.get(this.nameKey).getValue(player);
        } catch (NullPointerException e) {
            System.out.println("[Bridger] NullPointerException: " + this.nameKey);
            return HyriLanguageMessage.get("block.not-found").getValue(player);
        }
    }

    public String getAbsoluteName(UUID player) {
        if (this.nameKey != null) {
            return HyriLanguageMessage.get(this.nameKey).getValue(player);
        }
        return this.material.name().toLowerCase();
    }

    public List<String> getNotPossessedLore(UUID player)  {
        String baseString = HyriLanguageMessage.get(this.specificationNeeded.getLoreKey()).getValue(player)
                .replace("%cost%", String.valueOf(this.cost));
        return Arrays.asList(baseString.split("\n"));
    }

    public List<String> getNotBuyableLore(IHyriPlayer account)  {
        String baseString = HyriLanguageMessage.get("gui.lore.block.not-buyable-block").getValue(account.getUniqueId())
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
