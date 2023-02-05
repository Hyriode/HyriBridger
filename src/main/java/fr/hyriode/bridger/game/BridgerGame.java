package fr.hyriode.bridger.game;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.util.Skin;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.api.player.HyriBridgerData;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BridgerGame extends HyriGame<BridgerGamePlayer> {

    private final HyriBridger plugin;
    private final List<Boolean> emplacements;
    private final BridgerSession session;

    public static Skin NPC_SKIN = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY0NzYyNzQ0MzU4NiwKICAicHJvZmlsZUlkIiA6ICIxNDU1MDNhNDRjZmI0NzcwYmM3NWNjMTRjYjUwMDE4NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMaWtlbHlFcmljIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkOWRlOTE4ZmQyOTNlMjRlMGY0ZDI5MTNlZjI2YjE3Y2ZhY2UxZGNiMjdkOWU3MjVjNmRmYmQxYjQxNWVjNjEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                                          "ZJyWeWhCnA3AzDGNcg+OfTmgT7TxDFWWEBXyWcX2g7/CeQp6rmndUok5Seq0+iAcISVaUGUUMQjrLIv2ivR4YXltZyMKTirb7vPt5B7rLo7I8j6YWTc6jrJgAbf83e7q5fSF0WNZ4Bm3DG6edSRxwYZOFFaG8v+ZsFy21v0KL+OvAifEGYp6Or01pNfR/aNdhg7fFsk7difO8QIXghoAARiRGzcitDHPtgMCDQuphYkUSSiFlKFIg5sVMXC8a0E8x7wAWXan06/vdlZXnjZz/ZWehEl7wLstLZZb0mNl6k60VVt3lk5/5epMHhoiq3DaKFoxkGLBLx+qQmfT9Y0NsrEyjBdZDYQiOVSomC1c2BJ0f2aGEZCBaOHWzuJY9elB8o+rek4ovBc1E4W+EjwS0EYHggKCPmYhChUntN1TC4XQy64GEquZGAQLCzqKOPOeHdFg1oXAIpgI7R2orjfWkOb4MOtx/k+jriIb+oHjeE/q3tGYtwX9F8UdTWj3ZoyIoHfzd7uxcvGNUAH6X9gNPl0WpX21YmE3zdZnX75p1RRo2iwaUgxNTJdQrqWXN15TmwyvfuisJHZ3oijU8G9Dcaph+FcfJPfFTjmN5Skqb9IVMjmMSKHs7QANNWfhNNO5McwE+kvcIAr3HZsVXmLAAgEJTm9FqaCDU21mwfVCpfA=");

    public BridgerGame(IHyrame hyrame, HyriBridger plugin) {
        //dev:
        super(hyrame, plugin, new BridgerGameInfo("bridger", "Bridger"), BridgerGamePlayer.class, BridgerGameType.SHORT);
        //prod:
        //super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("bridger"), BridgerGamePlayer.class, HyriGameType.getFromData(BridgerGameType.values()));

        this.plugin = plugin;
        this.defaultStarting = false;
        this.setState(HyriGameState.READY);
        this.emplacements = new ArrayList<>();
        this.session = new BridgerSession();
        this.usingGameTabList = false;

        for (int i = 0; i < 30; i++) {
            this.emplacements.add(false);
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final BridgerGamePlayer gamePlayer = this.getPlayer(player);
        gamePlayer.init(this.plugin);
    }

    @Override
    public void handleLogout(Player player) {
        this.handleBridgerLogout(player);
        super.handleLogout(player);
    }

    private void handleBridgerLogout(Player player) {
        BridgerGamePlayer gamePlayer = this.getPlayer(player);
        if (gamePlayer.isBridging()) {
            gamePlayer.endBridging(false);
        }

        HyriBridgerData account = HyriBridgerData.get(player.getUniqueId());
        account.setActualBlockId(gamePlayer.getActualBlock().getId());
        account.update(player.getUniqueId());

        gamePlayer.getWatchers().forEach(watcher -> {
            watcher.getPlayer().sendMessage(ChatColor.AQUA + HyriLanguageMessage.get("message.player.watched-player-disconnected").getValue(watcher.getUniqueId()));
            watcher.reset();
        });

        gamePlayer.sendPlayerStats();
        gamePlayer.deleteHologram();
        gamePlayer.deleteNPC();

        this.session.removeScoresOf(player);
        this.emplacements.set(this.getPlayer(player).getPlayerNumber(), false);

        if (!gamePlayer.getWatchers().isEmpty()) {
            for (BridgerGamePlayer watcher : gamePlayer.getWatchers()) {
                watcher.setIslandNumber(this.getFirstEmplacementEmptyAndTakeIt());
            }
        }
    }

    public int getFirstEmplacementEmptyAndTakeIt() {
        for (int i = 0; i < emplacements.size()-1; i++) {
            if (!this.emplacements.get(i)) {
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
