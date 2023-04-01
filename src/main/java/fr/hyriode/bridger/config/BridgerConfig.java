package fr.hyriode.bridger.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.utils.AreaWrapper;
import fr.hyriode.hyrame.utils.LocationWrapper;

/**
 * Project: HyriBridger
 * Created by Akkashi
 * on 23/04/2022 at 15:21
 */
public class BridgerConfig implements IHyriConfig {

    private final LocationWrapper islandSpawn;
    private final LocationWrapper islandNpc;
    private final LocationWrapper islandHologram;
    private final AreaWrapper islandArea;

    private final double minY;

    private final LocationWrapper diffBetweenIslands; // 20 in x for long and short

    public BridgerConfig(LocationWrapper islandSpawn, LocationWrapper islandNpc, LocationWrapper islandHologram, AreaWrapper islandArea, double minY, LocationWrapper diffBetweenIslands) {
        this.islandSpawn = islandSpawn;
        this.islandNpc = islandNpc;
        this.islandHologram = islandHologram;
        this.islandArea = islandArea;
        this.minY = minY;
        this.diffBetweenIslands = diffBetweenIslands;
    }

    public LocationWrapper getIslandSpawn() {
        return this.islandSpawn;
    }

    public LocationWrapper getIslandNpc() {
        return this.islandNpc;
    }

    public LocationWrapper getIslandHologram() {
        return this.islandHologram;
    }

    public AreaWrapper getIslandArea() {
        return this.islandArea;
    }

    public double getMinY() {
        return this.minY;
    }

    public LocationWrapper getDiffBetweenIslands() {
        return this.diffBetweenIslands;
    }
}