package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.game.timers.BridgerScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BridgerSession {

    private final List<BridgerScore> scores = new ArrayList<>();

    public void add(Player player, HyriBridgerDuration duration) {
        if (scores.stream().anyMatch(bridgerScore -> bridgerScore.getPlayer() == player)) {
            scores.remove(scores.stream().filter(bridgerScore -> bridgerScore.getPlayer() == player).findFirst().orElse(null));
        } else {
            scores.add(new BridgerScore(duration, player));
        }
        scores.sort(Comparator.comparing(bridgerScore -> bridgerScore.getDuration().getExactTime()));
        if (scores.size() > 3) scores.remove(3);
    }

    public void removeScoresOf(Player player) {
        scores.removeIf(score -> score.getPlayer().equals(player));
    }

    public String getFormattedTop(int i) {
        if (i < 1 || i > 3) {
            return ChatColor.GRAY + "*****:" + ChatColor.YELLOW + " -.---";
        }
        if (scores.size() < i) {
            return ChatColor.GRAY + "*****:" + ChatColor.YELLOW + " -.---";
        }
        BridgerScore score = scores.get(i - 1);
        return ChatColor.GRAY + score.getPlayer().getDisplayName() + ": " + ChatColor.YELLOW + score.getDuration().toFormattedTime();
    }

    public List<BridgerScore> getScores() {
        return scores;
    }

    public List<String> getFormattedTopScores() {
        return scores.stream()
                .map(score -> ChatColor.GRAY + score.getPlayer().getDisplayName() + ": " + ChatColor.YELLOW + score.getDuration().toFormattedTime())
                .collect(Collectors.toList());
    }
}