package fr.hyriode.bridger.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainGUI extends HyriInventory {

    private final HyriBridger plugin;
    private final BridgerGamePlayer gamePlayer;

    public MainGUI(HyriBridger plugin, Player owner) {
        super(owner, "Settings", 45);
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(owner.getUniqueId());
        this.init();
    }

    private void init() {
        ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
        this.setItem(0, glassPane);
        this.setItem(1, glassPane);
        this.setItem(9, glassPane);
        this.setItem(7, glassPane);
        this.setItem(8, glassPane);
        this.setItem(17, glassPane);
        this.setItem(44, glassPane);
        this.setItem(43, glassPane);
        this.setItem(35, glassPane);
        this.setItem(36, glassPane);
        this.setItem(37, glassPane);
        this.setItem(27, glassPane);

        this.setItem(33, new ItemBuilder(Material.REDSTONE_COMPARATOR, 1)
                .withName(ChatColor.DARK_AQUA + this.getValue("gui.item.change-island"))
                .withLore(ChatColor.GRAY + this.getValue("gui.item-lore.change-island"))
                .build(), event -> new ChangeIslandGUI(this.plugin, this.owner).open());

        this.setItem(15, new ItemBuilder(Material.GOLD_BLOCK, 1, this.gamePlayer.getActualBlock().getMeta())
                .withName(ChatColor.DARK_AQUA + this.getValue("gui.item.change-block"))
                .withLore(ChatColor.GRAY + this.getValue("gui.item-lore.change-block"))
                .build(), event -> new ChangeBlockGUI(this.plugin, this.owner, 0).open());

        int i = 20;
        for (BridgerGameType type : BridgerGameType.values()) {
            if (HyriAPI.get().getConfig().isDevEnvironment() || !HyriAPI.get().getServer().getType().equalsIgnoreCase(type.getName())) {
                this.setItem(i, new ItemBuilder(type.getItemstack())
                        .withName(ChatColor.DARK_AQUA + this.getValue("gui.item." + type.getName().toLowerCase()))
                        .withLore(ChatColor.GRAY + this.getValue("gui.lore.bridger-mode"))
                        .build(), event -> HyriAPI.get().getQueueManager().addPlayerInQueue(event.getWhoClicked().getUniqueId(), "bridger", type.getName(), null));
            } else {
                this.setItem(i, new ItemBuilder(type.getItemstack())
                        .withName(ChatColor.DARK_AQUA + this.getValue("gui.item." + type.getDisplayName()))
                        .withLore(ChatColor.GRAY + this.getValue("gui.lore.bridger-mode-selected"))
                        .withGlow()
                        .build());
            }
            i++;
        }
    }

    private String getValue(String key) {
       return HyriLanguageMessage.get(key).getValue(owner);
    }
}
