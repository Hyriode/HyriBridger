package fr.hyriode.bridger.commands;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import org.bukkit.plugin.java.JavaPlugin;

public class SpectateCommand extends HyriCommand {

    public SpectateCommand(JavaPlugin plugin) {
        super(plugin, new HyriCommandInfo("spectate")
                .withAliases("spec")
                .withPermission(iHyriPlayer -> iHyriPlayer.getRank().isStaff())
                .withUsage("/spectate <player>/<island number> NÉCESSITE LE GRADE VIP")
                .withDescription("permit to teleport a player to an other in spectator"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        handleArgument(ctx, "%player%", hyriCommandOutput -> {

        });
    }
}
