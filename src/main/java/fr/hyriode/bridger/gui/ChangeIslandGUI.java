package fr.hyriode.bridger.gui;

import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ChangeIslandGUI extends HyriInventory {

    private final Bridger plugin;

    public ChangeIslandGUI(Bridger plugin, Player owner) {
        super(owner, "Change island", 9*6);
        this.plugin = plugin;

        this.init();
    }

    public void init() {
        List<Boolean> emplacements = this.plugin.getGame().getEmplacements();
        for (int i = 0; i < 50; i++) {
            boolean emplacement = emplacements.get(i);
            if(emplacement) {
                this.setItem(i, new ItemBuilder(Material.STAINED_CLAY, i+1, (short)14)
                        .withName(ChatColor.RED + this.getValue("gui.item.island"))
                        .withLore(ChatColor.RED + this.getValue("gui.lore.occupied"))
                        .build());
            }else {
                int finalI = i;
                this.setItem(i, new ItemBuilder(Material.STAINED_CLAY, i+1, (short)5)
                        .withName(ChatColor.GREEN + this.getValue("gui.item.island"))
                        .withLore(ChatColor.GREEN + this.getValue("gui.lore.free"))
                        .build(), event -> {
                    if(!this.plugin.getGame().getEmplacements().get(finalI)) {
                        this.owner.closeInventory();
                        BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(this.owner);
                        if (gamePlayer.isBridging()) {
                            gamePlayer.endBridging(false);
                        }
                        this.plugin.getGame().getEmplacements().set(gamePlayer.getPlayerNumber(), false);
                        gamePlayer.setPlayerNumber(finalI);
                    }
                });
            }
        }
    }

    public String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.owner, key);
    }
}
