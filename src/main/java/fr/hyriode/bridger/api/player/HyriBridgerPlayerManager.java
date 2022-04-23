package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.api.HyriBridgerAPI;

import java.util.UUID;
import java.util.function.Function;

public class HyriBridgerPlayerManager {

    public HyriBridgerPlayerManager() {
    }

    public HyriBridgerPlayer getPlayer(UUID uuid) {
        IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        return hyriPlayer.getData("therunner", HyriBridgerPlayer.class);
    }

    public void sendPlayer(HyriBridgerPlayer player) {
        final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

        hyriPlayer.addData("therunner", player);
        HyriAPI.get().getPlayerManager().sendPlayer(hyriPlayer);
    }

}
