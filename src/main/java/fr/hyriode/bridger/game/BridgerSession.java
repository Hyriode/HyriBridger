package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BridgerSession {

    private final Map<Player, BridgerDuration> scores = new HashMap<>();

    public void add(Player player, BridgerDuration duration) {
        scores.put(player, duration);
    }

    public void removeScoresOf(Player player) {
        scores.remove(player);
    }

    public String getFormattedTop(int i) {
        List<Player> players = scores.keySet().stream().sorted(Comparator.comparing(scores::get)).collect(Collectors.toList());
        if (i < 1 || i > 3 || players.size() < i) {
            return ChatColor.GRAY + "*****:" + ChatColor.YELLOW + " -.---";
        }
        final Player player = players.get(i - 1);
        final BridgerDuration duration = scores.get(player);
        return ChatColor.GRAY + player.getDisplayName() + ": " + ChatColor.YELLOW + duration.toFormattedTime();
    }
}