package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.Medal;
import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.api.player.HyriBridgerStats;
import org.bukkit.entity.Player;

public interface IBridgerTypeHandler {

    long getMedalTime(Medal medal);

    class NormalHandler implements IBridgerTypeHandler {
        @Override
        public long getMedalTime(Medal medal) {
            return medal.getTimeToReachNormal();
        }
    }
    class ShortHandler implements IBridgerTypeHandler {
        @Override
        public long getMedalTime(Medal medal) {
            return medal.getTimeToReachShort();
        }
    }

    class DiagonalHandler implements IBridgerTypeHandler {
        @Override
        public long getMedalTime(Medal medal) {
            return medal.getTimeToReachDiagonal();
        }
    }
}