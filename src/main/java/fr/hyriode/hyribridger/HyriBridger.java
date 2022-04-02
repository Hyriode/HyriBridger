package fr.hyriode.hyribridger;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriBridger extends JavaPlugin {

    public static final String NAME = "Hyrield";

    private IHyrame hyrame;
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

        this.hyrame = HyrameLoader.load(new HyriBridgerProvider(this));

        languageManager = this.hyrame.getLanguageManager();
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

}
