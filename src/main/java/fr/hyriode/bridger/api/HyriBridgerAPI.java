package fr.hyriode.bridger.api;

import com.google.gson.Gson;
import fr.hyriode.bridger.api.player.HyriBridgerPlayerManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Project: HyriRushTheFlag
 * Created by AstFaster
 * on 31/12/2021 at 18:09
 */
public class HyriBridgerAPI {

    private final HyriBridgerPlayerManager playerManager;

    public HyriBridgerAPI() {
        this.playerManager = new HyriBridgerPlayerManager();
    }

    public HyriBridgerPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
