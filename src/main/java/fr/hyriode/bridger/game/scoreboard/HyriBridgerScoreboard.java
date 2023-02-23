package fr.hyriode.bridger.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerMedal;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HyriBridgerScoreboard extends HyriGameScoreboard<BridgerGame> {

    private final HyriBridger plugin;
    private final Player player;
    private final BridgerGamePlayer gamePlayer;

    public HyriBridgerScoreboard(HyriBridger plugin, Player player) {
        super(plugin, plugin.getGame(), player, plugin.getGame().getName());
        this.plugin = plugin;
        this.player = player;
        this.gamePlayer = this.plugin.getGame().getPlayer(player);
        addLines();
    }

    public void addLines() {
        setLine(0, getDateLine(), line -> line.setValue(getDateLine()), 20 * 60);
        addBlankLine(1);
        setLine(2, ChatColor.AQUA + "" + ChatColor.BOLD + getValue("scoreboard.best-time"));
        setLine(3, getBestTime(), line -> line.setValue(getBestTime()), 20);
        setLine(4, getActualTime(), line -> line.setValue(getActualTime()), 1);
        addBlankLine(5);
        setLine(6, ChatColor.DARK_AQUA + getValue("scoreboard.top-3"));
        setLine(7, getBestTimes(1), line -> line.setValue(getBestTimes(1)), 20);
        setLine(8, getBestTimes(2), line -> line.setValue(getBestTimes(2)), 20);
        setLine(9, getBestTimes(3), line -> line.setValue(getBestTimes(3)), 20);
        addBlankLine(10);
        setLine(11, getMedalLine(), line -> line.setValue(getMedalLine()), 20);
        addBlankLine(12);
        addHostnameLine();
    }

    private String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private String getBestTime() {
        return (this.gamePlayer.getPB() != null) ? ChatColor.YELLOW + this.gamePlayer.getPB().toFormattedTime() : ChatColor.GRAY + "-.---";
    }

    private String getActualTime() {
        String startString = ChatColor.WHITE + getValue("scoreboard.actual-time") + " " + ChatColor.YELLOW;
        return (this.gamePlayer.isBridging() && this.gamePlayer.getActualTimer() != null && this.gamePlayer.getActualTimer().getFormattedActualTime() != null) ? startString + this.gamePlayer.getActualTimer().getFormattedActualTime() : startString + "0.000";
    }

    private String getBestTimes(int i) {
        return plugin.getGame().getSession().getFormattedTop(i);
    }

    private String getMedalLine() {
        final BridgerMedal bridgerMedal = this.gamePlayer.getMedal();
        return ChatColor.GOLD + getValue("scoreboard.medal.actual") + ((bridgerMedal != null) ? getValue(bridgerMedal.getLanguageValue()) : ChatColor.RED + "✘");
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(player);
    }
}
