package fr.hyriode.bridger.api.player;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;

import java.util.UUID;

public class HyriBridgerData extends HyriPlayerData {

    private String actualMaterialName;

    public String getActualMaterialName() {
        return actualMaterialName;
    }

    public void setActualMaterialName(String actualMaterialName) {
        this.actualMaterialName = actualMaterialName;
    }

    public void update(IHyriPlayer account) {
        account.addData("bridger", this);
        account.update();
    }

    public void update(UUID player) {
        this.update(HyriAPI.get().getPlayerManager().getPlayer(player));
    }

    public static HyriBridgerData get(IHyriPlayer account) {
        HyriBridgerData data = account.getData("bridger", HyriBridgerData.class);

        if (data == null) {
            data = new HyriBridgerData();
            data.update(account);
        }

        return data;
    }

    public static HyriBridgerData get(UUID playerId) {
        return get(HyriAPI.get().getPlayerManager().getPlayer(playerId));
    }

}
