package fr.hyriode.bridger.game.timers;

import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.utils.DurationConverter;
import org.bukkit.entity.Player;

import java.time.Duration;

import static fr.hyriode.bridger.language.BridgerMessage.*;

public class BridgerPlayedDuration extends DurationConverter {
    private final Player player;

    public BridgerPlayedDuration(Duration duration, Player player) {
        super(duration);
        this.player = player;
    }

    public String toFormattedTime() {
        return this.toDaysPart() + " " + this.getValue(UTILS_DAYS) + " " + this.toHoursPart() + " " + this.getValue(UTILS_HOURS) + " " + this.toMinutesPart() + " " + this.getValue(UTILS_MINUTES);
    }

    private String getValue(BridgerMessage message) {
        return message.asString(this.player);
    }
}
