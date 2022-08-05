package fr.hyriode.bridger.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.BridgerGame;
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

        this.addLines();
    }

    public void addLines() {
        this.setLine(0, this.getDateLine(), line -> line.setValue(this.getDateLine()), 20*60);
        this.addBlankLine(1);
        this.setLine(2, ChatColor.AQUA + "" + ChatColor.BOLD + this.getValue("scoreboard.best-time"));
        this.setLine(3, this.getBestTime(), line -> line.setValue(this.getBestTime()), 20);
        this.setLine(4, this.getActualTime(), line -> line.setValue(this.getActualTime()), 1);
        this.addBlankLine(5);
        this.setLine(6, ChatColor.DARK_AQUA + this.getValue("scoreboard.top-3"));
        this.setLine(7, this.getBestTimes(1), line -> line.setValue(this.getBestTimes(1)), 20);
        this.setLine(8, this.getBestTimes(2), line -> line.setValue(this.getBestTimes(2)), 20);
        this.setLine(9, this.getBestTimes(3), line -> line.setValue(this.getBestTimes(3)), 20);
        this.addBlankLine(10);
        this.setLine(11, this.getMedalLine(), line -> line.setValue(this.getMedalLine()), 20);
        this.addBlankLine(12);
        this.addHostnameLine();
    }

    private String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    private String getBestTime() {
        if (this.gamePlayer.getPB() != null) {
            return ChatColor.YELLOW + this.gamePlayer.getPB().toFormattedTime();
        }
        return ChatColor.GRAY + "-.---";
    }

    private String getActualTime() {
        String startString = ChatColor.WHITE + this.getValue("scoreboard.actual-time") + " " + ChatColor.YELLOW;
        if (this.gamePlayer.isBridging()) {
            if (this.gamePlayer.getActualTimer() != null) {
                if (this.gamePlayer.getActualTimer().getFormattedActualTime() != null) {
                    return startString + this.gamePlayer.getActualTimer().getFormattedActualTime();
                }
            }
        }
        return startString + "0.000";
    }

    private String getBestTimes(int i) {
        return this.plugin.getGame().getSession().getFormattedTop(i);
    }

    private String getMedalLine() {
        final Medal medal = this.gamePlayer.getMedal();
        if (medal != null) {
            return ChatColor.GOLD + this.getValue("scoreboard.medal.actual") + this.getValue(medal.getLanguageValue());
        } else {
            return ChatColor.GOLD + this.getValue("scoreboard.medal.actual") + ChatColor.RED + "✘";
        }
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(this.player);
    }

}
