package fr.hyriode.bridger.game;

import fr.hyriode.api.HyriAPI;
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
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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
    private List<Location> placedBlocks;
    private Area gameArea;
    private Location hologramLocation;
    private Location npcLocation;
    private Hologram hologram;
    private NPC npc;

    private long oldPlacedBlocks;
    private long oldMadeBridges;
    private long oldFailedBridges;
    private long playedTime;

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

        this.oldPlacedBlocks = this.accountSupplier.get().getBlocksPlaced();
        this.oldMadeBridges = this.accountSupplier.get().getBridgesMade();
        this.oldFailedBridges = this.accountSupplier.get().getBridgeFailed();
        this.playedTime = this.accountSupplier.get().getPlayedTimeInMs();
        this.setupHologram();
    }

    public void spawnPlayer() {
        this.player.teleport(this.spawn);
        this.player.getInventory().clear();
        this.player.getInventory().addItem(new ItemStack(blockType, 64*9));
        this.player.getInventory().setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());
    }

    public void startBridging() {
        this.startTimer();
        this.isBridging = true;
        this.placedBlocks = new ArrayList<>();
    }

    public void endBridging(boolean isEnded) {
        this.endTimer();
        this.isBridging = false;

        if(isEnded) {
            this.addActualMadeBridges(1);

            HyriBridgerPlayer account = this.accountSupplier.get();
            if(account.getPersonalBest() == null || account.getPersonalBest().getExactTime() > this.actualTimer.getFinalTime()) {
                account.setPersonalBest(this.actualTimer.toFinalDuration());

                /*Medal actualHighestMedal = this.getHighestAcquiredMedalInThisGameType();
                for (Medal medal : Medal.values()) {
                    System.out.println("test for " + medal.name());
                    if(this.getTimeToReachInThisGameType(medal) >= this.actualTimer.getFinalTime()) {
                        System.out.println("good");
                        if(actualHighestMedal == null || medal.getId() > actualHighestMedal.getId()) {
                            System.out.println("uiui");
                            this.setHighestAcquiredMedalInThisGameType(medal);
                        }
                    }
                }*/
                this.plugin.getApi().getPlayerManager().sendPlayer(account);

                this.player.playSound(this.player.getLocation(), Sound.ORB_PICKUP, 1, 1);
            }
            this.player.playNote(this.player.getLocation(), Instrument.PIANO, new Note(0));
            Title.sendTitle(this.player, new Title(ChatColor.GREEN + this.actualTimer.toFinalDuration().toFormattedTime(), "", 0, 3*20, 2*20));
            this.spawnPlayer();

            this.plugin.getGame().getSession().add(this.player, this.actualTimer.toFinalDuration());
            this.player.sendMessage(this.getValue("message.player.succeed-bridge"));
            HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId()).getHyris().add(50);
            HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId()).getNetworkLeveling().addExperience(200);

        }else {
            this.player.sendMessage(ChatColor.RED + this.getValue("message.player.failed-bridge")
                    .replace("%block%", String.valueOf(this.placedBlocks.size())));
            this.addActualFailedBridges(1);
            this.player.teleport(this.spawn);
        }

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

        this.setupHologram();
    }

    public void sendPlayerStats() {
        HyriBridgerPlayer account = this.plugin.getApi().getPlayerManager().getPlayer(this.getUUID());

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

    private void setupHologram() {
        if(this.hologram != null) {
            this.hologram.removeReceiver(player);
            this.hologram.destroy();
        }
        this.hologram = new Hologram.Builder(this.plugin, this.hologramLocation)
                //.withLine(ChatColor.AQUA + this.getValue("scoreboard.medal.actual") + (this.getHighestAcquiredMedalInThisGameType() != null ?this.getValue(this.getHighestAcquiredMedalInThisGameType().getLanguageValue()) : ChatColor.RED + "✘"))
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

    public Medal getHighestAcquiredMedalInThisGameType() {
        int id = 0;
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            id = this.accountSupplier.get().getHighestAcquiredShortMedal();
        }else if(this.game.getType().equals(BridgerGameType.LONG)) {
            id = this.accountSupplier.get().getHighestAcquiredLongMedal();
        }else {
            id = this.accountSupplier.get().getHighestAcquiredDiagonalMedal();
        }
        if(id == 0) {
            return null;
        }else {
            return Medal.getById(id);
        }
    }

    public void setHighestAcquiredMedalInThisGameType(Medal medal) {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            this.accountSupplier.get().setHighestAcquiredShortMedal(medal.getId());
        }else if(this.game.getType().equals(BridgerGameType.LONG)) {
            this.accountSupplier.get().setHighestAcquiredLongMedal(medal.getId());
        }else {
            this.accountSupplier.get().setHighestAcquiredDiagonalMedal(medal.getId());
        }
        this.plugin.getApi().getPlayerManager().sendPlayer(this.accountSupplier.get());
    }

    public long getTimeToReachInThisGameType(Medal medal) {
        if(this.game.getType().equals(BridgerGameType.SHORT)) {
            return medal.getTimeToReachShort();
        }else if(this.game.getType().equals(BridgerGameType.LONG)) {
            return medal.getTimeToReachLong();
        }else {
            return medal.getTimeToReachDiagonal();
        }
    }
}
