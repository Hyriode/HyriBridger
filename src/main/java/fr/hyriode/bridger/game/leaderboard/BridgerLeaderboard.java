package fr.hyriode.bridger.game.leaderboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leaderboard.IHyriLeaderboard;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerDuration;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.leaderboard.HyriLeaderboardDisplay;
import fr.hyriode.hyrame.utils.LocationWrapper;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 16/04/2023 at 21:20
 */
public class BridgerLeaderboard {

    private boolean setup;
    private HyriLeaderboardDisplay display;

    private final BridgerGameType type;
    private final IHyriLeaderboard handle;
    private final LocationWrapper location;

    public BridgerLeaderboard(BridgerGameType type) {
        this.type = type;
        this.handle = HyriAPI.get().getLeaderboardProvider().getLeaderboard("bridger", this.type.name().toLowerCase());
        this.location = HyriBridger.get().getConfiguration().getLeaderboard(this.type);
    }

    public void setup() {
        if (this.setup) {
            throw new IllegalStateException("Leaderboard is already setup!");
        }

        this.display = new HyriLeaderboardDisplay.Builder(HyriBridger.get(), this.handle.getType(), this.handle.getName(), this.location.asBukkit())
                .withHeader(player -> BridgerMessage.LEADERBOARD_HEADER.asString(player).replace("%type%", this.type.getDisplayName()))
                .withUpdateTime(20L * 60L)
                .withScoreFormatter((target, score) -> new BridgerDuration((long) -score).toFormattedTime())
                .build();

        this.display.show();

        this.setup = true;
    }

    public void destroy() {
        if (!this.setup) {
            throw new IllegalStateException("Leaderboard is not setup!");
        }

        this.display.hide();
        this.display = null;

        this.setup = false;
    }

    public void addTime(UUID playerId, BridgerDuration time) {
        this.handle.setScore(HyriLeaderboardScope.TOTAL, playerId, -time.getExactTime());

        if (this.setup && this.handle.getPosition(HyriLeaderboardScope.TOTAL, playerId) < 10) {
            this.display.update();
        }
    }

    public boolean isSuperior(UUID playerId, BridgerDuration time) {
        return this.handle.getScore(HyriLeaderboardScope.TOTAL, playerId) < -time.getExactTime();
    }

    public BridgerGameType getType() {
        return this.type;
    }

    public boolean hasTime(UUID playerId) {
        return this.handle.getScore(HyriLeaderboardScope.TOTAL, playerId) > 0;
    }

}
