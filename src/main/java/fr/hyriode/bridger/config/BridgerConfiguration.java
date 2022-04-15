package fr.hyriode.bridger.config;

import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.bridger.Bridger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

import static fr.hyriode.hyrame.configuration.HyriConfigurationEntry.*;

public class BridgerConfiguration implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(Bridger.WORLD.get(), 0, 0, 0, 0, 0);

    private Location spawnLocationOnFirstIsland;
    private final LocationEntry spawnLocationOnFirstIslandEntry;

    private Location npcLocationOnFirstIsland;
    private final LocationEntry npcLocationOnFirstIslandEntry;

    private Location hologramLocationOnFirstIsland;
    private final LocationEntry hologramLocationOnFirstIslandEntry;

    private Location gameAreaOnFirstIslandFirst;
    private final LocationEntry gameAreaOnFirstIslandFirstEntry;

    private Location gameAreaOnFirstIslandSecond;
    private final LocationEntry gameAreaOnFirstIslandSecondEntry;

    private Location spawnLocationOnSecondIsland;
    private final LocationEntry spawnLocationOnSecondIslandEntry;

    private double yMinBeforeTp;
    private final DoubleEntry yMinBeforeTpEntry;

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private Location diffBetweenIslands;

    public BridgerConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.spawnLocationOnFirstIsland = DEFAULT_LOCATION.get();
        this.spawnLocationOnFirstIslandEntry = new LocationEntry("spawn.firstIsland", this.config);
        this.spawnLocationOnSecondIsland = DEFAULT_LOCATION.get();
        this.spawnLocationOnSecondIslandEntry = new LocationEntry("spawn.secondIsland", this.config);

        this.npcLocationOnFirstIsland = DEFAULT_LOCATION.get();
        this.npcLocationOnFirstIslandEntry = new LocationEntry("npc.location", this.config);
        this.hologramLocationOnFirstIsland = DEFAULT_LOCATION.get();
        this.hologramLocationOnFirstIslandEntry = new LocationEntry("hologram.location", this.config);

        final String areaKey = "game-area.";

        this.gameAreaOnFirstIslandFirst = DEFAULT_LOCATION.get();
        this.gameAreaOnFirstIslandFirstEntry = new LocationEntry(areaKey + "first", this.config);
        this.gameAreaOnFirstIslandSecond = DEFAULT_LOCATION.get();
        this.gameAreaOnFirstIslandSecondEntry = new LocationEntry(areaKey + "second", this.config);

        this.yMinBeforeTp = 0;
        this.yMinBeforeTpEntry = new DoubleEntry(areaKey + "yMinBeforeTp", this.config);
    }

    @Override
    public void create() {
        this.spawnLocationOnFirstIslandEntry.setDefault(this.spawnLocationOnFirstIsland);
        this.spawnLocationOnSecondIslandEntry.setDefault(this.spawnLocationOnSecondIsland);

        this.npcLocationOnFirstIslandEntry.setDefault(this.npcLocationOnFirstIsland);
        this.hologramLocationOnFirstIslandEntry.setDefault(this.hologramLocationOnFirstIsland);

        this.gameAreaOnFirstIslandFirstEntry.setDefault(this.gameAreaOnFirstIslandFirst);
        this.gameAreaOnFirstIslandSecondEntry.setDefault(this.gameAreaOnFirstIslandSecond);

        this.yMinBeforeTpEntry.setDefault(this.yMinBeforeTp);

        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        Bridger.log("Loading configuration...");

        this.spawnLocationOnFirstIsland = this.spawnLocationOnFirstIslandEntry.get();
        this.spawnLocationOnSecondIsland = this.spawnLocationOnSecondIslandEntry.get();

        this.npcLocationOnFirstIsland = this.npcLocationOnFirstIslandEntry.get();
        this.hologramLocationOnFirstIsland = this.hologramLocationOnFirstIslandEntry.get();

        this.gameAreaOnFirstIslandFirst = this.gameAreaOnFirstIslandFirstEntry.get();
        this.gameAreaOnFirstIslandSecond = this.gameAreaOnFirstIslandSecondEntry.get();

        this.yMinBeforeTp = this.yMinBeforeTpEntry.get();

        this.diffBetweenIslands = this.spawnLocationOnSecondIsland.subtract(this.spawnLocationOnFirstIsland);
    }

    @Override
    public void save() {
        Bridger.log("Saving configuration...");

        this.spawnLocationOnFirstIslandEntry.set(this.spawnLocationOnFirstIsland);
        this.spawnLocationOnSecondIslandEntry.set(this.spawnLocationOnSecondIsland);

        this.npcLocationOnFirstIslandEntry.set(this.npcLocationOnFirstIsland);
        this.hologramLocationOnFirstIslandEntry.set(this.hologramLocationOnFirstIsland);

        this.gameAreaOnFirstIslandFirstEntry.set(this.gameAreaOnFirstIslandFirst);
        this.gameAreaOnFirstIslandSecondEntry.set(this.gameAreaOnFirstIslandSecond);

        this.yMinBeforeTpEntry.set(this.yMinBeforeTp);

        this.plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    public Location getSpawnLocationOnFirstIsland() {
        this.spawnLocationOnFirstIsland = this.spawnLocationOnFirstIslandEntry.get();
        return this.spawnLocationOnFirstIsland;
    }

    public Location getGameAreaOnFirstIslandFirst() {
        return this.gameAreaOnFirstIslandFirst;
    }

    public Location getGameAreaOnFirstIslandSecond() {
        return this.gameAreaOnFirstIslandSecond;
    }

    public Location getSpawnLocationOnSecondIsland() {
        return this.spawnLocationOnSecondIsland;
    }

    public double getyMinBeforeTp() {
        return yMinBeforeTp;
    }

    public Location getHologramLocationOnFirstIsland() {
        return hologramLocationOnFirstIsland;
    }

    public Location getNPCLocationOnFirstIsland() {
        return npcLocationOnFirstIsland;
    }

    public Location getDiffBetweenIslands() {
        return this.diffBetweenIslands;
    }
}
