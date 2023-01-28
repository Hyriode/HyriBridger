package fr.hyriode.bridger.gui;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.bridger.utils.UsefulHead;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static fr.hyriode.hyrame.utils.Symbols.*;
import static org.bukkit.ChatColor.*;

public class ChangeIslandGUI extends HyriInventory {

    //Maximum number of island in this configuration -> 36
    private int[] islandsSlots = new int[30];

    private final HyriBridger plugin;
    private final BridgerGamePlayer gamePlayer;

    public ChangeIslandGUI(HyriBridger plugin, Player owner) {
        super(owner, "Change island", 9*6);
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(this.owner);

        for (int i = 0; i < islandsSlots.length; i++) {
            islandsSlots[i] = i+9;
        }

        this.newUpdate(40L);

        this.init();
    }

    public void init() {
        ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 9)
                .withName(" ")
                .build();

        this.setHorizontalLine(0, 8, glassPane);
        this.setHorizontalLine(45, 53, glassPane);

        this.setItem(0, ItemBuilder.asHead()
                .withHeadTexture(UsefulHead.BACK.getTexture())
                .withName(DARK_AQUA + this.getValue("gui.item-name.go-back"))
                .build(), event -> new MainGUI(this.plugin, this.owner).open());

        this.update();
    }

    @Override
    public void update() {
        List<Boolean> emplacements = this.plugin.getGame().getEmplacements();
        for (int i = 0; i < emplacements.size(); i++) {
            boolean emplacement = emplacements.get(i);

            if (i == this.gamePlayer.getPlayerNumber()) {
                this.setItem(this.islandsSlots[i], new ItemBuilder(Material.STAINED_CLAY, i+1, (short)1)
                        .withName(this.getIslandName(i))
                        .withLore(getIslandLore(IslandStatus.SELF_OCCUPIED))
                        .build());
            } else if (emplacement) {
                this.setItem(this.islandsSlots[i], new ItemBuilder(Material.STAINED_CLAY, i+1, (short)14)
                        .withName(this.getIslandName(i))
                        .withLore(this.getIslandLore(IslandStatus.OCCUPIED))
                        .build());
            } else {
                int finalI = i;
                this.setItem(this.islandsSlots[i], new ItemBuilder(Material.STAINED_CLAY, i+1, (short)5)
                        .withName(this.getIslandName(i))
                        .withLore(this.getIslandLore(IslandStatus.FREE))
                        .build(), event -> this.warpToIsland(finalI));
            }
        }
    }

    private String getIslandName(int islandNumber) {
        return GRAY + this.getValue("gui.item.island") + " " + new DecimalFormat("00").format(islandNumber+1);
    }

    private List<String> getIslandLore(ChangeIslandGUI.IslandStatus status) {
        final ArrayList<String> lore = new ArrayList<>();
        lore.add(DARK_GRAY + DOT_BOLD + " " + GRAY + this.getValue("gui.lore.status") + ": " + status.getChatColor() + this.getValue(status.getKey()));
        lore.add(" ");
        lore.add(this.getValue("gui.lore.click-to-teleport"));

        return lore;
    }

    private void warpToIsland(int islandIndex) {
        if (!this.plugin.getGame().getEmplacements().get(islandIndex)) {
            this.owner.closeInventory();

            if (this.gamePlayer.isBridging()) {
                this.gamePlayer.endBridging(false);
            }
            this.plugin.getGame().getEmplacements().set(gamePlayer.getPlayerNumber(), false);
            this.gamePlayer.setIslandNumber(islandIndex);
        }
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(this.owner);
    }

    private enum IslandStatus {
        FREE(GREEN, "gui.lore.status.free"),
        OCCUPIED(RED, "gui.lore.status.occupied"),
        SELF_OCCUPIED(GOLD, "gui.lore.status.self-occupied");

        private final ChatColor chatColor;
        private final String key;

        IslandStatus(ChatColor chatColor, String key) {
            this.chatColor = chatColor;
            this.key = key;
        }

        public ChatColor getChatColor() {
            return chatColor;
        }

        public String getKey() {
            return key;
        }
    }
}
