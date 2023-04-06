package fr.hyriode.bridger.gui;

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
        super(owner, "Change island", 9*6);
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
        List<Boolean> emplacements = plugin.getGame().getEmplacements();
        for (int i = 0; i < emplacements.size(); i++) {
            boolean emplacement = emplacements.get(i);
            int slot = islandsSlots[i];
            IslandStatus status;
            ItemBuilder itemBuilder = new ItemBuilder(Material.STAINED_CLAY, i+1);

            if (i == gamePlayer.getPlayerNumber()) {
                status = IslandStatus.SELF_OCCUPIED;
            } else if (emplacement) {
                status = IslandStatus.OCCUPIED;
            } else {
                status = IslandStatus.FREE;
            }

            itemBuilder.withData(status.getColorData())
                    .withName(getIslandName(i))
                    .withLore(getIslandLore(status));
            if (status == IslandStatus.FREE) {
                int finalI = i;
                setItem(slot, itemBuilder.build(), event -> warpToIsland(finalI));
            } else {
                setItem(slot, itemBuilder.build());
            }

        }
    }

    private String getIslandName(int islandNumber) {
        return GRAY + GUI_ITEM_ISLAND.asString(gamePlayer.getPlayer()) + " " + new DecimalFormat("00").format(islandNumber+1);
    }

    private List<String> getIslandLore(IslandStatus status) {
        return Arrays.asList(
                DARK_GRAY + DOT_BOLD + " " + GRAY + GUI_LORE_STATUS.asString(gamePlayer.getPlayer()) + ": " + status.getChatColor() + status.message.asString(gamePlayer.getPlayer()),
                "",
                GUI_LORE_CLICK_TO_TELEPORT.asString(gamePlayer.getPlayer())
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
