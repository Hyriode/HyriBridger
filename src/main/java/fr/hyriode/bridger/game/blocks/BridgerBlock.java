package fr.hyriode.bridger.game.blocks;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import org.bukkit.Material;

public enum BridgerBlock {

    SANDSTONE(0, Material.SANDSTONE, (short) 0, 0),
    SANDSTONE_SLAB(1, Material.STONE_SLAB2, (short) 1, 0),
    SANDSTONE_STAIRS(2, Material.SANDSTONE, (short) 0, 0),
    WHITE_WOOL(3, Material.WOOL, (short) 0, 750),
    ORANGE_WOOL(4, Material.WOOL, (short) 1, 750),
    MAGENTA_WOOL(5, Material.WOOL, (short) 2, 750),
    LIGHT_BLUE_WOOL(6, Material.WOOL, (short) 3, 750),
    YELLOW_WOOL(7, Material.WOOL, (short) 4, 750),
    LIME_WOOL(8, Material.WOOL, (short) 5, 750),
    PINK_WOOL(9, Material.WOOL, (short) 6, 750),
    GRAY_WOOL(10, Material.WOOL, (short) 7, 750),
    LIGHT_GRAY_WOOL(11, Material.WOOL, (short) 8, 750),
    CYAN_WOOL(12, Material.WOOL, (short) 9, 750),
    PURPLE_WOOL(13, Material.WOOL, (short) 10, 750),
    BLUE_WOOL(14, Material.WOOL, (short) 11, 750),
    BROWN_WOOL(15, Material.WOOL, (short) 12, 750),
    GREEN_WOOL(16, Material.WOOL, (short) 13, 750),
    RED_WOOL(17, Material.WOOL, (short) 14, 750),
    BLACK_WOOL(18, Material.WOOL, (short) 15, 750),
    WHITE_CLAY(19, Material.STAINED_CLAY, (short) 0, 750),
    ORANGE_CLAY(20, Material.STAINED_CLAY, (short) 1, 750),
    MAGENTA_CLAY(21, Material.STAINED_CLAY, (short) 2, 750),
    LIGHT_BLUE_CLAY(22, Material.STAINED_CLAY, (short) 3, 750),
    YELLOW_CLAY(23, Material.STAINED_CLAY, (short) 4, 750),
    LIME_CLAY(24, Material.STAINED_CLAY, (short) 5, 750),
    PINK_CLAY(25, Material.STAINED_CLAY, (short) 6, 750),
    GRAY_CLAY(26, Material.STAINED_CLAY, (short) 7, 750),
    LIGHT_GRAY_CLAY(27, Material.STAINED_CLAY, (short) 8, 750),
    CYAN_CLAY(28, Material.STAINED_CLAY, (short) 9, 750),
    PURPLE_CLAY(29, Material.STAINED_CLAY, (short) 10, 750),
    BLUE_CLAY(30, Material.STAINED_CLAY, (short) 11, 750),
    BROWN_CLAY(31, Material.STAINED_CLAY, (short) 12, 750),
    GREEN_CLAY(32, Material.STAINED_CLAY, (short) 13, 750),
    RED_CLAY(33, Material.STAINED_CLAY, (short) 14, 750),
    BLACK_CLAY(34, Material.STAINED_CLAY, (short) 15, 750),
    COAL_ORE(35, Material.COAL_ORE, (short) 0, 1500),
    IRON_ORE(36, Material.IRON_ORE, (short) 0, 1500),
    REDSTONE_ORE(37, Material.REDSTONE_ORE, (short) 0, 1500),
    LAPIS_ORE(38, Material.LAPIS_ORE, (short) 0, 1500),
    QUARTZ_ORE(39, Material.QUARTZ_ORE, (short) 0, 1500),
    GOLD_ORE(40, Material.GOLD_ORE, (short) 0, 1500),
    DIAMOND_ORE(41, Material.DIAMOND_ORE, (short) 0, 1500),
    EMERALD_ORE(42, Material.EMERALD_ORE, (short) 0, 1500),
    BEDROCK(43, Material.BEDROCK, (short) 0, 10000),
    CHISELED_SANDSTONE(44, Material.BEDROCK, (short) 0, -1),
    SMOOTH_SANDSTONE(45, Material.BEDROCK, (short) 0, -1),
    RED_SANDSTONE(46, Material.BEDROCK, (short) 0, -1),
    CHISELED_RED_SANDSTONE(47, Material.BEDROCK, (short) 0, -1),
    SMOOTH_RED_SANDSTONE(48, Material.BEDROCK, (short) 0, -1);


    private final int id;
    private final Material material;
    private final short meta;
    private final int cost;

    BridgerBlock(int id, Material material, short meta, int cost) {
        this.id = id;
        this.material = material;
        this.meta = meta;
        this.cost = cost;
    }

    public int getId() {
        return id;
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

    public static BridgerBlock getById(int id) {
        for (BridgerBlock bridgerBlock : BridgerBlock.values()) {
            if(bridgerBlock.getId() == id) {
                return bridgerBlock;
            }
        }
        throw new NotFoundException("Not found");
    }
}
