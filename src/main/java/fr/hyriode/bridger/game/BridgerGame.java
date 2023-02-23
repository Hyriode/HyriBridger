package fr.hyriode.bridger.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.util.Skin;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.BridgerData;
import fr.hyriode.bridger.api.BridgerStatistics;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BridgerGame extends HyriGame<BridgerGamePlayer> {

    public static Skin NPC_SKIN = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY0NzYyNzQ0MzU4NiwKICAicHJvZmlsZUlkIiA6ICIxNDU1MDNhNDRjZmI0NzcwYmM3NWNjMTRjYjUwMDE4NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMaWtlbHlFcmljIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkOWRlOTE4ZmQyOTNlMjRlMGY0ZDI5MTNlZjI2YjE3Y2ZhY2UxZGNiMjdkOWU3MjVjNmRmYmQxYjQxNWVjNjEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "ZJyWeWhCnA3AzDGNcg+OfTmgT7TxDFWWEBXyWcX2g7/CeQp6rmndUok5Seq0+iAcISVaUGUUMQjrLIv2ivR4YXltZyMKTirb7vPt5B7rLo7I8j6YWTc6jrJgAbf83e7q5fSF0WNZ4Bm3DG6edSRxwYZOFFaG8v+ZsFy21v0KL+OvAifEGYp6Or01pNfR/aNdhg7fFsk7difO8QIXghoAARiRGzcitDHPtgMCDQuphYkUSSiFlKFIg5sVMXC8a0E8x7wAWXan06/vdlZXnjZz/ZWehEl7wLstLZZb0mNl6k60VVt3lk5/5epMHhoiq3DaKFoxkGLBLx+qQmfT9Y0NsrEyjBdZDYQiOVSomC1c2BJ0f2aGEZCBaOHWzuJY9elB8o+rek4ovBc1E4W+EjwS0EYHggKCPmYhChUntN1TC4XQy64GEquZGAQLCzqKOPOeHdFg1oXAIpgI7R2orjfWkOb4MOtx/k+jriIb+oHjeE/q3tGYtwX9F8UdTWj3ZoyIoHfzd7uxcvGNUAH6X9gNPl0WpX21YmE3zdZnX75p1RRo2iwaUgxNTJdQrqWXN15TmwyvfuisJHZ3oijU8G9Dcaph+FcfJPfFTjmN5Skqb9IVMjmMSKHs7QANNWfhNNO5McwE+kvcIAr3HZsVXmLAAgEJTm9FqaCDU21mwfVCpfA=");

    private final HyriBridger plugin;
    private final List<Boolean> emplacements;
    private final BridgerSession session;

    public BridgerGame(IHyrame hyrame, HyriBridger plugin) {
        super(hyrame, plugin, new BridgerGameInfo("bridger", "Bridger"), BridgerGamePlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? BridgerGameType.SHORT : HyriGameType.getFromData(BridgerGameType.values()));

        this.plugin = plugin;

        this.defaultStarting = false;
        this.usingGameTabList = false;

        this.session = new BridgerSession();
        this.emplacements = new ArrayList<>(getType().getMaxPlayers());
        for (int i = 0; i < getType().getMaxPlayers(); i++) {
            this.emplacements.add(Boolean.FALSE);
        }

        this.setState(HyriGameState.READY);
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final UUID uuid = player.getUniqueId();
        final BridgerGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);
        gamePlayer.setGame(this);
        gamePlayer.setPlayerNumber(getAvailableEmplacement());
        gamePlayer.setStatistics(BridgerStatistics.get(player.getUniqueId()));
        gamePlayer.setData(BridgerData.get(player.getUniqueId()));

        this.getPlayer(player).onJoin();
    }

    //TODO save player data
    @Override
    public void handleLogout(Player player) {
        final BridgerGamePlayer gamePlayer = this.getPlayer(player);
        final BridgerStatistics statistics = gamePlayer.getStatistics();
        final BridgerStatistics.Data statisticsData = gamePlayer.getStatisticsData();
        final BridgerData data = gamePlayer.getData();

        gamePlayer.onLeave();

        statisticsData.setPlayedTime(statisticsData.getPlayedTime() + gamePlayer.getPlayTime());

        if (!HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            statistics.update(player.getUniqueId());
        }

        super.handleLogout(player);
    }

    public int getAvailableEmplacement() {
        int size = emplacements.size();
        for (int i = 0; i < size; i++) {
            if (!emplacements.get(i)) {
                emplacements.set(i, true);
                return i;
            }
        }
        throw new IllegalStateException("Game is full");
    }

    public BridgerSession getSession() {
        return session;
    }

    public List<Boolean> getEmplacements() {
        return emplacements;
    }

    @Override
    public BridgerGameType getType() {
        return (BridgerGameType) super.getType();
    }
}
