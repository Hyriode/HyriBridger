package fr.hyriode.bridger.game.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerData;
import fr.hyriode.bridger.api.BridgerDuration;
import fr.hyriode.bridger.api.BridgerMedal;
import fr.hyriode.bridger.api.BridgerStatistics;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.scoreboard.HyriBridgerScoreboard;
import fr.hyriode.bridger.game.task.BridgeTask;
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

    //== Utils
    private HyriBridger plugin;
    private BridgerGame game;

    //== Config
    private int playerNumber;
    private Location spawn;
    private Area gameArea;
    private Location hologramLocation;
    private Location npcLocation;

    //== User experience
    private HyriBridgerScoreboard scoreboard;
    private NPC npc;
    private Hologram hologram;
    private BridgerBlock actualBlock;

    //== Temporary data
    private BridgerPlayerState state;
    private final List<Location> placedBlocks = new ArrayList<>();
    private BridgerTimer actualTimer;
    private BridgeTask bridgeTask;

    //== Data
    private BridgerStatistics statistics;
    private BridgerStatistics.Data statisticsData;
    private BridgerData data;

    public BridgerGamePlayer(Player player) {
        super(player);
    }

    /*public void initSpec(BridgerGamePlayer watchedPlayer) {
        if (this.isSpectating()) {
            this.exitSpec();
        }

        if (this.isBridging()) {
            this.endBridging(false);
        }
        game.getEmplacements().set(this.playerNumber, false);

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
    }*/

    /*public void exitSpec() {
        this.spectating = false;
        this.watchedPlayer.getWatchers().remove(this);
        this.watchedPlayer = null;
    }*/

    public void spawnPlayer() {
        this.player.teleport(this.spawn);
        this.player.getInventory().clear();
        this.giveBlocks();
        this.player.setGameMode(GameMode.SURVIVAL);

        this.deletePlacedBlocks();
        this.placedBlocks.clear();
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
            new ActionBar(ChatColor.RED + this.getTranslatedMessage("message.player.failed-bridge")
                    .replace("%block%", "0")).send(this.player);
        }

    }

    public void startBridging() {
        this.startTimer();
        this.state = BridgerPlayerState.BRIDGING;
    }

    //TODO move to BridgeTask.java `void startTimer()`
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
        this.state = BridgerPlayerState.SPAWN;
        this.endTimer();

        if (success && this.placedBlocks.size() > 20 && this.getActualTimer().getActualTime() > 3700) {
            if (this.isPB()) {
                this.successPersonalBest();
            } else {
                this.failPersonalBest();
            }

            game.getSession().add(this.player, this.actualTimer.toFinalDuration());

            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId());
            account.getHyris().add(5).exec();
            account.getNetworkLeveling().addExperience(10);
            account.update();

            statisticsData.addBridgesMade(1);
        } else {
            new ActionBar(ChatColor.RED + this.getTranslatedMessage("message.player.failed-bridge")
                    .replace("%block%", String.valueOf(this.placedBlocks.size()-1))).send(this.player);
            this.addActualFailedBridges(1);
        }

        this.deletePlacedBlocks();
        this.refreshHologram();

        this.spawnPlayer();
        this.isInEndingBridging = false;
        this.isBridging = false;

        statisticsData.addBlocksPlaced(placedBlocks.size());
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
        if (this.getMedal() == null || !this.getMedal().equals(BridgerMedal.ULTIMATE)) {
            for (BridgerMedal bridgerMedal : BridgerMedal.values()) {
                if (this.actualTimer.toFinalDuration().getExactTime() < bridgerMedal.getTimeToReach(game.getType())) {
                    this.successMedal(bridgerMedal);
                }
            }
        }

        this.actualPB = this.actualTimer.toFinalDuration();

        this.player.playSound(this.spawn, Sound.LEVEL_UP, 10, 1);

        Title.sendTitle(this.player, new Title(ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), ChatColor.DARK_AQUA + this.getTranslatedMessage("title.sub.player.pb")
                .replace("%pb%", ChatColor.AQUA + this.actualTimer.toFinalDuration().toFormattedTime())
                , 5, 40, 15));
        this.plugin.getMessageHelper().sendSuccessPBMessage(this.player, this.actualTimer.toFinalDuration());
    }

    private void successMedal(BridgerMedal bridgerMedal) {
        this.actualBridgerMedal = bridgerMedal;

        final HyriBridgerData account = HyriBridgerData.get(this.player.getUniqueId());
        account.addBlockForMedal(bridgerMedal, this.player.getUniqueId());
        account.update(this.player.getUniqueId());
    }


    private void failPersonalBest() {
        this.player.playSound(this.spawn, Sound.ORB_PICKUP, 10, 1);
        Title.sendTitle(this.player, ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), "", 5, 20, 5);
        this.plugin.getMessageHelper().sendFailedPBMessage(this.player, this.actualPB, this.actualTimer.toFinalDuration());
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
                .withLine(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + this.getTranslatedMessage("hologram.stats"))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("scoreboard.medal.actual") + (this.getMedal() != null ? this.getTranslatedMessage(this.getMedal().getLanguageValue()) : ChatColor.RED + "✘"))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.placed-blocks") + ChatColor.YELLOW + (this.oldPlacedBlocks + this.actualPlacedBlocks))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.made-bridges") + ChatColor.YELLOW + (this.oldMadeBridges + this.actualMadeBridges))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.failed-bridged") + ChatColor.YELLOW + (this.oldFailedBridges + this.actualFailedBridges))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.played-time") + ChatColor.YELLOW + new BridgerPlayedDuration(Duration.ofMillis(this.playedTime + (System.currentTimeMillis() - getPlayTime())), this.player).toFormattedTime())
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
        npcHolo.add(this.getTranslatedMessage("npc.name"));
        npcHolo.add(this.getTranslatedMessage("npc.lore"));

        this.npc = NPCManager.createNPC(this.npcLocation, BridgerGame.NPC_SKIN, npcHolo);

        this.npc.addPlayer(this.player)
                .setTrackingPlayer(true)
                .setShowingToAll(true)
                .setInteractCallback((rightClick, clicker) -> {
                    if (!rightClick) {
                        return;
                    }

                    if (game.getPlayer(clicker).isSpectating()) {
                        return;
                    }
                    new MainGUI(this.plugin, clicker).open();
                });

        NPCManager.sendNPC(this.npc);
    }

    private Location calculateLocationForThisPlayer(Location location) {
        BridgerConfig config = this.plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        Location forFirstIsland = location.clone();
        return forFirstIsland.add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber);
    }

    public void setIslandNumber(int islandNumber) {
        game.getEmplacements().set(islandNumber, true);
        this.onJoin(this.plugin, islandNumber);
    }

    public BridgerDuration getPB() {
        if (this.actualPB != null) {
            return this.actualPB;
        }
        if (this.oldPB != null) {
            return this.oldPB;
        }
        return null;
    }

    public BridgerMedal getMedal() {
        if (this.actualBridgerMedal != null) {
            return this.actualBridgerMedal;
        }
        if (this.oldBridgerMedal != null) {
            return this.oldBridgerMedal;
        }
        return null;
    }


    //==
    public void onJoin() {
        this.spawn = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getSpawnLocationOnFirstIsland().asBukkit());
        this.hologramLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getHologramLocationOnFirstIsland().asBukkit());
        this.npcLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getNpcLocationOnFirstIsland().asBukkit());
        this.gameArea = new Area(this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandFirst().asBukkit()),this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandSecond().asBukkit()));

        this.setupScoreboard();
        this.setupNPC();
        this.refreshHologram();

        //TODO this.actualBlock = BridgerBlock.getById(HyriBridgerData.get(this.player.getUniqueId()).getActualBlockId());

        PlayerUtil.resetPlayer(this.player, true);
        PlayerUtil.showPlayer(this.player);

        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setCanPickupItems(false);
        //Spawn player
        this.spawnPlayer();
    }

    public void onLeave() {
        if (isBridging()) {
            endBridging(false);
        }

        deleteHologram();
        deleteNPC();

        game.getSession().removeScoresOf(player);
        game.getEmplacements().set(playerNumber, false);
    }

    public void joinSpectators(BridgerGamePlayer target) {

    }

    public void quitSpectators() {

    }

    public void setActualBlock(BridgerBlock actualBlock) {
        this.actualBlock = actualBlock;
        this.giveBlocks();
    }

    private void deletePlacedBlocks() {
        //TODO animation
        for (Location placedBlock : placedBlocks) {
            placedBlock.getBlock().setType(Material.AIR);
            this.sendBlockChange(placedBlock, Material.AIR, (byte) 0);
        }
        placedBlocks.clear();
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        if (((CraftPlayer) this.player).getHandle().playerConnection != null) {
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld)loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private String getTranslatedMessage(String key) {
        return HyriLanguageMessage.get(key).getValue(this.player);
    }

    public boolean isBridging() {
        return state == BridgerPlayerState.BRIDGING;
    }

    public boolean isSpectating() {
        return state == BridgerPlayerState.SPECTATING;
    }

    public BridgerStatistics getStatistics() {
        return statistics;
    }

    public BridgerStatistics.Data getStatisticsData() {
        return statisticsData;
    }

    public void setStatistics(BridgerStatistics statistics) {
        this.statistics = statistics;
        this.statisticsData = statistics.getData(game.getType());
    }

    public HyriBridger getPlugin() {
        return plugin;
    }

    public void setPlugin(HyriBridger plugin) {
        this.plugin = plugin;
    }

    public BridgerGame getGame() {
        return game;
    }

    public void setGame(BridgerGame game) {
        this.game = game;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Area getGameArea() {
        return gameArea;
    }

    public void setGameArea(Area gameArea) {
        this.gameArea = gameArea;
    }

    public Location getHologramLocation() {
        return hologramLocation;
    }

    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    public Location getNpcLocation() {
        return npcLocation;
    }

    public void setNpcLocation(Location npcLocation) {
        this.npcLocation = npcLocation;
    }

    public HyriBridgerScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(HyriBridgerScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void setHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public BridgerBlock getActualBlock() {
        return actualBlock;
    }

    public BridgerPlayerState getState() {
        return state;
    }

    public void setState(BridgerPlayerState state) {
        this.state = state;
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    public BridgerTimer getActualTimer() {
        return actualTimer;
    }

    public void setActualTimer(BridgerTimer actualTimer) {
        this.actualTimer = actualTimer;
    }

    public BridgeTask getBridgeTask() {
        return bridgeTask;
    }

    public void setBridgeTask(BridgeTask bridgeTask) {
        this.bridgeTask = bridgeTask;
    }

    public BridgerData getData() {
        return data;
    }

    public void setData(BridgerData data) {
        this.data = data;
    }
}