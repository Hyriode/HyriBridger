package fr.hyriode.bridger.game;

import fr.hyriode.hyrame.game.HyriGameType;

/**
 * Project: HyriBridger
 * Created by Akkashi
 * on 23/04/2022 at 14:25
 */
public enum BridgerGameType implements HyriGameType {

    SHORT("Short", 1, 30),
    LONG("Long", 1, 30),
    DIAGONAL("Diagonal", 1, 30),
    ;

    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;

    BridgerGameType(String displayName, int minPlayers, int maxPlayers) {
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}
