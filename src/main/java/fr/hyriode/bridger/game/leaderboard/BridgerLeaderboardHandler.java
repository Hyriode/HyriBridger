package fr.hyriode.bridger.game.leaderboard;

import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.BridgerGameType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AstFaster
 * on 16/04/2023 at 21:21
 */
public class BridgerLeaderboardHandler {

    private final Map<BridgerGameType, BridgerLeaderboard> leaderboards = new HashMap<>();

    public BridgerLeaderboardHandler() {
        this.registerLeaderboard(BridgerGameType.SHORT);
        this.registerLeaderboard(BridgerGameType.NORMAL);
        this.registerLeaderboard(BridgerGameType.DIAGONAL);
    }

    private void registerLeaderboard(BridgerGameType type) {
        final BridgerLeaderboard leaderboard = new BridgerLeaderboard(type);

        this.leaderboards.put(type, leaderboard);

        leaderboard.setup();
    }

    public BridgerLeaderboard getLeaderboard(BridgerGameType type) {
        return this.leaderboards.get(type);
    }

    public Map<BridgerGameType, BridgerLeaderboard> getLeaderboards() {
        return this.leaderboards;
    }

}
