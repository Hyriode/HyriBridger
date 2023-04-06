package fr.hyriode.bridger.game.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerMedal;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        this.addCurrentDateLine(0);
        addBlankLine(1);
        setLine(2, ChatColor.AQUA + "" + ChatColor.BOLD + BridgerMessage.SCOREBOARD_BEST_TIME.asList(player));
        setLine(3, getBestTime(), line -> line.setValue(getBestTime()), 20);
        setLine(4, getActualTime(), line -> line.setValue(getActualTime()), 1);
        addBlankLine(5);
        setLine(6, ChatColor.DARK_AQUA + BridgerMessage.SCOREBOARD_TOP_3.asString(player));
        setLine(7, getBestTimes(1), line -> line.setValue(ChatColor.AQUA + getBestTimes(1)), 20);
        setLine(8, getBestTimes(2), line -> line.setValue(ChatColor.GOLD + getBestTimes(2)), 20);
        setLine(9, getBestTimes(3), line -> line.setValue(ChatColor.GRAY + getBestTimes(3)), 20);
        addBlankLine(10);
        setLine(11, getMedalLine(), line -> line.setValue(getMedalLine()), 20);
        addBlankLine(12);
        addHostnameLine();
    }

    private String getBestTime() {
        return (this.gamePlayer.getPersonalBest() != null) ? ChatColor.YELLOW + this.gamePlayer.getPersonalBest().toFormattedTime() : ChatColor.GRAY + "-.---";
    }

    private String getActualTime() {
        String startString = ChatColor.WHITE + BridgerMessage.SCOREBOARD_ACTUAL_TIME.asString(player) + " " + ChatColor.YELLOW;
        return (this.gamePlayer.isBridging() && this.gamePlayer.getTimer() != null && this.gamePlayer.getTimer().getFormattedActualTime() != null) ?
                startString + this.gamePlayer.getTimer().getFormattedActualTime() : startString + "0.000";
    }

    private String getBestTimes(int i) {
        return plugin.getGame().getSession().getFormattedTop(i);
    }

    private String getMedalLine() {
        final BridgerMedal bridgerMedal = this.gamePlayer.getMedal();
        return ChatColor.GOLD + BridgerMessage.SCOREBOARD_MEDAL_ACTUAL.asString(player) + ((bridgerMedal != null) ? bridgerMedal.getMessageValue().asString(player) : ChatColor.RED + "✘");
    }
}
