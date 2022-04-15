package fr.hyriode.bridger.listener;

import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.bridger.Bridger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerListener extends HyriListener<Bridger> {

    public PlayerListener(Bridger plugin) {
        super(plugin);
    }

    public static final String BREAKABLE_META_DATA_KEY = "BridgerBreakable";

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if(event.getPlayer().getItemInHand().getAmount() < 64) {
            event.getPlayer().getInventory().addItem(new ItemStack(event.getItemInHand().getType(), 1));
        }

        BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());
        if(gamePlayer.getGameArea().isInArea(event.getBlock().getLocation())) {
            gamePlayer.addActualPlacedBlocks(1);
            gamePlayer.getPlacedBlocks().add(event.getBlock().getLocation());

            if(!gamePlayer.isBridging()) {
                gamePlayer.startBridging();
                gamePlayer.getPlacedBlocks().add(event.getBlock().getLocation());
            }
        event.getBlockPlaced().setMetadata(BREAKABLE_META_DATA_KEY, new FixedMetadataValue(plugin, true));
        return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + Bridger.getLanguageManager().getValue(event.getPlayer(), "message.player.oob.block"));
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBreakBlock(BlockBreakEvent event) {
        if (!event.getBlock().hasMetadata(BREAKABLE_META_DATA_KEY)) {
            event.setCancelled(true);
        }else {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getType().equals(InventoryType.PLAYER)) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());
        if(event.getTo().getBlock().getType().equals(Material.GOLD_PLATE)) {
            if(gamePlayer.isBridging()) {
                this.plugin.getGame().getPlayer(event.getPlayer()).addActualPlacedBlocks(1);
                gamePlayer.endBridging(true);
            }
            return;
        }
        if(event.getTo().getY() < this.plugin.getConfiguration().getyMinBeforeTp()) {
            if(gamePlayer.isBridging()) {
                gamePlayer.endBridging(false);
                return;
            }
            gamePlayer.spawnPlayer();
            gamePlayer.getPlayer().sendMessage(ChatColor.RED + Bridger.getLanguageManager().getValue(event.getPlayer(), "message.player.failed-bridge")
                    .replace("%block%", "0"));
        }
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
