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
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
        setItem(0, ItemBuilder.asHead()
                .withHeadTexture(UsefulHead.BACK.getTexture())
                .withName(DARK_AQUA + getValue("gui.item-name.go-back"))
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
        return GRAY + getValue("gui.item.island") + " " + new DecimalFormat("00").format(islandNumber+1);
    }

    private List<String> getIslandLore(IslandStatus status) {
        return Arrays.asList(
                DARK_GRAY + DOT_BOLD + " " + GRAY + getValue("gui.lore.status") + ": " + status.getChatColor() + getValue(status.getKey()),
                "",
                getValue("gui.lore.click-to-teleport")
        );
    }

    private void warpToIsland(int islandIndex) {
        if (!plugin.getGame().getEmplacements().get(islandIndex)) {
            owner.closeInventory();
            if (gamePlayer.isBridging()) {
                gamePlayer.endBridging(false);
            }
            plugin.getGame().getEmplacements().set(gamePlayer.getPlayerNumber(), false);
            gamePlayer.setIslandNumber(islandIndex);
        }
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(owner);
    }

    private enum IslandStatus {
        FREE(GREEN, "gui.lore.status.free", (short) 5),
        OCCUPIED(RED, "gui.lore.status.occupied", (short) 14),
        SELF_OCCUPIED(GOLD, "gui.lore.status.self-occupied", (short) 1);

        private final ChatColor chatColor;
        private final String key;
        private final Short colorData;

        IslandStatus(ChatColor chatColor, String key, Short colorData) {
            this.chatColor = chatColor;
            this.key = key;
            this.colorData = colorData;
        }

        public ChatColor getChatColor() {
            return chatColor;
        }

        public String getKey() {
            return key;
        }

        public Short getColorData() {
            return colorData;
        }
    }
}
