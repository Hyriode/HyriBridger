package fr.hyriode.bridger.game.timers;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.DurationConverter;
import org.bukkit.entity.Player;

import java.time.Duration;

public class BridgerPlayedDuration extends DurationConverter {
    private final Player player;

    public BridgerPlayedDuration(Duration duration, Player player) {
        super(duration);
        this.player = player;
    }

    public String toFormattedTime() {
        return this.toDaysPart() + " " + this.getValue("utils.days") + " " + this.toHoursPart() + " " + this.getValue("utils.hours") + " " + this.toMinutesPart() + " " + this.getValue("utils.minutes");
    }

    private String getValue(String key) {
        return HyriLanguageMessage.get(key).getValue(this.player);
    }
}
