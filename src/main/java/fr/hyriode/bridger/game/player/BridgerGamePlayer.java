package fr.hyriode.bridger.game.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriTransaction;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.*;
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
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;
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
import java.util.List;
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

        this.giveBlocks();
    }

    private void giveBlocks() {
        final PlayerInventory inventory = this.player.getInventory();
        inventory.clear();
        inventory.addItem(new ItemStack(this.getActualBlock().getMaterial(), 64 * 9, this.getActualBlock().getMeta()));
        inventory.setItem(3, new ItemBuilder(Material.IRON_PICKAXE)
                .withEnchant(Enchantment.DIG_SPEED, 2)
                .unbreakable()
                .build());
    }

    public void resetPlayerBridge() {
        if (this.isBridging()) {
            this.endBridging(false);
            return;
        }

        this.spawnPlayer();
        new ActionBar(ChatColor.RED + this.getTranslatedMessage("message.player.failed-bridge")
                .replace("%block%", "0")).send(this.player);
    }

    public void startBridging() {
        this.state = BridgerPlayerState.BRIDGING;
        this.bridgeTask = new BridgeTask(this);
        this.bridgeTask.start();
    }

    public void endBridging(boolean success) {
        this.bridgeTask.stop();

        if (success && this.placedBlocks.size() > 20 && this.timer.getActualTime() > 3700) {
            if (statisticsData.getPersonalBest() == null || timer.getActualTime() > statisticsData.getPersonalBest().getExactTime()) {
                this.successPersonalBest();
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
            new ActionBar(ChatColor.RED + this.getTranslatedMessage("message.player.failed-bridge")
                    .replace("%block%", String.valueOf(this.placedBlocks.size()-1))).send(this.player);
            statisticsData.addBridgeFailed(1);
        }

        statisticsData.addBlocksPlaced(placedBlocks.size());

        this.deletePlacedBlocks();
        this.refreshHologram();

        this.spawnPlayer();
    }

    private void successPersonalBest() {
        if (this.getMedal() == null || !this.getMedal().equals(BridgerMedal.ULTIMATE)) {
            for (BridgerMedal bridgerMedal : BridgerMedal.values()) {
                if (this.timer.toFinalDuration().getExactTime() < bridgerMedal.getTimeToReach(game.getType())) {
                    this.successMedal(bridgerMedal);
                }
            }
        }

        this.statisticsData.setPersonalBest(this.timer.toFinalDuration());

        this.player.playSound(this.spawn, Sound.LEVEL_UP, 10, 1);

        Title.sendTitle(this.player, new Title(ChatColor.AQUA + this.timer.toFinalDuration().toFormattedTime(), ChatColor.DARK_AQUA + this.getTranslatedMessage("title.sub.player.pb")
                .replace("%pb%", ChatColor.AQUA + this.timer.toFinalDuration().toFormattedTime())
                , 5, 40, 15));
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
                .withLine(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + this.getTranslatedMessage("hologram.stats"))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("scoreboard.medal.actual") + (this.getMedal() != null ? this.getTranslatedMessage(this.getMedal().getLanguageValue()) : ChatColor.RED + "✘"))
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.placed-blocks") + ChatColor.YELLOW + this.statisticsData.getBlocksPlaced())
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.made-bridges") + ChatColor.YELLOW + this.statisticsData.getBridgesMade())
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.failed-bridged") + ChatColor.YELLOW + this.statisticsData.getBridgeFailed())
                .withLine(ChatColor.AQUA + this.getTranslatedMessage("hologram.played-time") + ChatColor.YELLOW + new BridgerPlayedDuration(Duration.ofMillis(asHyriPlayer().getStatistics().getPlayTime(HyriAPI.get().getServer().getType()) + this.getPlayTime()), this.player).toFormattedTime())
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
                .filter(block ->  block.getCost() <= 0 || block.getSpecificationNeeded().getOptionalRankType()
                        .filter(rankType -> rankType.getPriority() >= asHyriPlayer().getRank().getPriority())
                        .isPresent())
                .forEach(this.data::addUnlockedBlock);
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
        this.state = BridgerPlayerState.SPECTATING;
        this.setSpectator(true);
        if (target != null) {
            this.player.teleport(target.player.getLocation());
        }
    }

    public void quitSpectators() {
        this.state = BridgerPlayerState.SPAWN;
        this.setSpectator(false);
        this.onJoin();
    }

    private void deletePlacedBlocks() {
        //TODO animations
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
        this.game.getEmplacements().set(playerNumber, true);
    }

    public BridgerBlock getActualBlock() {
        return this.data.getSelectedBlock();
    }

    public void setActualBlock(BridgerBlock actualBlock) {
        this.data.setSelectedBlock(actualBlock);
        this.giveBlocks();
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
}