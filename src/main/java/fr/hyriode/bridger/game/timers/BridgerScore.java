package fr.hyriode.bridger.game.timers;

import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import org.bukkit.entity.Player;

public class BridgerScore {

    private HyriBridgerDuration duration;
    private final Player player;

    public BridgerScore(HyriBridgerDuration duration, Player player) {
        this.duration = duration;
        this.player = player;
    }

    public HyriBridgerDuration getDuration() {
        return duration;
    }

    public Player getPlayer() {
        return player;
    }

    public void setDuration(HyriBridgerDuration duration) {
        this.duration = duration;
    }
}
