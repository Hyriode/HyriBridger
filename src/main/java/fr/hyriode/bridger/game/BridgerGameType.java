package fr.hyriode.bridger.game;

import fr.hyriode.hyrame.game.HyriGameType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Project: HyriBridger
 * Created by Akkashi
 * on 23/04/2022 at 14:25
 */
public enum BridgerGameType implements HyriGameType {

    SHORT("short","Short", 1, 30, new ItemStack(Material.STONE_SLAB2, 1)),
    NORMAL("normal","Normal", 1, 30, new ItemStack(Material.SANDSTONE)),
    DIAGONAL("diagonal","Diagonal", 1, 30, new ItemStack(Material.SANDSTONE_STAIRS)),
    ;

    private final String name;
    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;
    private final ItemStack itemstack;

    BridgerGameType(String name, String displayName, int minPlayers, int maxPlayers, ItemStack itemstack) {
        this.name = name;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.itemstack = itemstack;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public ItemStack getItemstack() {
        return itemstack;
    }
}
