package fr.hyriode.bridger.game.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriTransaction;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.*;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.bridger.game.blocks.BridgerBlock;
import fr.hyriode.bridger.game.item.LeaveLeaderboardsItem;
import fr.hyriode.bridger.game.item.TeleportLeaderboardsItem;
import fr.hyriode.bridger.game.leaderboard.BridgerLeaderboard;
import fr.hyriode.bridger.game.scoreboard.HyriBridgerScoreboard;
import fr.hyriode.bridger.game.task.BridgeTask;
import fr.hyriode.bridger.game.timers.BridgerPlayedDuration;
import fr.hyriode.bridger.game.timers.BridgerTimer;
import fr.hyriode.bridger.gui.MainGUI;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.util.HyriGameItems;
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
import org.bukkit.inventory.PlayerInventory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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

    //== Temporary data
    private BridgerPlayerState state;
    private final List<Location> placedBlocks = new ArrayList<>();
    private BridgeTask bridgeTask;
    private BridgerTimer timer;

    //== Data
    private BridgerStatistics statistics;
    private BridgerStatistics.Data statisticsData;
    private BridgerData data;

    public BridgerGamePlayer(Player player) {
        super(player);
    }

    public void onJoin() {
        this.spawn = this.calculateLocation(this.plugin.getConfiguration().getIslandSpawn().asBukkit());
        this.player.teleport(this.spawn);
        this.hologramLocation = this.calculateLocation(this.plugin.getConfiguration().getIslandHologram().asBukkit());
        this.npcLocation = this.calculateLocation(this.plugin.getConfiguration().getIslandNpc().asBukkit());
        this.gameArea = this.calculateArea(this.plugin.getConfiguration().getIslandArea().asArea());

        this.setupScoreboard();
        this.setupNPC();
        this.refreshHologram();

        PlayerUtil.resetPlayer(this.player, true);
        PlayerUtil.showPlayer(this.player);

        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setCanPickupItems(false);
        //Spawn player
        this.spawnPlayer();
    }

    public void onLeave() {
        if (isBridging()) {
            this.endBridging(false);
        }

        deleteHologram();
        deleteNPC();

        game.getSession().removeScoresOf(player);
        game.getEmplacements().set(playerNumber, false);
    }

    public void spawnPlayer() {
        this.state = BridgerPlayerState.SPAWN;
        this.player.teleport(this.spawn);
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.getInventory().clear();

        this.giveItems();
    }

    private void giveItems() {
        final PlayerInventory inventory = this.player.getInventory();

        inventory.clear();
        inventory.addItem(new ItemStack(this.getActualBlock().getMaterial(), 64 * 9, this.getActualBlock().getMeta()));
        inventory.setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());

        IHyrame.get().getItemManager().giveItem(this.player, 8, TeleportLeaderboardsItem.class);
    }

    public void resetPlayerBridge() {
        if (this.isBridging()) {
            this.endBridging(false);
            return;
        }

        this.spawnPlayer();
        new ActionBar(ChatColor.RED + BridgerMessage.MESSAGE_PLAYER_FAILED_BRIDGE.asString(player).replace("%block%", "0")).send(this.player);
    }

    public void teleportToLeaderboards() {
        if (this.isBridging()) {
            this.endBridging(false);
        }

        this.state = BridgerPlayerState.LEADERBOARDS;
        this.player.teleport(HyriBridger.get().getConfiguration().getLeaderboardIsland().asBukkit());

        IHyrame.get().getItemManager().giveItem(this.player, 8, LeaveLeaderboardsItem.class);
    }

    public void startBridging() {
        this.state = BridgerPlayerState.BRIDGING;
        this.bridgeTask = new BridgeTask(this);
        this.bridgeTask.start();
    }

    public void endBridging(boolean success) {
        this.bridgeTask.stop();

        if (success && this.placedBlocks.size() > 20 && this.timer.getActualTime() > 1700) {
            // Update leaderboard
            final BridgerLeaderboard leaderboard = this.plugin.getLeaderboardHandler().getLeaderboard(this.game.getType());
            if (!leaderboard.hasTime(this.uniqueId)) {
                leaderboard.addTime(this.uniqueId, this.timer.toFinalDuration());
            }

            if (statisticsData.getPersonalBest() == null || statisticsData.getPersonalBest().getExactTime() == 0 || new BridgerDuration(this.timer.getActualTime()).getExactTime() < statisticsData.getPersonalBest().getExactTime()) {
                this.successPersonalBest();
                leaderboard.addTime(this.uniqueId, this.timer.toFinalDuration());
            } else {
                this.failPersonalBest();
            }

            game.getSession().add(this.player, this.timer.toFinalDuration());

            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(this.player.getUniqueId());

            account.getHyris().add(5).exec();
            account.getNetworkLeveling().addExperience(10);
            account.update();

            statisticsData.addBridgesMade(1);
        } else {
            new ActionBar(ChatColor.RED + BridgerMessage.MESSAGE_PLAYER_FAILED_BRIDGE.asString(player)
                    .replace("%block%", String.valueOf(this.placedBlocks.size()-1))).send(this.player);
            statisticsData.addBridgeFailed(1);
        }

        statisticsData.addBlocksPlaced(placedBlocks.size());

        this.deletePlacedBlocks();
        this.refreshHologram();

        this.spawnPlayer();
    }

    private void successPersonalBest() {
        statistics.update(player.getUniqueId());
        if (this.getMedal() == null || !this.getMedal().equals(BridgerMedal.ULTIMATE)) {
            for (BridgerMedal bridgerMedal : BridgerMedal.values()) {
                if (this.timer.toFinalDuration().getExactTime() < bridgerMedal.getTimeToReach(game.getType())) {
                    this.successMedal(bridgerMedal);
                }
            }
        }

        // Update statistics
        this.statisticsData.setPersonalBest(this.timer.toFinalDuration());

        this.player.playSound(this.spawn, Sound.LEVEL_UP, 10, 1);

        Title.sendTitle(this.player, new Title(
                ChatColor.AQUA + this.timer.toFinalDuration().toFormattedTime(),
                ChatColor.DARK_AQUA + BridgerMessage.TITLE_SUB_PLAYER_PB.asString(player).replace("%pb%", ChatColor.AQUA + this.timer.toFinalDuration().toFormattedTime()),
                5, 40, 15)
        );
        this.plugin.getMessageHelper().sendSuccessPBMessage(this.player, this.timer.toFinalDuration());
    }

    private void successMedal(BridgerMedal bridgerMedal) {
        this.statisticsData.setHighestAcquiredMedal(bridgerMedal);
    }

    private void failPersonalBest() {
        this.player.playSound(this.spawn, Sound.ORB_PICKUP, 10, 1);
        Title.sendTitle(this.player, ChatColor.AQUA + this.timer.toFinalDuration().toFormattedTime(), "", 5, 20, 5);
        this.plugin.getMessageHelper().sendFailedPBMessage(this.player, this.getPersonalBest(), this.timer.toFinalDuration());
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
                .withLine(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + BridgerMessage.HOLOGRAM_STATS.asString(player))
                .withLine(ChatColor.AQUA + BridgerMessage.SCOREBOARD_MEDAL_ACTUAL.asString(player) + (this.getMedal() != null ? this.getMedal().getMessageValue().asString(player) : ChatColor.RED + "✘"))
                .withLine(ChatColor.AQUA + BridgerMessage.HOLOGRAM_PLACED_BLOCKS.asString(player) + ChatColor.YELLOW + this.statisticsData.getBlocksPlaced())
                .withLine(ChatColor.AQUA + BridgerMessage.HOLOGRAM_MADE_BRIDGES.asString(player) + ChatColor.YELLOW + this.statisticsData.getBridgesMade())
                .withLine(ChatColor.AQUA + BridgerMessage.HOLOGRAM_FAILED_BRIDGED.asString(player) + ChatColor.YELLOW + this.statisticsData.getBridgeFailed())
                .withLine(ChatColor.AQUA + BridgerMessage.HOLOGRAM_PLAYED_TIME.asString(player) + ChatColor.YELLOW + new BridgerPlayedDuration(Duration.ofMillis(asHyriPlayer().getStatistics().getPlayTime(HyriAPI.get().getServer().getType()) + this.getPlayTime()), this.player).toFormattedTime())
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
        npcHolo.add(BridgerMessage.NPC_NAME.asString(player));
        npcHolo.add(BridgerMessage.NPC_LORE.asString(player));

        this.npc = NPCManager.createNPC(this.npcLocation, BridgerGame.NPC_SKIN, npcHolo);

        this.npc.addPlayer(this.player)
                .setTrackingPlayer(true)
                .setShowingToAll(true)
                .setInteractCallback((rightClick, clicker) -> {
                    if (game.getPlayer(clicker) == null) return;
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

    public void initBlocks() {
        final List<IHyriTransaction> transactions = this.asHyriPlayer().getTransactions().getAll("bridgerBlocks");
        if (transactions != null) {
            for (IHyriTransaction transaction : transactions)
                this.data.addUnlockedBlock(transaction.loadContent(new BridgerBlockTransaction()).getBlock());
        }
        for (BridgerMedal medal : BridgerMedal.getMedalsBefore(statisticsData.getHighestAcquiredMedal()))
            this.data.addUnlockedBlock(medal.getRewardBlock());
        Stream.of(BridgerBlock.values())
                .filter(block ->
                        // TODO: to be added in the prod version (removed for testing)
                        // asHyriPlayer().getRank().isStaff() ||
                        block.getCost() == 0 ||
                        block.getSpecificationNeeded().getOptionalRankType()
                                .filter(rankType -> rankType.getPriority() >= asHyriPlayer().getRank().getPriority())
                                .isPresent() ||
                        block.getSpecificationNeeded().getOptionalMedal()
                                .filter(medal -> BridgerMedal.getMedalsBefore(medal).contains(statisticsData.getHighestAcquiredMedal()))
                                .isPresent()
                ).forEach(this.data::addUnlockedBlock);
    }

    private Location calculateLocation(Location location) {
        BridgerConfig config = this.plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        Location forFirstIsland = location.clone();
        return forFirstIsland.add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber);
    }


    private Area calculateArea(Area area) {
        BridgerConfig config = this.plugin.getConfiguration();
        Location diff = config.getDiffBetweenIslands().asBukkit();
        return new Area(
                area.getMax().clone().add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber),
                area.getMin().clone().add(diff.getX() * playerNumber, diff.getY() * playerNumber, diff.getZ() * playerNumber)
        );
    }

    public void joinSpectators() {
        this.joinSpectators(null);
    }

    public void joinSpectators(BridgerGamePlayer target) {
        if (isBridging()) {
            this.endBridging(false);
        }
        PlayerUtil.resetPlayer(this.player, true);
        PlayerUtil.addSpectatorAbilities(this.player);

        this.hide();
        this.player.spigot().setCollidesWithEntities(false);

        HyriGameItems.SPECTATOR_TELEPORTER.give(this.plugin.getHyrame(), this.player, 0);
        HyriGameItems.SPECTATOR_SETTINGS.give(this.plugin.getHyrame(), this.player, 1);
        this.plugin.getHyrame().getItemManager().giveItem(player, 8, "leave_spectator_item");

        this.state = BridgerPlayerState.SPECTATING;
        if (target != null) {
            this.player.teleport(target.player.getLocation());
        }
    }

    public void quitSpectators() {
        PlayerUtil.resetPlayer(player, true);
        this.show();
        player.spigot().setCollidesWithEntities(true);
        this.state = BridgerPlayerState.SPAWN;
        this.onJoin();
    }

    @SuppressWarnings("deprecation")
    private void deletePlacedBlocks() {
        final Set<Chunk> chunkToReload = new HashSet<>();
        for (Location placedBlock : placedBlocks) {
            chunkToReload.add(placedBlock.getChunk());
            placedBlock.getBlock().setType(Material.AIR);
        }
        placedBlocks.clear();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Chunk chunk : chunkToReload) {
                chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
            }
        }, 5L);
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        if (((CraftPlayer) this.player).getHandle().playerConnection != null) {
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld)loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            packet.block = CraftMagicNumbers.getBlock(material).fromLegacyData(data);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public boolean isBridging() {
        return state == BridgerPlayerState.BRIDGING;
    }

    public boolean isSpectating() {
        return state == BridgerPlayerState.SPECTATING;
    }

    public boolean isInLeaderboards()  {
        return this.state == BridgerPlayerState.LEADERBOARDS;
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
        this.game.getEmplacements().set(playerNumber, true);
    }

    public BridgerBlock getActualBlock() {
        return this.data.getSelectedBlock();
    }

    public void setActualBlock(BridgerBlock actualBlock) {
        this.data.setSelectedBlock(actualBlock, player);
        this.giveItems();
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    public BridgerData getData() {
        return data;
    }

    public void setData(BridgerData data) {
        this.data = data;
    }

    public BridgerMedal getMedal() {
        return this.statisticsData.getHighestAcquiredMedal();
    }

    public BridgerDuration getPersonalBest() {
        return this.statisticsData.getPersonalBest();
    }

    public Area getGameArea() {
        return gameArea;
    }

    public BridgerTimer getTimer() {
        return timer;
    }

    public void setTimer(BridgerTimer timer) {
        this.timer = timer;
    }

    public Location getSpawn() {
        return spawn;
    }
}