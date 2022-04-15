package fr.hyriode.bridger.api.player;

import fr.hyriode.bridger.api.HyriBridgerAPI;

import java.util.UUID;
import java.util.function.Function;

public class HyriBridgerPlayerManager {

    private static final Function<UUID, String> REDIS_KEY = uuid -> HyriBridgerAPI.REDIS_KEY + "players:" + uuid.toString();

    private final HyriBridgerAPI api;

    public HyriBridgerPlayerManager(HyriBridgerAPI api) {
        this.api = api;
    }

    public HyriBridgerPlayer getPlayer(UUID uuid) {
        final String json = this.api.getFromRedis(REDIS_KEY.apply(uuid));

        if (json != null) {
            return HyriBridgerAPI.GSON.fromJson(json, HyriBridgerPlayer.class);
        }else {
            HyriBridgerPlayer account = new HyriBridgerPlayer(uuid);
            this.sendPlayer(account);
            return account;
        }
    }

    public void sendPlayer(HyriBridgerPlayer player) {
        this.api.redisRequest(jedis -> jedis.set(REDIS_KEY.apply(player.getUniqueId()), HyriBridgerAPI.GSON.toJson(player)));
    }

    public void removePlayer(UUID uuid) {
        this.api.redisRequest(jedis -> jedis.del(REDIS_KEY.apply(uuid)));
    }

}
