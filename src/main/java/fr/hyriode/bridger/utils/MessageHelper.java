package fr.hyriode.bridger.utils;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import org.bukkit.entity.Player;

import static fr.hyriode.hyrame.utils.Symbols.HYPHENS_LINE;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class MessageHelper {

    public void sendSuccessPBMessage(Player player, HyriBridgerDuration pbTime) {
        sendHeader(player);
        player.sendMessage(MessageUtil.getCentredMultiLinesMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.succeed-pb").getValue(player)
               .replace("%pb%", pbTime.toFormattedTime())));
        sendFooter(player);
    }

    public void sendFailedPBMessage(Player player, HyriBridgerDuration pbTime, HyriBridgerDuration actualTime) {
        sendHeader(player);
        player.sendMessage(MessageUtil.getCentredMultiLinesMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.failed-pb").getValue(player)
                .replace("%pb%", pbTime.toFormattedTime())
                .replace("%time%", actualTime.toFormattedTime())));
        sendFooter(player);
    }

    public void sendHeader(Player player) {
        player.sendMessage(AQUA + "" + STRIKETHROUGH + HYPHENS_LINE);
    }

    public void sendFooter(Player player) {
        player.sendMessage(MessageUtil.getCentredMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.rewards.hyris").getValue(player)));
        player.sendMessage(MessageUtil.getCentredMessage(HyriAPI.get().getLanguageManager().getMessage("message.player.rewards.xp").getValue(player)));
        player.sendMessage(AQUA + "" + STRIKETHROUGH + HYPHENS_LINE);
    }
}
