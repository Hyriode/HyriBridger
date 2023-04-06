package fr.hyriode.bridger.utils;

import fr.hyriode.bridger.api.BridgerDuration;
import fr.hyriode.bridger.language.BridgerMessage;
import org.bukkit.entity.Player;

import static fr.hyriode.hyrame.utils.Symbols.HYPHENS_LINE;
import static org.bukkit.ChatColor.*;

public class MessageHelper {

    public void sendSuccessPBMessage(Player player, BridgerDuration pbTime) {
        player.sendMessage(getHeader() + MessageUtil.getCentredMultiLinesMessage(BridgerMessage.MESSAGE_PLAYER_SUCCEED_PB.asString(player)
               .replace("%pb%", pbTime.toFormattedTime())) + "\n" + getFooter(player));
    }

    public void sendFailedPBMessage(Player player, BridgerDuration pbTime, BridgerDuration actualTime) {
        player.sendMessage(getHeader() + MessageUtil.getCentredMultiLinesMessage(BridgerMessage.MESSAGE_PLAYER_FAILED_PB.asString(player)
                .replace("%pb%", pbTime.toFormattedTime())
                .replace("%time%", actualTime.toFormattedTime())) + "\n" + getFooter(player));
    }

    public String getHeader() {
        return DARK_AQUA + "" + STRIKETHROUGH + HYPHENS_LINE + "\n";
    }

    public String getFooter(Player player) {
        return MessageUtil.getCentredMessage(BridgerMessage.MESSAGE_PLAYER_REWARDS_HYRIS.asString(player)) + "\n" +
                MessageUtil.getCentredMessage(BridgerMessage.MESSAGE_PLAYER_REWARDS_XP.asString(player)) + "\n" +
                DARK_AQUA + "" + STRIKETHROUGH + HYPHENS_LINE;
    }
}
