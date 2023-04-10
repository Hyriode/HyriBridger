package fr.hyriode.bridger.language;

import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.entity.Player;

import static fr.hyriode.hyrame.utils.Symbols.HYPHENS_LINE;
import static org.bukkit.ChatColor.*;

public class MessageHelper {

    public void sendSuccessPBMessage(Player player, BridgerDuration pbTime) {
        player.sendMessage(
                getLine()  + "\n§r" +
                MessageUtil.getCentredMultiLinesMessage(BridgerMessage.MESSAGE_PLAYER_SUCCEED_PB.asString(player).replace("%pb%", pbTime.toFormattedTime())) + "\n" +
                getFooter(player));
    }

    public void sendFailedPBMessage(Player player, BridgerDuration pbTime, BridgerDuration actualTime) {
        player.sendMessage(getLine() + MessageUtil.getCentredMultiLinesMessage(BridgerMessage.MESSAGE_PLAYER_FAILED_PB.asString(player)
                .replace("%pb%", pbTime.toFormattedTime())
                .replace("%time%", actualTime.toFormattedTime())) + "\n" + getFooter(player));
    }

    public String getLine() {
        return DARK_AQUA + "" + STRIKETHROUGH + HYPHENS_LINE;
    }

    public String getFooter(Player player) {
        return MessageUtil.getCentredMessage(BridgerMessage.MESSAGE_PLAYER_REWARDS_HYRIS.asString(player))  +
                MessageUtil.getCentredMessage(BridgerMessage.MESSAGE_PLAYER_REWARDS_XP.asString(player) + "\n") +
                getLine();
    }
}
