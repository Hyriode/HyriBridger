package fr.hyriode.bridger.game.scoreboard;

import fr.hyriode.bridger.api.player.HyriBridgerPlayer;
import fr.hyriode.bridger.api.player.Medal;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

public class HyriBridgerScoreboard extends HyriGameScoreboard<BridgerGame> {

    private final Bridger plugin;
    private final Player player;
    private final BridgerGamePlayer gamePlayer;
    private final Supplier<HyriBridgerPlayer> accountSupplier;

    public HyriBridgerScoreboard(Bridger plugin, Player player) {
        super(plugin, plugin.getGame(), player, plugin.getGame().getName());
        this.plugin = plugin;
        this.player = player;
        this.gamePlayer = this.plugin.getGame().getPlayer(player);
        this.accountSupplier = () -> this.plugin.getApi().getPlayerManager().getPlayer(this.player.getUniqueId());

        this.addLines();
    }

    public void addLines() {
        this.setLine(0, this.getDateLine(), line -> line.setValue(this.getDateLine()), 20*60);
        this.addBlankLine(1);
        this.setLine(2, ChatColor.AQUA + "" + ChatColor.BOLD + this.getValue("scoreboard.best-time"));
        this.setLine(3, this.getBestTime(), line -> line.setValue(this.getBestTime()), 80);
        this.setLine(4, this.getActualTime(), line -> line.setValue(this.getActualTime()), 1);
        this.addBlankLine(5);
        this.setLine(6, ChatColor.DARK_AQUA + this.getValue("scoreboard.top-3"));
        this.setLine(7, this.getBestTimes(1), line -> line.setValue(this.getBestTimes(1)), 80);
        this.setLine(8, this.getBestTimes(2), line -> line.setValue(this.getBestTimes(2)), 80);
        this.setLine(9, this.getBestTimes(3), line -> line.setValue(this.getBestTimes(3)), 80);
        this.addBlankLine(10);
        this.setLine(11, this.getMedalLine(), line -> line.setValue(this.getMedalLine()), 80);
        this.addBlankLine(12);
        this.addHostnameLine();
    }

    private String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private String getBestTime() {
        if(this.accountSupplier.get().getStatistics().getPersonalBest() != null) {
            return ChatColor.YELLOW + this.accountSupplier.get().getStatistics().getPersonalBest().toFormattedTime();
        }else {
            return ChatColor.GRAY + "----";
        }
    }

    private String getActualTime() {
        String startString = ChatColor.WHITE + this.getValue("scoreboard.actual-time") + " " + ChatColor.YELLOW;
        if(this.gamePlayer.isBridging()) {
            return startString + this.gamePlayer.getActualTimer().getFormattedActualTime();
        }else {
            return startString + "0.000";
        }
    }

    private String getBestTimes(int i) {
        return this.plugin.getGame().getSession().getFormattedTop(i);
    }

    private String getMedalLine() {
        final Medal actualMedal = this.accountSupplier.get().getStatistics().getHighestAcquiredMedal();
        if(actualMedal != null) {
            return ChatColor.GOLD + this.getValue("scoreboard.medal.actual") + this.getValue(actualMedal.getLanguageValue());
        }else {
            return ChatColor.GOLD + this.getValue("scoreboard.medal.actual") + ChatColor.RED + "✘";
        }
    }

    private String getValue(String key) {
        return Bridger.getLanguageManager().getValue(this.player, key);
    }

}
