package fr.hyriode.bridger.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainGUI extends HyriInventory {

    private final HyriBridger plugin;
    private final BridgerGamePlayer gamePlayer;
    private final ItemStack glassPane;

    public MainGUI(HyriBridger plugin, Player owner) {
        super(owner, "Settings", 45);
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(owner.getUniqueId());
        this.glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
        this.init();
    }

    private void init() {
        fillGlassPane();
        setChangeIslandGUI();
        setChangeBlockGUI();
        setBridgerGameType();
    }

    private void fillGlassPane() {
        int[] glassPaneIndexes = {0, 1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44};
        for (int index : glassPaneIndexes) {
            this.setItem(index, glassPane);
        }
    }

    private void setChangeIslandGUI() {
        this.setItem(33, new ItemBuilder(Material.REDSTONE_COMPARATOR, 1)
                .withName(ChatColor.DARK_AQUA + BridgerMessage.GUI_ITEM_CHANGE_ISLAND.asString(this.owner))
                .withLore(ChatColor.GRAY + BridgerMessage.GUI_ITEM_LORE_CHANGE_ISLAND.asString(this.owner))
                .build(), event -> new ChangeIslandGUI(plugin, owner).open());
    }

    private void setChangeBlockGUI() {
        this.setItem(15, new ItemBuilder(Material.GOLD_BLOCK, 1, gamePlayer.getCurrentBlock().getMeta())
                .withName(ChatColor.DARK_AQUA + BridgerMessage.GUI_ITEM_CHANGE_BLOCK.asString(this.owner))
                .withLore(ChatColor.GRAY + BridgerMessage.GUI_ITEM_LORE_CHANGE_BLOCK.asString(this.owner))
                .build(), event -> new ChangeBlockGUI(plugin, owner, 0).open());
    }

    private void setBridgerGameType() {
        int index = 20;
        for (BridgerGameType type : BridgerGameType.values()) {
            ItemBuilder itemBuilder;
            if (HyriAPI.get().getConfig().isDevEnvironment() || !HyriAPI.get().getServer().getType().equalsIgnoreCase(type.getName())) {
                itemBuilder = new ItemBuilder(type.getItemstack())
                        .withName(ChatColor.DARK_AQUA + getValue("gui.item." + type.getName().toLowerCase()))
                        .withLore(ChatColor.GRAY + BridgerMessage.GUI_LORE_BRIDGER_MODE.asString(this.owner));
                this.setItem(index, itemBuilder.build(), event -> HyriAPI.get().getQueueManager().addPlayerInQueue(event.getWhoClicked().getUniqueId(), "bridger", type.getName(), null));
            } else {
                itemBuilder = new ItemBuilder(type.getItemstack())
                        .withName(ChatColor.DARK_AQUA + getValue("gui.item." + type.getDisplayName()))
                        .withLore(ChatColor.GRAY + BridgerMessage.GUI_LORE_BRIDGER_MODE_SELECTED.asString(this.owner))
                        .withGlow();
                this.setItem(index, itemBuilder.build());
            }
            index++;
        }
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(owner);
    }
}
