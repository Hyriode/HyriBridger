package fr.hyriode.hyribridger;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriBridgerProvider implements IPluginProvider {

    private static final String PACKAGE = "fr.hyriode.hyribridger";

    private final HyriBridger plugin;

    public HyriBridgerProvider(HyriBridger plugin) {
        this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getId() {
        return "hyribridger";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[]{PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}