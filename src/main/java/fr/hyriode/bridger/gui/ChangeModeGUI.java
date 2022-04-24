package fr.hyriode.bridger.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChangeModeGUI extends HyriInventory {

    private final Bridger plugin;

    public ChangeModeGUI(Bridger plugin, Player owner) {
        super(owner, "Change server", 9*5);
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.setItem(20, new ItemBuilder(Material.RED_SANDSTONE, 1, (short)1)
                .withName(ChatColor.DARK_AQUA + this.getValue("gui.item.short"))
                .withLore(ChatColor.RESET + this.getValue("gui.lore.bridger-mode"))
                .build(), event -> {
            UUID uuid = event.getWhoClicked().getUniqueId();
            if(!HyriAPI.get().getServer().getGameType().equalsIgnoreCase(BridgerGameType.SHORT.getName())) {
                HyriAPI.get().getQueueManager().addPlayerInQueue(uuid, "bridger", BridgerGameType.SHORT.getName());
            }else {
                this.owner.closeInventory();
                this.owner.sendMessage(this.getValue("message.player.already-on-the-server"));
            }
        });

        this.setItem(21, new ItemBuilder(Material.SANDSTONE, 1)
                .withName(ChatColor.DARK_AQUA + this.getValue("gui.item.long"))
                .withLore(ChatColor.RESET + this.getValue("gui.lore.bridger-mode"))
                .build(), event -> {
            UUID uuid = event.getWhoClicked().getUniqueId();
            if(!HyriAPI.get().getServer().getGameType().equalsIgnoreCase(BridgerGameType.LONG.getName())) {
                HyriAPI.get().getQueueManager().addPlayerInQueue(uuid, "bridger", BridgerGameType.LONG.getName());
            }else {
                this.owner.closeInventory();
                this.owner.sendMessage(this.getValue("message.player.already-on-the-server"));
            }
        });

        this.setItem(22, new ItemBuilder(Material.SANDSTONE_STAIRS, 1)
                .withName(ChatColor.DARK_AQUA + this.getValue("gui.item.diagonal"))
                .withLore(ChatColor.RESET + this.getValue("gui.lore.bridger-mode"))
                .build(), event -> {
            UUID uuid = event.getWhoClicked().getUniqueId();
            if(!HyriAPI.get().getServer().getGameType().equalsIgnoreCase(BridgerGameType.DIAGONAL.getName())) {
                HyriAPI.get().getQueueManager().addPlayerInQueue(uuid, "bridger", BridgerGameType.DIAGONAL.getName());
            }else {
                this.owner.closeInventory();
                this.owner.sendMessage(this.getValue("message.player.already-on-the-server"));
            }
        });

        this.setItem(24, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .withName(ChatColor.YELLOW + this.getValue("gui.item.choose-island"))
                .build(), event -> new ChangeIslandGUI(this.plugin, this.owner).open());
    }

    public String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.owner, key);
    }
}
