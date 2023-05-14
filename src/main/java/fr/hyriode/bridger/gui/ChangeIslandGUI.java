package fr.hyriode.bridger.gui;

import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static fr.hyriode.bridger.language.BridgerMessage.*;
import static fr.hyriode.hyrame.utils.Symbols.DOT_BOLD;
import static org.bukkit.ChatColor.*;

public class ChangeIslandGUI extends HyriInventory {

    //Maximum number of island in this configuration -> 36
    private final int[] islandsSlots = IntStream.range(0, 30).map(i -> i+9).toArray();

    private final HyriBridger plugin;
    private final BridgerGamePlayer gamePlayer;
    private final ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 9).withName(" ").build();;

    public ChangeIslandGUI(HyriBridger plugin, Player owner) {
        super(owner, name(owner, "gui.change-island.name"), 9*6);
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(this.owner);

        this.init();
    }

    public void init() {
        setHorizontalLine(0, 8, glassPane);
        setHorizontalLine(45, 53, glassPane);
        setItem(0, new ItemBuilder(Material.ARROW)
                .withName(HyrameMessage.GO_BACK.asString(this.owner))
                .build(), event -> new MainGUI(plugin, owner).open());
        update();
    }

    @Override
    public void update() {
        for (BridgerGamePlayer player : this.plugin.getGame().getPlayers()) {
            final int finalNumber = player.getPlayerNumber();
            this.setItem(islandsSlots[finalNumber], new ItemBuilder(Material.STAINED_CLAY, finalNumber+1)
                    .withData(player.getUniqueId() == owner.getUniqueId() ? IslandStatus.SELF_OCCUPIED.getColorData() : IslandStatus.OCCUPIED.getColorData())
                    .withName(this.getIslandName(finalNumber))
                    .withLore(this.getOccupiedIslandLore(player)).build());
        }
        
        
        for (int i = 0; i < this.plugin.getGame().getEmplacements().size(); i++) {
            if (this.plugin.getGame().getEmplacements().get(i)) {
                continue;
            }

            final int finalI = i;
            this.setItem(islandsSlots[i], new ItemBuilder(Material.STAINED_CLAY, i+1)
                    .withData(IslandStatus.FREE.getColorData())
                    .withName(this.getIslandName(i))
                    .withLore(this.getFreeIslandLore()).build(), event -> warpToIsland(finalI));
        }
    }

    private String getIslandName(int islandNumber) {
        return GRAY + GUI_ITEM_ISLAND.asString(gamePlayer.getPlayer()) + " " + new DecimalFormat("00").format(islandNumber+1);
    }

    private List<String> getFreeIslandLore() {
        return Arrays.asList(
                DARK_GRAY + DOT_BOLD + " " + GRAY + GUI_LORE_STATUS.asString(gamePlayer.getPlayer()) + ": " + IslandStatus.FREE.getChatColor() + IslandStatus.FREE.getMessage().asString(gamePlayer.getPlayer()),
                "",
                GUI_LORE_CLICK_TO_TELEPORT.asString(gamePlayer.getPlayer())
        );
    }

    private List<String> getOccupiedIslandLore(BridgerGamePlayer player) {
        final IslandStatus status = player.getUniqueId() == owner.getUniqueId() ? IslandStatus.SELF_OCCUPIED : IslandStatus.OCCUPIED;

        return Arrays.asList(
                DARK_GRAY + DOT_BOLD + " " + GRAY + GUI_LORE_STATUS.asString(gamePlayer.getPlayer()) + ": " + status.getChatColor() + status.getMessage().asString(gamePlayer.getPlayer()),
                DARK_GRAY + DOT_BOLD + " " + GRAY + GUI_LORE_BY.asString(gamePlayer.getPlayer()) + ": " + player.getPlayer().getDisplayName()
        );
    }

    private void warpToIsland(int islandIndex) {
        if (!plugin.getGame().getEmplacements().get(islandIndex)) {
            owner.closeInventory();
            if (gamePlayer.isBridging()) {
                gamePlayer.endBridging(false);
            }
            plugin.getGame().getEmplacements().set(gamePlayer.getPlayerNumber(), false);
            gamePlayer.setPlayerNumber(islandIndex);
            gamePlayer.onJoin();
        }
    }

    private enum IslandStatus {
        FREE(GREEN, GUI_LORE_STATUS_FREE, (short) 5),
        OCCUPIED(RED, GUI_LORE_STATUS_OCCUPIED, (short) 14),
        SELF_OCCUPIED(GOLD, GUI_LORE_STATUS_SELF_OCCUPIED, (short) 1);

        private final ChatColor chatColor;
        private final BridgerMessage message;
        private final Short colorData;

        IslandStatus(ChatColor chatColor, BridgerMessage message, Short colorData) {
            this.chatColor = chatColor;
            this.message = message;
            this.colorData = colorData;
        }

        public ChatColor getChatColor() {
            return chatColor;
        }

        public BridgerMessage getMessage() {
            return message;
        }

        public Short getColorData() {
            return colorData;
        }
    }
}
