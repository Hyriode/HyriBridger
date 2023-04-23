package fr.hyriode.bridger.listener;

import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerListener extends HyriListener<HyriBridger> {

    public PlayerListener(HyriBridger plugin) {
        super(plugin);
    }

    public static final String BREAKABLE_META_DATA_KEY = "BridgerBreakable";

    @EventHandler (priority = EventPriority.LOWEST) @SuppressWarnings("deprecation")
    public void onPlaceBlock(BlockPlaceEvent event) {
        final BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());

        if (event.getBlock().getType() != gamePlayer.getData().getSelectedBlock().getMaterial()) {
            event.setCancelled(true);
            return;
        }

        if (!gamePlayer.isBridging() && event.getBlockAgainst().hasMetadata(BREAKABLE_META_DATA_KEY)) {
            event.setCancelled(true);
            event.getBlockAgainst().setType(Material.AIR);
            return;
        }

        if (event.getPlayer().getItemInHand().getAmount() < 64) {
            event.getPlayer().getInventory().addItem(new ItemStack(event.getItemInHand().getType(), 1, event.getItemInHand().getDurability()));
        }

        if (gamePlayer.getGameArea().isInArea(event.getBlock().getLocation())) {
            if (!gamePlayer.isBridging()) {
                gamePlayer.startBridging();
                gamePlayer.getPlacedBlocks().add(event.getBlock().getLocation());
            }

            gamePlayer.getPlacedBlocks().add(event.getBlock().getLocation());
            event.getBlockPlaced().setMetadata(BREAKABLE_META_DATA_KEY, new FixedMetadataValue(plugin, true));
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + BridgerMessage.MESSAGE_PLAYER_OOB_BLOCK.asString(event.getPlayer()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Chunk chunk = event.getBlock().getChunk();
            chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        }, 5L);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(true);
        if (event.getBlock().hasMetadata(BREAKABLE_META_DATA_KEY)) {
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.PLAYER)) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());

        if (gamePlayer == null || gamePlayer.isSpectating()) {
            return;
        }

        Location to = event.getTo();

        if (to.getY() < this.plugin.getConfiguration().getMinY()) {
            if (gamePlayer.isInLeaderboards()) {
                gamePlayer.teleportToLeaderboards();
            } else {
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
                gamePlayer.resetPlayerBridge();
            }
            return;
        }

        if (gamePlayer.isInLeaderboards()) {
            return;
        }

        Location max = gamePlayer.getGameArea().getMax();
        Location min = gamePlayer.getGameArea().getMin();

        if (max.getX() + 5 < to.getX()) {
            gamePlayer.getPlayer().teleport(gamePlayer.getSpawn());
            gamePlayer.getPlayer().sendMessage(ChatColor.RED + BridgerMessage.MESSAGE_PLAYER_OOB.asString(event.getPlayer()));
            return;
        } else if (min.getX() - 5 > to.getX()) {
            gamePlayer.getPlayer().teleport(gamePlayer.getSpawn());
            gamePlayer.getPlayer().sendMessage(ChatColor.RED + BridgerMessage.MESSAGE_PLAYER_OOB.asString(event.getPlayer()));
            return;
        }

        if (gamePlayer.isBridging()) {
            if (to.getBlock().getType().equals(Material.GOLD_PLATE)) {
                gamePlayer.endBridging(true);
            }
        } else {
            Block block = to.clone().subtract(0, 1, 0).getBlock();
            if (block.hasMetadata(BREAKABLE_META_DATA_KEY)) {
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(event.getPlayer());

        if (gamePlayer == null) {
            return;
        }

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (event.getClickedBlock().hasMetadata(BREAKABLE_META_DATA_KEY)) {
                if(event.getClickedBlock().getType().equals(Material.BEDROCK)) {
                    event.getClickedBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPhysic(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

}
