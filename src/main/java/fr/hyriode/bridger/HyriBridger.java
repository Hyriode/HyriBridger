package fr.hyriode.bridger;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.bridger.config.BridgerConfig;
import fr.hyriode.bridger.game.BridgerGame;
import fr.hyriode.bridger.game.BridgerGameType;
import fr.hyriode.bridger.utils.MessageHelper;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.utils.LocationWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriBridger extends JavaPlugin {

    public static final String NAME = "HyriBridger";

    private IHyrame hyrame;
    private BridgerGame game;

    private MessageHelper messageHelper;
    private BridgerConfig configuration;

    @Override
    public void onEnable() {
        final ChatColor color = ChatColor.BLUE;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color +  "  _    _               _  _            _      _ ");
        sender.sendMessage(color +  " | |  | |             (_)| |          (_)    | | ");
        sender.sendMessage(color +  " | |__| | _   _  _ __  _ | |__   _ __  _   __| |  __ _   ___  _ __ ");
        sender.sendMessage(color + "  |  __  || | | || '__|| || '_ \\ | '__|| | / _` | / _` | / _ \\| '__| ");
        sender.sendMessage(color +  " | |  | || |_| || |   | || |_) || |   | || (_| || (_| ||  __/| | ");
        sender.sendMessage(color +  " |_|  |_| \\__, ||_|   |_||_.__/ |_|   |_| \\__,_| \\__, | \\___||_| ");
        sender.sendMessage(color +  "           __/ |                                  __/ | ");
        sender.sendMessage(color +  "          |___/                                  |___/ ");

        log("Starting " + NAME + "...");

        if (HyriAPI.get().getConfig().isDevEnvironment()) {
            this.configuration = new BridgerConfig(
//  Spawn location on the first island
            new LocationWrapper(new Location(IHyrame.WORLD.get(), 0.5, 100.0, 0.5, 180.0F, 0.0F)),
// NPC Location on the first island
                new LocationWrapper(new Location(IHyrame.WORLD.get(), -3.5, 100.0, 1.5, -90.0F, 0.0F)),
// Hologram Location on the first island
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 2.0, 102.0, 5.0, 0.0F, 0.0F)),
// Pos1 of island area
                new LocationWrapper(new Location(IHyrame.WORLD.get(), -6, 107.0, -1.0)),
// Pos2 of island area
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 6.0, 96, -52.0)),
// Y coordinates where the player respawn
                97.0,
// Distance between 2 islands
                new LocationWrapper(new Location(IHyrame.WORLD.get(), 25, 0.0,0)));
        } else this.configuration = HyriAPI.get().getServer().getConfig(BridgerConfig.class);

        this.hyrame = HyrameLoader.load(new BridgerProvider(this));
        this.messageHelper = new MessageHelper();

        //HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(this.configuration, "bridger", BridgerGameType.NORMAL.getName(), "normal");
        //HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "bridger", BridgerGameType.NORMAL.getName(), "normal");

        //HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(this.configuration, "bridger", BridgerGameType.SHORT.getName(), "short");
        //HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "bridger", BridgerGameType.SHORT.getName(), "short");

        //HyriAPI.get().getHystiaAPI().getConfigManager().saveConfig(this.configuration, "bridger", BridgerGameType.DIAGONAL.getName(), "Diagonal");
        //HyriAPI.get().getHystiaAPI().getWorldManager().saveWorld(IHyrame.WORLD.get().getUID(), "bridger", BridgerGameType.DIAGONAL.getName(), "Diagonal");

        this.game = new BridgerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        HyriAPI.get().getServer().setState(IHyriServer.State.READY);
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

    public IHyrame getHyrame() {
        return hyrame;
    }

    public BridgerGame getGame() {
        return game;
    }

    public BridgerConfig getConfiguration() {
        return configuration;
    }

    public MessageHelper getMessageHelper() {
        return messageHelper;
    }
}