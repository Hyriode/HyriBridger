package fr.hyriode.bridger.utils;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static fr.hyriode.hyrame.utils.Symbols.HYPHENS_LINE;
import static org.bukkit.ChatColor.*;

public class MessageHelper {

    public void sendSuccessPBMessage(Player player, BridgerDuration pbTime) {
        sendHeader(player);
        player.sendMessage(MessageUtil.getCentredMultiLinesMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.succeed-pb").getValue(player)
               .replace("%pb%", pbTime.toFormattedTime())));
        sendFooter(player);
    }

    public void sendFailedPBMessage(Player player, BridgerDuration pbTime, BridgerDuration actualTime) {
        sendHeader(player);
        player.sendMessage(MessageUtil.getCentredMultiLinesMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.failed-pb").getValue(player)
                .replace("%pb%", pbTime.toFormattedTime())
                .replace("%time%", actualTime.toFormattedTime())));
        sendFooter(player);
    }

    public void sendHeader(Player player) {
        player.sendMessage(DARK_AQUA + "" + STRIKETHROUGH + HYPHENS_LINE);
    }

    public void sendFooter(Player player) {
        player.sendMessage(
                "\n" +
                MessageUtil.getCentredMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.rewards.hyris").getValue(player)) + "\n" +
                MessageUtil.getCentredMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.rewards.xp").getValue(player)) + "\n" +
                DARK_AQUA + "" + STRIKETHROUGH + HYPHENS_LINE
        );
    }
}
