package fr.hyriode.bridger.game;

import fr.hyriode.hyrame.game.HyriGameType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public enum BridgerGameType implements HyriGameType {

    SHORT("SHORT","Short", 1, 30, new ItemStack(Material.STEP, 1, (short)1), new IBridgerTypeHandler.ShortHandler()),
    NORMAL("NORMAL", "Normal", 1, 30, new ItemStack(Material.SANDSTONE), new IBridgerTypeHandler.NormalHandler()),
    DIAGONAL("DIAGONAL", "Diagonal", 1, 30, new ItemStack(Material.SANDSTONE_STAIRS), new IBridgerTypeHandler.DiagonalHandler()),
    ;

    private final String name;
    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;
    private final ItemStack itemstack;
    private final IBridgerTypeHandler handler;

    BridgerGameType(String name, String displayName, int minPlayers, int maxPlayers, ItemStack itemstack, IBridgerTypeHandler handler) {
        this.name = name;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.itemstack = itemstack;
        this.handler = handler;
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

    public IBridgerTypeHandler getHandler() {
        return this.handler;
    }
}
