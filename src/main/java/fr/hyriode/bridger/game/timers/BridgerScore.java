package fr.hyriode.bridger.game.timers;

import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.entity.Player;

public class BridgerScore {

    private BridgerDuration duration;
    private final Player player;

    public BridgerScore(BridgerDuration duration, Player player) {
        this.duration = duration;
        this.player = player;
    }

    public BridgerDuration getDuration() {
        return duration;
    }

    public Player getPlayer() {
        return player;
    }

    public void setDuration(BridgerDuration duration) {
        this.duration = duration;
    }
}
