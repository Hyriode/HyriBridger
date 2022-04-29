package fr.hyriode.bridger.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.api.player.HyriBridgerPlayer;
import fr.hyriode.bridger.api.player.Medal;
import fr.hyriode.bridger.config.BridgerConfig;
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
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BridgerGamePlayer extends HyriGamePlayer {

    private Bridger plugin;
    private int playerNumber;
    private Location spawn;
    private Material blockType;
    private BridgerTimer actualTimer;
    private boolean isBridging;
    private HyriBridgerScoreboard scoreboard;
    private final List<Location> placedBlocks;
    private Area gameArea;
    private Location hologramLocation;
    private Location npcLocation;
    private Hologram hologram;
    private NPC npc;

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
    private final long connexionTime;

    private Supplier<HyriBridgerPlayer> accountSupplier;

    public BridgerGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
        this.actualPlacedBlocks = 0;
        this.actualMadeBridges = 0;
        this.actualFailedBridges = 0;
        this.connexionTime = System.currentTimeMillis();
        this.placedBlocks = new ArrayList<>();
    }

    public void init(Bridger plugin, int playerNumber) {
        this.plugin = plugin;
        this.accountSupplier = () -> this.plugin.getApi().getPlayerManager().getPlayer(this.player.getUniqueId());
        this.playerNumber = playerNumber;

        this.spawn = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getSpawnLocationOnFirstIsland().asBukkit());
        this.hologramLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getHologramLocationOnFirstIsland().asBukkit());
        this.npcLocation = this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getNpcLocationOnFirstIsland().asBukkit());

        this.blockType = Material.SANDSTONE;
        this.setupScoreboard();
        this.setupNPC();

        this.spawnPlayer();
        this.gameArea = new Area(this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandFirst().asBukkit()),this.calculateLocationForThisPlayer(this.plugin.getConfiguration().getGameAreaOnFirstIslandSecond().asBukkit()));

        this.oldPB = this.getOldPB();
        this.oldMedal = this.getOldMedal();
        this.oldPlacedBlocks = this.accountSupplier.get().getBlocksPlaced();
        this.oldMadeBridges = this.accountSupplier.get().getBridgesMade();
        this.oldFailedBridges = this.accountSupplier.get().getBridgeFailed();
        this.playedTime = this.accountSupplier.get().getPlayedTimeInMs();
        this.refreshHologram();
    }

    public void spawnPlayer() {
        this.player.teleport(this.spawn);
        this.player.getInventory().clear();
        this.player.getInventory().addItem(new ItemStack(blockType, 64*9));
        this.player.getInventory().setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());
        if(!this.placedBlocks.isEmpty()) {
            this.deletePlacedBlocks();
        }
        this.placedBlocks.clear();
    }

    public void startBridging() {
        this.startTimer();
        this.isBridging = true;
    }

    public void endBridging(boolean success) {
        this.endTimer();
        this.isBridging = false;

        if(success) {
            if((this.actualPB == null || this.actualPB.getExactTime() > this.actualTimer.getFinalTime()) && (this.oldPB == null || this.oldPB.getExactTime() > this.actualTimer.getFinalTime())) {
                this.successPersonalBest();
            }else {
                this.failPersonalBest();
            }

            this.plugin.getGame().getSession().add(this.player, this.actualTimer.toFinalDuration());

            HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId()).getHyris().add(50, false);
            HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId()).getNetworkLeveling().addExperience(75);
            this.addActualMadeBridges(1);
        }else {
            this.player.sendMessage(ChatColor.RED + this.getValue("message.player.failed-bridge")
                    .replace("%block%", String.valueOf(this.placedBlocks.size())));
            this.addActualFailedBridges(1);
        }

        this.deletePlacedBlocks();
        this.refreshHologram();
        this.spawnPlayer();
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
        for (Location placedBlock : placedBlocks) {
            if(placedBlocks.size() < 50) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                    if(placedBlock.getBlock().getType() != Material.AIR) {
                        placedBlock.getBlock().setType(Material.AIR);
                    }
                }, this.placedBlocks.indexOf(placedBlock));
            }else {
                if(placedBlock.getBlock().getType() != Material.AIR) {
                    placedBlock.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public void sendPlayerStats() {
        HyriBridgerPlayer account = this.plugin.getApi().getPlayerManager().getPlayer(this.getUUID());

        if(this.actualPB != null) {
            if(this.game.getType().equals(BridgerGameType.SHORT)) {
                account.setPersonalShortBest(this.actualPB);
            }else if(this.game.getType().equals(BridgerGameType.NORMAL)) {
                account.setPersonalNormalBest(this.actualPB);
            }else if(this.game.getType().equals(BridgerGameType.DIAGONAL)) {
                account.setPersonalDiagonalBest(this.actualPB);
            }
        }

        if(this.actualMedal != null) {
            if(this.game.getType().equals(BridgerGameType.SHORT)) {
                account.setHighestAcquiredShortMedal(this.actualMedal);
            }else if(this.game.getType().equals(BridgerGameType.NORMAL)) {
                account.setHighestAcquiredNormalMedal(this.actualMedal);
            }else if(this.game.getType().equals(BridgerGameType.DIAGONAL)) {
                account.setHighestAcquiredDiagonalMedal(this.actualMedal);
            }
        }

        account.addBlocksPlaced(this.actualPlacedBlocks);
        account.addBridgesMade(this.actualMadeBridges);
        account.addBridgeFailed(this.actualFailedBridges);
        account.addPlayedTimeInMs(System.currentTimeMillis() - this.connexionTime);

        this.plugin.getApi().getPlayerManager().sendPlayer(account);
    }

    private void startTimer() {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if(actualTimer.getActualTime() > 1000000-1) {
                this.endBridging(false);
            }else {
                new ActionBar(ChatColor.DARK_AQUA + actualTimer.getFormattedActualTime()).send(player);
            }
        }, 1, 0);
        this.actualTimer = new BridgerTimer(bukkitTask);
        this.actualTimer.start();
    }

    private void endTimer() {
        this.actualTimer.getLinkedTask().cancel();
        this.actualTimer.end();
    }

    private void setupScoreboard() {
        if(this.scoreboard != null) {
            this.scoreboard.hide();
        }
        this.scoreboard = new HyriBridgerScoreboard(this.plugin, this.player);
        this.scoreboard.show();
    }

    private void refreshHologram() {
        if(this.hologram != null) {
            this.hologram.removeReceiver(player);
            this.hologram.destroy();
        }
        this.hologram = new Hologram.Builder(this.plugin, this.hologramLocation)
                .withLine(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + this.getValue("hologram.stats"))
                .withLine(ChatColor.AQUA + this.getValue("scoreboard.medal.actual") + (this.getMedal() != null ? this.getValue(this.getMedal().getLanguageValue()) : ChatColor.RED + "✘"))
                .withLine(ChatColor.AQUA + this.getValue("hologram.placed-blocks") + ChatColor.YELLOW + (this.oldPlacedBlocks + this.actualPlacedBlocks))
                .withLine(ChatColor.AQUA + this.getValue("hologram.made-bridges") + ChatColor.YELLOW + (this.oldMadeBridges + this.actualMadeBridges))
                .withLine(ChatColor.AQUA + this.getValue("hologram.failed-bridged") + ChatColor.YELLOW + (this.oldFailedBridges + this.actualFailedBridges))
                .withLine(ChatColor.AQUA + this.getValue("hologram.played-time") + ChatColor.YELLOW + new BridgerPlayedDuration(Duration.ofMillis(this.playedTime + (System.currentTimeMillis() - this.connexionTime)), this.player).toFormattedTime())
                .build();
        this.hologram.setLocation(this.hologramLocation);
        this.hologram.addReceiver(this.player);
        this.hologram.sendLines();
    }

    public void setupNPC() {
        if(this.npc != null) {
            NPCManager.removeNPC(this.npc);
        }

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

    private Location calculateLocationForThisPlayer(Location location) {
        BridgerConfig config = plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        Location forFirstIsland = location.clone();
        return forFirstIsland.add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber);
    }

    private HyriBridgerDuration getOldPB() {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            return this.accountSupplier.get().getPersonalShortBest();
        }
        if(this.game.getType().equals(BridgerGameType.NORMAL)) {
            return this.accountSupplier.get().getPersonalNormalBest();
        }
        return this.accountSupplier.get().getPersonalDiagonalBest();
    }

    private Medal getOldMedal() {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            return this.accountSupplier.get().getHighestAcquiredShortMedal();
        }
        if(this.game.getType().equals(BridgerGameType.NORMAL)) {
            return this.accountSupplier.get().getHighestAcquiredNormalMedal();
        }
        return this.accountSupplier.get().getHighestAcquiredDiagonalMedal();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public BridgerTimer getActualTimer() {
        return actualTimer;
    }

    public boolean isBridging() {
        return isBridging;
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

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    public Area getGameArea() {
        return gameArea;
    }

    private String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.player, key);
    }

    public void setPlayerNumber(int playerNumber) {
        this.plugin.getGame().getEmplacements().set(playerNumber, true);
        this.init(this.plugin, playerNumber);
    }

    public NPC getNPC() {
        return this.npc;
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
}
