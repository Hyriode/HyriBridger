package fr.hyriode.bridger.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ChangeModeGUI extends HyriInventory {

    private final Bridger plugin;

    public ChangeModeGUI(Bridger plugin, Player owner) {
        super(owner, "Change server", 9*5);
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        int i = 20;
        for (BridgerGameType type : BridgerGameType.values()) {
            if(!HyriAPI.get().getServer().getGameType().equalsIgnoreCase(BridgerGameType.NORMAL.getName())) {
                this.setItem(i, new ItemBuilder(type.getItemstack())
                    .withName(ChatColor.DARK_AQUA + this.getValue("gui.item." + type.getDisplayName()))
                    .withLore(ChatColor.RESET + this.getValue("gui.lore.bridger-mode"))
                    .build(), event -> HyriAPI.get().getQueueManager().addPlayerInQueue(event.getWhoClicked().getUniqueId(), "bridger", type.getName()));
            }else {
                this.setItem(i, new ItemBuilder(type.getItemstack())
                        .withName(ChatColor.DARK_AQUA + this.getValue("gui.item." + type.getDisplayName()))
                        .withLore(ChatColor.RESET + this.getValue("gui.lore.bridger-mode-selected"))
                        .withGlow()
                        .build());
            }
            i++;
        }

        this.setItem(24, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .withName(ChatColor.YELLOW + this.getValue("gui.item.choose-island"))
                .build(), event -> new ChangeIslandGUI(this.plugin, this.owner).open());
    }

    public String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.owner, key);
    }
}
