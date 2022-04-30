package fr.hyriode.bridger.api;

import fr.hyriode.bridger.api.player.HyriBridgerPlayerManager;

public class HyriBridgerAPI {

    private final HyriBridgerPlayerManager playerManager;

    public HyriBridgerAPI() {
        this.playerManager = new HyriBridgerPlayerManager();
    }

    public HyriBridgerPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
