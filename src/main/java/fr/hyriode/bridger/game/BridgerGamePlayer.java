package fr.hyriode.bridger.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.api.player.HyriBridgerStats;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.timers.BridgerPlayedDuration;
import fr.hyriode.bridger.gui.ChangeModeGUI;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.scoreboard.HyriBridgerScoreboard;
import fr.hyriode.bridger.game.timers.BridgerTimer;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BridgerGamePlayer extends HyriGamePlayer {

    private Bridger plugin;
    private int playerNumber;

    private List<BridgerGamePlayer> watchers;
    private boolean isSpec;
    private BridgerGamePlayer watchedPlayer;

    private Location spawn;
    private BridgerBlock actualBlock;
    private ItemStack actualItemStack;

    private BridgerTimer actualTimer;
    private boolean isBridging;
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

    public BridgerGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void initSpec(Bridger plugin, BridgerGamePlayer watchedPlayer) {
        this.plugin = plugin;
        this.isSpec = true;
        this.watchedPlayer = watchedPlayer;
        this.watchers = new ArrayList<>();

        this.oldAccount =  HyriBridgerStats.get(this.player.getUniqueId());

        this.watchedPlayer.addWatcher(watchedPlayer);
        PlayerUtil.addSpectatorAbilities(this.player);
        PlayerUtil.hidePlayer(this.player,false);

        this.deleteNPC();
        this.deleteHologram();

        this.player.setGameMode(GameMode.SPECTATOR);
        this.player.teleport(this.watchedPlayer.player);
    }

    public void init(Bridger plugin, int playerNumber) {
        this.actualBlock = BridgerBlock.getById(0);

        this.plugin = plugin;
        this.isSpec = false;
        this.playerNumber = playerNumber;
        this.watchers = new ArrayList<>();

        this.oldAccount = HyriBridgerStats.get(this.player.getUniqueId());

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
        this.spawn = this.calculateLocationForThisPlayer(new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5, 180F, 0F));
        this.hologramLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getHologramLocationOnFirstIsland().asBukkit());
        this.npcLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getNpcLocationOnFirstIsland().asBukkit());
        this.gameArea = new Area(this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandFirst().asBukkit()),this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandSecond().asBukkit()));
         //Setup game
        this.actualItemStack = new ItemStack(this.actualBlock.getMaterial(), 64*9, this.actualBlock.getMeta());
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

    public void spawnPlayer() {
        this.player.teleport(this.spawn);
        this.player.getInventory().clear();
        this.player.getInventory().addItem(this.actualItemStack);
        this.player.getInventory().setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());

        this.deletePlacedBlocks();
        this.placedBlocks = new ArrayList<>();
    }

    public void startBridging() {
        this.startTimer();
        this.isBridging = true;
    }

    private void startTimer() {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if(actualTimer.getActualTime() > Duration.ofHours(10).toMillis()-1) {
                this.endBridging(false);
            }else {
                new ActionBar(ChatColor.DARK_AQUA + actualTimer.getFormattedActualTime()).send(player);
            }
        }, 1, 0);
        this.actualTimer = new BridgerTimer(bukkitTask);
        this.actualTimer.start();
    }

    public void endBridging(boolean success) {
        this.endTimer();
        this.isBridging = false;

        if(success && this.placedBlocks.size() > 20 && this.getActualTimer().getActualTime() > 3000) {
            if((this.actualPB == null || this.actualPB.getExactTime() > this.actualTimer.getFinalTime()) && (this.oldPB == null || this.oldPB.getExactTime() > this.actualTimer.getFinalTime())) {
                this.successPersonalBest();
            }else {
                this.failPersonalBest();
            }

            this.plugin.getGame().getSession().add(this.player, this.actualTimer.toFinalDuration());

            IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId());
            hyriPlayer.getHyris().add(50).exec();
            hyriPlayer.getNetworkLeveling().addExperience(75);

            hyriPlayer.update();
            this.addActualMadeBridges(1);
        }else {
            new ActionBar(ChatColor.RED + this.getValue("message.player.failed-bridge")
                    .replace("%block%", String.valueOf(this.placedBlocks.size()-1))).send(this.player);
            this.addActualFailedBridges(1);
        }

        this.deletePlacedBlocks();
        this.refreshHologram();
        this.spawnPlayer();
    }

    private void endTimer() {
        this.actualTimer.getLinkedTask().cancel();
        this.actualTimer.end();
    }

    private void successPersonalBest() {
        if(this.getMedal() != null) {
            if(!this.getMedal().equals(Medal.PLATINUM)) {
                for(int i = this.getMedal().getId()+1;i <= 4;i++) {
                    long medalTimeToReach = Medal.getById(i).getTimeToReach(this.game.getType().getName());
                    if(this.actualTimer.toFinalDuration().getExactTime() < medalTimeToReach) {
                        this.actualMedal = Medal.getById(i);
                    }
                }
            }
        }else {
            for(int i = 1;i <= 4;i++) {
                long medalTimeToReach = Medal.getById(i).getTimeToReach(this.game.getType().getName());
                if(this.actualTimer.toFinalDuration().getExactTime() < medalTimeToReach) {
                    this.actualMedal = Medal.getById(i);
                }
            }
        }

        this.actualPB = this.actualTimer.toFinalDuration();

        this.player.playSound(this.spawn, Sound.LEVEL_UP, 10, 1);
        Title.sendTitle(this.player, new Title(ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), this.getValue("title.sub.player.pb").replace("%pb%", ChatColor.AQUA + this.actualTimer.toFinalDuration().toFormattedTime()), 5, 40, 15));
        this.player.sendMessage(this.getValue("message.player.succeed-pb")
                .replace("%pb%", ChatColor.DARK_AQUA + this.actualTimer.toFinalDuration().toFormattedTime()));
    }

    private void failPersonalBest() {
        this.player.playSound(this.spawn, Sound.ORB_PICKUP, 10, 1);
        Title.sendTitle(this.player, ChatColor.AQUA + this.getActualTimer().toFinalDuration().toFormattedTime(), "", 5, 20, 5);
        this.player.sendMessage(this.getValue("message.player.failed-pb")
                .replace("%pb%", ChatColor.YELLOW + this.getPB().toFormattedTime())
                .replace("%time%", ChatColor.DARK_AQUA + this.actualTimer.toFinalDuration().toFormattedTime()));
    }

    private void deletePlacedBlocks() {
        if(placedBlocks.size() < 30) {
            for (Location placedBlock : placedBlocks) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        if(!placedBlock.getBlock().getType().equals(Material.AIR)) {
                            placedBlock.getBlock().setType(Material.AIR);
                        }
                    }
                }, this.placedBlocks.indexOf(placedBlock));
            }
        }else {
            for (Location placedBlock : placedBlocks) {
                if(placedBlock.getBlock().getType() != Material.AIR) {
                    placedBlock.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private void setupScoreboard() {
        if(this.scoreboard != null) {
            this.scoreboard.hide();
        }
        this.scoreboard = new HyriBridgerScoreboard(this.plugin, this.player);
        this.scoreboard.show();
    }

    public void deleteHologram() {
        if(this.hologram != null) {
            this.hologram.removeReceiver(player);
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
        if(this.npc != null) {
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
                    if (rightClick) new ChangeModeGUI(this.plugin, clicker).open();
                });

        NPCManager.sendNPC(this.npc);
    }

    public void sendPlayerStats() {
        HyriBridgerStats account = HyriBridgerStats.get(this.player.getUniqueId());

        if(this.actualPB != null) {
            if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.SHORT.getName())) {
                account.setPersonalShortBest(this.actualPB);
            }else if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.NORMAL.getName())) {
                account.setPersonalNormalBest(this.actualPB);
            }/*else if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.DIAGONAL.getName())) {
                account.setPersonalDiagonalBest(this.actualPB);
            }*/
        }

        if(this.actualMedal != null) {
            if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.SHORT.getName())) {
                account.setHighestAcquiredShortMedal(this.actualMedal);
            }else if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.NORMAL.getName())) {
                account.setHighestAcquiredNormalMedal(this.actualMedal);
            }/*else if(this.game.getType().getName().equalsIgnoreCase(BridgerGameType.DIAGONAL.getName())) {
                account.setHighestAcquiredDiagonalMedal(this.actualMedal);
            }*/
        }

        account.addBlocksPlaced(this.actualPlacedBlocks);
        account.addBridgesMade(this.actualMadeBridges);
        account.addBridgeFailed(this.actualFailedBridges);
        account.addPlayedTimeInMs(System.currentTimeMillis() - this.connectionTime);

        account.update(this.player.getUniqueId());
    }

    private Location calculateLocationForThisPlayer(Location location) {
        BridgerConfig config = this.plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        Location forFirstIsland = location.clone();
        return forFirstIsland.add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber);
    }

    public void setPlayerNumber(int playerNumber) {
        this.plugin.getGame().getEmplacements().set(playerNumber, true);
        this.init(this.plugin, playerNumber);
    }

    public HyriBridgerDuration getPB() {
        if(this.actualPB != null) {
            return this.actualPB;
        }
        if(this.oldPB != null) {
            return this.oldPB;
        }
        return null;
    }

    public Medal getMedal() {
        if(this.actualMedal != null) {
            return this.actualMedal;
        }
        if(this.oldMedal != null) {
            return this.oldMedal;
        }
        return null;
    }

    private HyriBridgerDuration getOldPB() {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            return this.oldAccount.getPersonalShortBest();
        }
        if(this.game.getType().equals(BridgerGameType.NORMAL)) {
            return this.oldAccount.getPersonalNormalBest();
        }
        return this.oldAccount.getPersonalDiagonalBest();
    }

    private Medal getOldMedal() {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            return this.oldAccount.getHighestAcquiredShortMedal();
        }
        if(this.game.getType().equals(BridgerGameType.NORMAL)) {
            return this.oldAccount.getHighestAcquiredNormalMedal();
        }
        return this.oldAccount.getHighestAcquiredDiagonalMedal();
    }



    private String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.player, key);
    }

    public boolean isSpec() {
        return isSpec;
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

    public NPC getNPC() {
        return this.npc;
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

}
