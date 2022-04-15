package fr.hyriode.bridger;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.bridger.api.HyriBridgerAPI;
import fr.hyriode.bridger.listener.PlayerListener;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.bridger.config.BridgerConfiguration;
import fr.hyriode.bridger.game.BridgerGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private BridgerConfiguration configuration;

    private static IHyriLanguageManager languageManager;

    @Override
    public void onEnable() {
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

        this.configuration = new BridgerConfiguration(this);
        this.configuration.create();
        this.configuration.load();
        this.hyrame = HyrameLoader.load(new BridgerProvider(this));

        languageManager = this.hyrame.getLanguageManager();

        this.api = new HyriBridgerAPI(HyriAPI.get().getRedisConnection().getPool());
        this.api.start();
        this.game = new BridgerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);
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

    public BridgerConfiguration getConfiguration() {
        return configuration;
    }

    public HyriBridgerAPI getApi() {
        return api;
    }
}
