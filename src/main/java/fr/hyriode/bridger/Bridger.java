package fr.hyriode.bridger;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.api.HyriBridgerAPI;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;
import java.util.logging.Level;

public class Bridger extends JavaPlugin {

    public static final String NAME = "Bridger";
    public static final Supplier<World> WORLD = () -> Bukkit.getWorld("world");

    private IHyrame hyrame;
    private BridgerGame game;
    private HyriBridgerAPI api;

    private BridgerConfig configuration;

    private static IHyriLanguageManager languageManager;

    @Override
    public void onEnable() {
        /*
        final ChatColor color = ChatColor.BLUE;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color +  "  _    _               _  _            _      _ ");
        sender.sendMessage(color +  " | |  | |             (_)| |          (_)    | | ");
        sender.sendMessage(color +  " | |__| | _   _  _ __  _ | |__   _ __  _   __| |  __ _   ___  _ __ ");
        sender.sendMessage(color + " |  __  || | | || '__|| || '_ \\ | '__|| | / _` | / _` | / _ \\| '__| ");
        sender.sendMessage(color +  " | |  | || |_| || |   | || |_) || |   | || (_| || (_| ||  __/| | ");
        sender.sendMessage(color +  " |_|  |_| \\__, ||_|   |_||_.__/ |_|   |_| \\__,_| \\__, | \\___||_| ");
        sender.sendMessage(color +  "           __/ |                                  __/ | ");
        sender.sendMessage(color +  "          |___/                                  |___/ ");

        log("Starting " + NAME + "...");

         */

        this.configuration = new BridgerConfig(
                // Position de spawn sur la première île de la série (celle en bout)
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 0.0, 100.0, 0.0, 180.0F, 0.0F)),

                // Position du NPC sur la première île de la série
                new LocationWrapper(new Location(IHyrame.WORLD.get(), -4.0, 100.0, 1.0, 0.0F, 0.0F)),

                // Position de l'hologram sur la première île de la série
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 4.0, 101.0, 0.0, 0.0F, 0.0F)),

                // Pos1 de la zone de l'île
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 16.0, 65.0, -3.0)),

                // Pos2 de la zone de l'île
                new LocationWrapper(new Location(IHyrame.WORLD.get(), -16.0, 136.0, 14.0)),

                // Coordonnée y où t'es re-tp
                92.0,

                // Distance entre 2 îles genre si c'est 20 blocks en x tu mets 20 en x
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 20.0, 0.0,0.0))
        );

        this.hyrame = HyrameLoader.load(new BridgerProvider(this));

        HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(this.configuration, "bridger", BridgerGameType.LONG.getName(), "long").whenComplete((aBoolean, throwable) -> System.out.println(aBoolean + " map l"));
        HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "bridger", BridgerGameType.LONG.getName(), "long").whenComplete((aBoolean, throwable) -> System.out.println(aBoolean + " world l"));

        /*
        HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(this.configuration, "bridger", BridgerGameType.SHORT.getName(), "short").whenComplete((aBoolean, throwable) -> System.out.println(aBoolean + " map s"));
        HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "bridger", BridgerGameType.SHORT.getName(), "short").whenComplete((aBoolean, throwable) -> System.out.println(aBoolean + " world s"));


        languageManager = this.hyrame.getLanguageManager();

        this.api = new HyriBridgerAPI();

        this.game = new BridgerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

         */
    }

    @Override
    public void onDisable() {
        this.hyrame.getGameManager().unregisterGame(this.game);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public static void log(Level level, String msg) {
        String prefix = ChatColor.RED + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + msg);
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }

    public IHyrame getHyrame() {
        return hyrame;
    }

    public BridgerGame getGame() {
        return game;
    }

    public BridgerConfig getConfiguration() {
        return configuration;
    }

    public HyriBridgerAPI getApi() {
        return api;
    }
}
