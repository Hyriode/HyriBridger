package fr.hyriode.bridger.gui;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerBlockTransaction;
import fr.hyriode.bridger.api.BridgerData;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

public class ConfirmPurchaseGUI extends HyriInventory {

    private final HyriBridger plugin;
    private final BridgerBlock block;
    private final String blockName;

    public ConfirmPurchaseGUI(HyriBridger plugin, Player owner, BridgerBlock block) {
        super(owner, name(owner, "gui.confirm-purchase.name"), 9 * 5);

        this.plugin = plugin;
        this.block = block;
        this.blockName = block.getAbsoluteName(owner.getUniqueId());

        this.init(plugin.getGame().getPlayer(owner));
    }

    private void init(final BridgerGamePlayer gamePlayer) {
        final ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 9)
                .withName(" ")
                .build();
        this.setVerticalLine(0, 36, glassPane);
        this.setVerticalLine(8, 44, glassPane);

        this.setItem(13, new ItemBuilder(this.block.getMaterial())
                .withName(AQUA + this.block.getItemStackName(this.owner.getUniqueId()))
                .withLore(this.block.getNotPossessedLore(this.owner.getUniqueId()))
                .removeLoreLines(2)
                .build());

        this.setItem(30, new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                .withName(GREEN + BridgerMessage.UTILS_YES.asString(gamePlayer.getPlayer()))
                .build(),
                event -> {
                    final BridgerData data = gamePlayer.getData();

                    data.addUnlockedBlock(block);

                    final IHyriPlayer account = gamePlayer.asHyriPlayer();

                    account.getTransactions().add("bridgerBlocks", new BridgerBlockTransaction(this.block));
                    account.getHyris().remove(this.block.getCost()).exec();
                    account.update();

                    this.plugin.getGame().getPlayer(this.owner.getUniqueId()).setActualBlock(block);

                    this.owner.closeInventory();

                    this.owner.sendMessage(DARK_AQUA + BridgerMessage.MESSAGE_PLAYER_BOUGHT.asString(this.owner)
                            .replace("%block%", AQUA + this.blockName)
                            .replace("%cost%", LIGHT_PURPLE + String.valueOf(this.block.getCost())));
                });

        this.setItem(32, new ItemBuilder(Material.STAINED_CLAY, 1, (short) 14)
                .withName(RED + BridgerMessage.UTILS_NO.asString(this.owner))
                .build(), event -> new ChangeBlockGUI(this.plugin, this.owner, 0).open());
    }
}