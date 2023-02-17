package fr.hyriode.bridger.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.api.player.HyriBridgerData;
import fr.hyriode.bridger.api.player.HyriBridgerStats;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.scoreboard.HyriBridgerScoreboard;
import fr.hyriode.bridger.game.timers.BridgerPlayedDuration;
import fr.hyriode.bridger.game.timers.BridgerTimer;
import fr.hyriode.bridger.gui.MainGUI;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BridgerGamePlayer extends HyriGamePlayer {

    private HyriBridger plugin;
    private int playerNumber;

    private List<BridgerGamePlayer> watchers;
    private boolean spectating;
    private BridgerGamePlayer watchedPlayer;

    private Location spawn;
    private BridgerBlock actualBlock;

    private BridgerTimer actualTimer;
    private boolean isBridging;
    private boolean isInEndingBridging;
    private HyriBridgerScoreboard scoreboard;
    private List<Location> placedBlocks;
    private Area gameArea;
    private Location hologramLocation;
    private Location npcLocation;
    private Hologram hologram;
    private NPC npc;
    private HyriBridgerStats oldAccount;

    private HyriBridgerDuration oldPB;
    private Medal oldMedal;
    private long oldPlacedBlocks;
    private long oldMadeBridges;
    private long oldFailedBridges;
    private long playedTime;

    private HyriBridgerDuration actualPB;
    private Medal actualMedal;
    private long actualPlacedBlocks;
    private long actualMadeBridges;
    private long actualFailedBridges;
    private long connectionTime;

    public BridgerGamePlayer(Player player) {
        super(player);
        this.watchers = new ArrayList<>();
    }

    public void init(HyriBridger plugin) {
        this.init(plugin, plugin.getGame().getFirstEmplacementEmptyAndTakeIt());
    }

    public void init(HyriBridger plugin, int playerNumber) {
        this.actualBlock = BridgerBlock.getById(HyriBridgerData.get(this.player.getUniqueId()).getActualBlockId());

        this.plugin = plugin;
        this.spectating = false;
        this.playerNumber = playerNumber;
        this.watchers = new ArrayList<>();
        this.isInEndingBridging = false;

        this.oldAccount = HyriBridgerStats.get(this.player.getUniqueId());
        HyriBridgerData.updatePossessedBlocks(this.player.getUniqueId());

        //Setup new stats
        this.actualPlacedBlocks = 0;
        this.actualMadeBridges = 0;
        this.actualFailedBridges = 0;
        this.connectionTime = System.currentTimeMillis();
        this.placedBlocks = new ArrayList<>();
        //Setup old stats
        this.oldPB = this.getOldPB();
        this.oldMedal = this.getOldMedal();
        this.oldPlacedBlocks = this.oldAccount.getBlocksPlaced();
        this.oldMadeBridges = this.oldAccount.getBridgesMade();
        this.oldFailedBridges = this.oldAccount.getBridgeFailed();
        this.playedTime = this.oldAccount.getPlayedTimeInMs();
        //Setup config
        this.spawn = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getSpawnLocationOnFirstIsland().asBukkit());
        this.hologramLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getHologramLocationOnFirstIsland().asBukkit());
        this.npcLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getNpcLocationOnFirstIsland().asBukkit());
        this.gameArea = new Area(this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandFirst().asBukkit()),this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandSecond().asBukkit()));
        //Setup game
        this.setupScoreboard();
        this.setupNPC();
        this.refreshHologram();
        //Reset player
        PlayerUtil.resetPlayer(this.player, true);
        PlayerUtil.showPlayer(this.player);
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setCanPickupItems(false);
        //Spawn player
        this.spawnPlayer();
    }

    public void initSpec(BridgerGamePlayer watchedPlayer) {
        if (this.isSpectating()) {
            this.exitSpec();
        }

        if (this.isBridging()) {
            this.endBridging(false);
        }
        this.plugin.getGame().getEmplacements().set(this.playerNumber, false);

        this.spectating = true;
        this.watchedPlayer = watchedPlayer;

        this.watchers.forEach(gamePlayer -> {
            gamePlayer.initSpec(watchedPlayer);
            gamePlayer.getPlayer().sendMessage(ChatColor.AQUA + HyriLanguageMessage.get("message.player.watched-player-changed").getValue(gamePlayer.getUniqueId()));
        });
        this.watchers.clear();

        this.oldAccount =  HyriBridgerStats.get(this.player.getUniqueId());
        watchedPlayer.addWatcher(this);

        PlayerUtil.addSpectatorAbilities(this.player);
        PlayerUtil.hidePlayer(this.player,false);

        this.deleteNPC();
        this.deleteHologram();

        this.player.setGameMode(GameMode.SPECTATOR);
        this.player.teleport(watchedPlayer.player);
        this.player.sendMessage(ChatColor.AQUA + HyriLanguageMessage.get("message.player.is-spectating").getValue(this.player.getUniqueId())
                .replace("%player%", watchedPlayer.player.getName()));
    }

    public void exitSpec() {
        this.spectating = false;
        this.watchedPlayer.getWatchers().remove(this);
        this.watchedPlayer = null;
    }

    public void reset() {
        this.init(this.plugin);
    }

    public void spawnPlayer() {
        this.player.teleport(this.spawn);
        this.player.getInventory().clear();
        this.giveBlocks();
        this.player.setGameMode(GameMode.SURVIVAL);

        this.deletePlacedBlocks();
        this.placedBlocks = new ArrayList<>();
    }

    private void giveBlocks() {
        this.player.getInventory().clear();
        this.player.getInventory().addItem(new ItemStack(this.actualBlock.getMaterial(), 64*9, this.actualBlock.getMeta()));
        this.player.getInventory().setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());
    }

    public void resetPlayerBridge() {
        if(!this.isInEndingBridging) {
            if (this.isBridging()) {
                this.endBridging(false);
                return;
            }

            this.spawnPlayer();
            new ActionBar(ChatColor.RED + this.getValue("message.player.failed-bridge")
                    .replace("%block%", "0")).send(this.player);
        }

    }

    public void startBridging() {
        this.startTimer();
        this.isBridging = true;
    }

    private void startTimer() {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (actualTimer.getActualTime() > Duration.ofHours(10).toMillis()-1) {
                this.endBridging(false);
            } else {
                new ActionBar(ChatColor.DARK_AQUA + actualTimer.getFormattedActualTime()).send(player);
            }
        }, 1, 0);

        this.actualTimer = new BridgerTimer(bukkitTask);
        this.actualTimer.start();
    }

    public void endBridging(boolean success) {
        if(!this.isInEndingBridging) {
            this.isInEndingBridging = true;
            this.player.setGameMode(GameMode.ADVENTURE);
            this.endTimer();

            if (success && this.placedBlocks.size() > 20 && this.getActualTimer().getActualTime() > 3700) {
                if (this.isPB()) {
                    this.successPersonalBest();
                } else {
                    this.failPersonalBest();
                }

                this.plugin.getGame().getSession().add(this.player, this.actualTimer.toFinalDuration());

                final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId());
                account.getHyris().add(5).exec();
                //TODO: remettre quand newton aura arrêter de me baiser
                //account.getNetworkLeveling().addExperience(10);*
                account.update();

                this.addActualMadeBridges(1);
            } else {
                new ActionBar(ChatColor.RED + this.getValue("message.player.failed-bridge")
                        .replace("%block%", String.valueOf(this.placedBlocks.size()-1))).send(this.player);
                this.addActualFailedBridges(1);
            }

            this.deletePlacedBlocks();
            this.refreshHologram();
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.spawnPlayer();
                this.isInEndingBridging = false;
                this.isBridging = false;
                this.player.setGameMode(GameMode.SURVIVAL);
            }, 3L);
        }
    }

    private boolean isPB() {
        if(this.actualPB == null || this.actualPB.getExactTime() > this.actualTimer.getFinalTime()) {
            if(this.oldPB == null || this.oldPB.getExactTime() > this.actualTimer.getFinalTime()) {
                return true;
            }
        }
        return false;
    }

    private void endTimer() {
        this.actualTimer.getLinkedTask().cancel();
        this.actualTimer.end();
    }

    private void successPersonalBest() {
        if (this.getMedal() == null || !this.getMedal().equals(Medal.ULTIMATE)) {
            for (Medal medal : Medal.values()) {
                if (this.actualTimer.toFinalDuration().getExactTime() < this.plugin.getTypeHandler().getMedalTime(medal)) {
                    this.successMedal(medal);
                }
            }
        }

        this.actualPB = this.actualTimer.toFinalDuration();

        this.player.playSound(this.spawn, Sound.LEVEL_UP, 10, 1);

        Title.sendTitle(this.player, new Title(ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), ChatColor.DARK_AQUA + this.getValue("title.sub.player.pb")
                .replace("%pb%", ChatColor.AQUA + this.actualTimer.toFinalDuration().toFormattedTime())
                , 5, 40, 15));
        this.plugin.getMessageHelper().sendSuccessPBMessage(this.player, this.actualTimer.toFinalDuration());
    }

    private void successMedal(Medal medal) {
        this.actualMedal = medal;

        final HyriBridgerData account = HyriBridgerData.get(this.player.getUniqueId());
        account.addBlockForMedal(medal, this.player.getUniqueId());
        account.update(this.player.getUniqueId());
    }


    private void failPersonalBest() {
        this.player.playSound(this.spawn, Sound.ORB_PICKUP, 10, 1);
        Title.sendTitle(this.player, ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), "", 5, 20, 5);
        this.plugin.getMessageHelper().sendFailedPBMessage(this.player, this.actualPB, this.actualTimer.toFinalDuration());
    }

    private void deletePlacedBlocks() {
        if (placedBlocks.size() < 30) {
            for (Location placedBlock : placedBlocks) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                    if (!placedBlock.getBlock().getType().equals(Material.AIR)) {
                        placedBlock.getBlock().setType(Material.AIR);
                    }
                }, this.placedBlocks.indexOf(placedBlock));
            }
        } else {
            for (Location placedBlock : placedBlocks) {
                if (placedBlock.getBlock().getType() != Material.AIR) {
                    placedBlock.getBlock().setType(Material.AIR);
                }
            }
        }

        for (Location placedBlock : placedBlocks) {
            this.sendBlockChange(placedBlock, Material.AIR, (byte) 0);
        }
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        if (((CraftPlayer) this.player).getHandle().playerConnection != null) {
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld)loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void setupScoreboard() {
        if (this.scoreboard != null) {
            this.scoreboard.hide();
        }
        this.scoreboard = new HyriBridgerScoreboard(this.plugin, this.player);
        this.scoreboard.show();
    }

    public void deleteHologram() {
        if (this.hologram != null) {
            this.hologram.removeReceiver(this.player);
            this.hologram.destroy();
        }
    }

    private void refreshHologram() {
        this.deleteHologram();
        this.hologram = new Hologram.Builder(this.plugin, this.hologramLocation)
                .withLine(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + this.getValue("hologram.stats"))
                .withLine(ChatColor.AQUA + this.getValue("scoreboard.medal.actual") + (this.getMedal() != null ? this.getValue(this.getMedal().getLanguageValue()) : ChatColor.RED + "✘"))
                .withLine(ChatColor.AQUA + this.getValue("hologram.placed-blocks") + ChatColor.YELLOW + (this.oldPlacedBlocks + this.actualPlacedBlocks))
                .withLine(ChatColor.AQUA + this.getValue("hologram.made-bridges") + ChatColor.YELLOW + (this.oldMadeBridges + this.actualMadeBridges))
                .withLine(ChatColor.AQUA + this.getValue("hologram.failed-bridged") + ChatColor.YELLOW + (this.oldFailedBridges + this.actualFailedBridges))
                .withLine(ChatColor.AQUA + this.getValue("hologram.played-time") + ChatColor.YELLOW + new BridgerPlayedDuration(Duration.ofMillis(this.playedTime + (System.currentTimeMillis() - this.connectionTime)), this.player).toFormattedTime())
                .build();
        this.hologram.setLocation(this.hologramLocation);
        this.hologram.addReceiver(this.player);
        this.hologram.sendLines();
    }

    public void deleteNPC() {
        if (this.npc != null) {
            NPCManager.removeNPC(this.npc);
        }
    }

    private void setupNPC() {
        this.deleteNPC();
        List<String> npcHolo = new ArrayList<>();
        npcHolo.add(this.getValue("npc.name"));
        npcHolo.add(this.getValue("npc.lore"));

        this.npc = NPCManager.createNPC(this.npcLocation, BridgerGame.NPC_SKIN, npcHolo);

        this.npc.addPlayer(this.player)
                .setTrackingPlayer(true)
                .setShowingToAll(true)
                .setInteractCallback((rightClick, clicker) -> {
                    if (!rightClick) {
                        return;
                    }

                    if (this.plugin.getGame().getPlayer(clicker).isSpectating()) {
                        return;
                    }
                    new MainGUI(this.plugin, clicker).open();
                });

        NPCManager.sendNPC(this.npc);
    }

    public void sendPlayerStats() {
        final HyriBridgerStats account = HyriBridgerStats.get(this.player.getUniqueId());

        if (this.actualPB != null) {
            this.plugin.getTypeHandler().sendNewPB(account, this.actualPB);
        }

        if (this.actualMedal != null) {
            this.plugin.getTypeHandler().sendNewMedal(account, this.actualMedal);
        }

        account.addBlocksPlaced(this.actualPlacedBlocks);
        account.addBridgesMade(this.actualMadeBridges);
        account.addBridgeFailed(this.actualFailedBridges);
        account.addPlayedTimeInMs(System.currentTimeMillis() - this.connectionTime);

        account.update();
    }

    private Location calculateLocationForThisPlayer(Location location) {
        BridgerConfig config = this.plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        Location forFirstIsland = location.clone();
        return forFirstIsland.add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber);
    }

    public void setIslandNumber(int islandNumber) {
        this.plugin.getGame().getEmplacements().set(islandNumber, true);
        this.init(this.plugin, islandNumber);
    }

    public HyriBridgerDuration getPB() {
        if (this.actualPB != null) {
            return this.actualPB;
        }
        if (this.oldPB != null) {
            return this.oldPB;
        }
        return null;
    }

    public Medal getMedal() {
        if (this.actualMedal != null) {
            return this.actualMedal;
        }
        if (this.oldMedal != null) {
            return this.oldMedal;
        }
        return null;
    }

    private HyriBridgerDuration getOldPB() {
        if (this.plugin.getGame().getType().equals(BridgerGameType.SHORT)) {
            return this.oldAccount.getPersonalShortBest();
        }
        if (this.plugin.getGame().getType().equals(BridgerGameType.NORMAL)) {
            return this.oldAccount.getPersonalNormalBest();
        }
        return this.oldAccount.getPersonalDiagonalBest();
    }

    private Medal getOldMedal() {
        if (this.plugin.getGame().getType().equals(BridgerGameType.SHORT)) {
            return this.oldAccount.getHighestAcquiredShortMedal();
        }
        if (this.plugin.getGame().getType().equals(BridgerGameType.NORMAL)) {
            return this.oldAccount.getHighestAcquiredNormalMedal();
        }
        return this.oldAccount.getHighestAcquiredDiagonalMedal();
    }



    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(this.player);
    }

    public boolean isSpectating() {
        return spectating;
    }

    public boolean isBridging() {
        return isBridging;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public BridgerTimer getActualTimer() {
        return actualTimer;
    }

    public Area getGameArea() {
        return this.gameArea;
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    public List<BridgerGamePlayer> getWatchers() {
        return watchers;
    }

    public BridgerBlock getActualBlock() {
        return actualBlock;
    }

    public void setActualBlock(BridgerBlock actualBlock) {
        this.actualBlock = actualBlock;
        this.giveBlocks();
    }

    public HyriBridgerData getAccountData() {
        return HyriBridgerData.get(HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId()));
    }

    public void addWatcher(BridgerGamePlayer gamePlayer) {
        this.watchers.add(gamePlayer);
    }

    public void addActualPlacedBlocks(long actualPlacedBlocks) {
        this.actualPlacedBlocks += actualPlacedBlocks;
    }

    public void addActualMadeBridges(long actualMadeBridges) {
        this.actualMadeBridges += actualMadeBridges;
    }

    public void addActualFailedBridges(long actualFailedBridges) {
        this.actualFailedBridges += actualFailedBridges;
    }

    public BridgerGamePlayer getWatchedPlayer() {
        return watchedPlayer;
    }
}