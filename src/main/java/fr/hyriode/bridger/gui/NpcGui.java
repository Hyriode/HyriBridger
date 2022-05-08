package fr.hyriode.bridger.gui;

import fr.hyriode.bridger.Bridger;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NpcGui extends HyriInventory {

    private final Bridger plugin;

    public NpcGui(Bridger plugin, Player owner) {
        super(owner, "Change ?", 45);
        this.plugin = plugin;
    }

    private void init() {
        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS, 1, (short)0)
                .withName("")
                .build());
        this.setHorizontalLine(36, 44, new ItemBuilder(Material.STAINED_GLASS, 1, (short)0)
                .withName("")
                .build());
        this.setVerticalLine(0, 36, new ItemBuilder(Material.STAINED_GLASS, 1, (short)0)
                .withName("")
                .build());
        this.setHorizontalLine(8, 44, new ItemBuilder(Material.STAINED_GLASS, 1, (short)0)
                .withName("")
                .build());

        this.setItem(20, new ItemBuilder(Material.CLAY, 1, (short)0)
                .withName(ChatColor.AQUA + "Change island ?")
                .build(), event -> new ChangeIslandGUI(this.plugin, this.owner).open());

        this.setItem(22, new ItemBuilder(Material.STONE_SLAB2, 1, (short)1)
                .withName(ChatColor.AQUA + "Change mode ?")
                .build());

        //this.setItem(24, new ItemBuilder());
    }
}
