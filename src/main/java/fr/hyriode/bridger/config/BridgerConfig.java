package fr.hyriode.bridger.config;

import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.Location;

/**
 * Project: HyriBridger
 * Created by Akkashi
 * on 23/04/2022 at 15:21
 */
public class BridgerConfig implements IConfig {

    private final LocationWrapper spawnLocationOnFirstIsland;
    private final LocationWrapper npcLocationOnFirstIsland;
    private final LocationWrapper hologramLocationOnFirstIsland;
    private final LocationWrapper gameAreaOnFirstIslandFirst;
    private final LocationWrapper gameAreaOnFirstIslandSecond;

    private final double yPosBeforeTeleport;

    private final LocationWrapper diffBetweenIslands; // 20 in x for long and short

    public BridgerConfig(LocationWrapper spawnLocationOnFirstIsland, LocationWrapper npcLocationOnFirstIsland, LocationWrapper hologramLocationOnFirstIsland, LocationWrapper gameAreaOnFirstIslandFirst, LocationWrapper gameAreaOnFirstIslandSecond, double yPosBeforeTeleport, LocationWrapper diffBetweenIslands) {
        this.spawnLocationOnFirstIsland = spawnLocationOnFirstIsland;
        this.npcLocationOnFirstIsland = npcLocationOnFirstIsland;
        this.hologramLocationOnFirstIsland = hologramLocationOnFirstIsland;
        this.gameAreaOnFirstIslandFirst = gameAreaOnFirstIslandFirst;
        this.gameAreaOnFirstIslandSecond = gameAreaOnFirstIslandSecond;
        this.yPosBeforeTeleport = yPosBeforeTeleport;
        this.diffBetweenIslands = diffBetweenIslands;
    }

    public LocationWrapper getSpawnLocationOnFirstIsland() {
        return spawnLocationOnFirstIsland;
    }

    public LocationWrapper getNpcLocationOnFirstIsland() {
        return npcLocationOnFirstIsland;
    }

    public LocationWrapper getHologramLocationOnFirstIsland() {
        return hologramLocationOnFirstIsland;
    }

    public LocationWrapper getGameAreaOnFirstIslandFirst() {
        return gameAreaOnFirstIslandFirst;
    }

    public LocationWrapper getGameAreaOnFirstIslandSecond() {
        return gameAreaOnFirstIslandSecond;
    }

    public double getyPosBeforeTeleport() {
        return yPosBeforeTeleport;
    }

    public LocationWrapper getDiffBetweenIslands() {
        return diffBetweenIslands;
    }
}
