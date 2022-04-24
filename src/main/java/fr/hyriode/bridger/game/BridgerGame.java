package fr.hyriode.bridger.game;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.util.Skin;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.npc.NPCManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class BridgerGame extends HyriGame<BridgerGamePlayer> {

    private Bridger plugin;
    private final List<Boolean> emplacements;
    private final BridgerSession session;

    public static Skin NPC_SKIN = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY0NzYyNzQ0MzU4NiwKICAicHJvZmlsZUlkIiA6ICIxNDU1MDNhNDRjZmI0NzcwYmM3NWNjMTRjYjUwMDE4NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMaWtlbHlFcmljIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkOWRlOTE4ZmQyOTNlMjRlMGY0ZDI5MTNlZjI2YjE3Y2ZhY2UxZGNiMjdkOWU3MjVjNmRmYmQxYjQxNWVjNjEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                                          "ZJyWeWhCnA3AzDGNcg+OfTmgT7TxDFWWEBXyWcX2g7/CeQp6rmndUok5Seq0+iAcISVaUGUUMQjrLIv2ivR4YXltZyMKTirb7vPt5B7rLo7I8j6YWTc6jrJgAbf83e7q5fSF0WNZ4Bm3DG6edSRxwYZOFFaG8v+ZsFy21v0KL+OvAifEGYp6Or01pNfR/aNdhg7fFsk7difO8QIXghoAARiRGzcitDHPtgMCDQuphYkUSSiFlKFIg5sVMXC8a0E8x7wAWXan06/vdlZXnjZz/ZWehEl7wLstLZZb0mNl6k60VVt3lk5/5epMHhoiq3DaKFoxkGLBLx+qQmfT9Y0NsrEyjBdZDYQiOVSomC1c2BJ0f2aGEZCBaOHWzuJY9elB8o+rek4ovBc1E4W+EjwS0EYHggKCPmYhChUntN1TC4XQy64GEquZGAQLCzqKOPOeHdFg1oXAIpgI7R2orjfWkOb4MOtx/k+jriIb+oHjeE/q3tGYtwX9F8UdTWj3ZoyIoHfzd7uxcvGNUAH6X9gNPl0WpX21YmE3zdZnX75p1RRo2iwaUgxNTJdQrqWXN15TmwyvfuisJHZ3oijU8G9Dcaph+FcfJPfFTjmN5Skqb9IVMjmMSKHs7QANNWfhNNO5McwE+kvcIAr3HZsVXmLAAgEJTm9FqaCDU21mwfVCpfA=");

    public BridgerGame(IHyrame hyrame, Bridger plugin) {
        //dev:
        //super(hyrame, plugin, new BridgerGameInfo("bridger", "Bridger"), BridgerGamePlayer.class, BridgerGameType.SHORT);
        //prod:
        super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("bridger"), BridgerGamePlayer.class, HyriGameType.getFromData(BridgerGameType.values()));
        this.plugin = plugin;
        this.defaultStarting = false;
        this.setState(HyriGameState.READY);
        this.emplacements = new ArrayList<>();
        this.session = new BridgerSession();

        for (int i = 0; i < 30; i++) {
            this.emplacements.add(false);
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setCanPickupItems(false);
        player.teleport(this.plugin.getConfiguration().getSpawnLocationOnFirstIsland().asBukkit());

        final UUID uuid = player.getUniqueId();
        final BridgerGamePlayer gamePlayer;

        if(this.getPlayer(uuid) != null) {
             gamePlayer = this.getPlayer(uuid);
        }else {
            gamePlayer = new BridgerGamePlayer(this, player);
        }

        gamePlayer.init(this.plugin, this.getFirstEmplacementEmptyAndTakeIt());

        gamePlayer.setConnectionTime();
    }

    @Override
    public void handleLogout(Player player) {
        if(this.getPlayer(player).isBridging()) {
            this.getPlayer(player).endBridging(false);
        }
        NPCManager.removeNPC(this.getPlayer(player).getNPC());
        this.session.removeScoresOf(player);
        this.emplacements.set(this.getPlayer(player).getPlayerNumber(), false);
        this.getPlayer(player).sendPlayerStats();
        super.handleLogout(player);
    }

    public int getFirstEmplacementEmptyAndTakeIt() {
        for (int i = 0; i < emplacements.size()-1; i++) {
            if(!this.emplacements.get(i)) {
                this.emplacements.set(i, true);
                return i;
            }
        }
        throw new NotFoundException("Game is full");
    }

    public BridgerSession getSession() {
        return session;
    }

    public List<Boolean> getEmplacements() {
        return emplacements;
    }


}
