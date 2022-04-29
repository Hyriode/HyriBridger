package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;

import java.util.UUID;

public class HyriBridgerPlayerManager {

    public static String gameType;

    public HyriBridgerPlayerManager() {
    }

    public HyriBridgerPlayer getPlayer(UUID uuid) {
        IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        if(hyriPlayer.getStatistics("bridger", HyriBridgerPlayer.class) != null) {
            return hyriPlayer.getStatistics("bridger", HyriBridgerPlayer.class);
        }
        return new HyriBridgerPlayer(uuid);
    }

    public void sendPlayer(HyriBridgerPlayer player) {
        final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUUID());

        hyriPlayer.addStatistics("bridger", player);
        HyriAPI.get().getPlayerManager().sendPlayer(hyriPlayer);
    }

}
