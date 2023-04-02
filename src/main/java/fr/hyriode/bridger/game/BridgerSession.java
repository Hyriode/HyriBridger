package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BridgerSession {

    private final Map<Player, BridgerDuration> scores = new HashMap<>();

    public void add(Player player, BridgerDuration duration) {
        if (scores.containsKey(player)) {
            final BridgerDuration currentDuration = scores.get(player);
            if (currentDuration.compareTo(duration) < 0) {
                return;
            }
        }
        scores.put(player, duration);
    }

    public void removeScoresOf(Player player) {
        scores.remove(player);
    }

    public String getFormattedTop(int i) {
        if (i < 1 || i > scores.size()) {
            return ChatColor.GRAY + "*****:" + ChatColor.YELLOW + " -.---";
        }
        final Player player = scores.keySet().stream().sorted(Comparator.comparing(scores::get)).collect(Collectors.toList()).get(i - 1);
        return ChatColor.GRAY + player.getDisplayName() + ": " + ChatColor.YELLOW + scores.get(player).toFormattedTime();
    }
}