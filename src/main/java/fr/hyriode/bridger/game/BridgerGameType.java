package fr.hyriode.bridger.game;

import fr.hyriode.hyrame.game.HyriGameType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum BridgerGameType implements HyriGameType {

    SHORT("SHORT","Short", 1, 30, new ItemStack(Material.STEP, 1, (short)1)),
    NORMAL("NORMAL", "Normal", 1, 30, new ItemStack(Material.SANDSTONE)),
    DIAGONAL("DIAGONAL", "Diagonal", 1, 30, new ItemStack(Material.SANDSTONE_STAIRS)),
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
