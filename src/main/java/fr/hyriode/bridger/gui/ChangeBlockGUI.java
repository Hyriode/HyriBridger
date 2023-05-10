package fr.hyriode.bridger.gui;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerData;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.blocks.Specification;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

    public class ChangeBlockGUI extends HyriInventory {

    private final HyriBridger plugin;
    private final BridgerGamePlayer gamePlayer;
    private final BridgerData playerData;
    private final int page;

    private final List<Integer> slots = new ArrayList<>(Arrays.asList(19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34));

    public ChangeBlockGUI(HyriBridger plugin, Player owner, int page) {
        super(owner, name(owner, "gui.change-block.name"), 9*6);
        this.plugin = plugin;
        this.gamePlayer = this.plugin.getGame().getPlayer(owner.getUniqueId());
        this.playerData = gamePlayer.getData();
        this.page = page;
        this.init();
    }

    private void init() {
        ItemStack glassPane = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 9)
                .withName(" ")
                .build();

        this.setHorizontalLine(0, 8, glassPane);
        this.setHorizontalLine(45, 53, glassPane);

        this.setItem(0, new ItemBuilder(Material.ARROW)
                .withName(HyrameMessage.GO_BACK.asString(this.owner))
                .build(), event -> new MainGUI(this.plugin, this.owner).open());

        final int freeSpace = 14;
        final int actualShowedBlocksSize = (Math.min((this.page * freeSpace) + freeSpace, BridgerBlock.values().length));
        final int numberPages = (BridgerBlock.values().length / freeSpace);
        final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());

        for (int i = this.page * freeSpace; i < actualShowedBlocksSize; i++) {
            final BridgerBlock block = BridgerBlock.getById(i);
            final int slot = this.slots.get(i % freeSpace);

            if (block.equals(this.gamePlayer.getActualBlock())) {
                this.setItem(slot, new ItemBuilder(block.getMaterial(), 1, block.getMeta())
                        .withName(AQUA + block.getItemStackName(this.owner.getUniqueId()))
                        .withLore(BridgerMessage.GUI_LORE_BLOCK_SELECTED.asList(gamePlayer.getPlayer()))
                        .withGlow()
                        .build());
            } else if (this.gamePlayer.hasUnlockedBlock(block)) {
                this.setItem(slot, new ItemBuilder(block.getMaterial(), 1, block.getMeta())
                        .withName(GREEN + block.getItemStackName(this.owner.getUniqueId()))
                        .withLore(BridgerMessage.GUI_LORE_BLOCK_POSSESSED_BLOCK.asList(gamePlayer.getPlayer()))
                        .build(), event -> {
                    gamePlayer.setActualBlock(block);
                    new ChangeBlockGUI(this.plugin, this.owner, this.page).open();
                });

            } else {
                final ItemBuilder blockItemBuilder = new ItemBuilder(block.getMaterial(), 1, block.getMeta())
                        .withName(RED + block.getItemStackName(this.owner.getUniqueId()));

                if (!account.getHyris().hasEnough(block.getCost())) {
                    this.setItem(slot, blockItemBuilder.withLore(block.getNotBuyableLore(account)).build());
                } else {
                    this.setItem(slot, blockItemBuilder.withLore(block.getNotPossessedLore(owner.getUniqueId())).build(), event -> {
                        if (block.getSpecificationNeeded() != Specification.DEFAULT) return;
                        new ConfirmPurchaseGUI(this.plugin, this.owner, block).open();
                    });
                }
            }
        }

        if (this.page + 1 < numberPages){
            this.setItem(53, ItemBuilder.asHead(HyrameHead.MONITOR_FORWARD)
                    .withName(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_NAME.asString(this.owner).replace("%current_page%", String.valueOf(this.page + 1)).replace("%total_pages%", String.valueOf(numberPages)))
                    .withLore(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_LORE.asList(this.owner))
                    .build(), event -> new ChangeBlockGUI(this.plugin, this.owner, this.page+1).open());
        }

        if (this.page != 0) {
            this.setItem(45, ItemBuilder.asHead(HyrameHead.MONITOR_FORWARD)
                    .withName(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_NAME.asString(this.owner).replace("%current_page%", String.valueOf(this.page + 1)).replace("%total_pages%", String.valueOf(numberPages)))
                    .withLore(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_LORE.asList(this.owner))
                    .build(), event -> new ChangeBlockGUI(this.plugin, this.owner, this.page-1).open());
        }
    }
}
