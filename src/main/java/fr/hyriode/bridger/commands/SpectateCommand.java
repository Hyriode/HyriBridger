package fr.hyriode.bridger.commands;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.language.BridgerMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

public class SpectateCommand extends HyriCommand<HyriBridger> {

    public SpectateCommand(HyriBridger plugin) {
        super(plugin, new CommandInfo("spectate")
                .withAliases("spec", "sp", "spect")
                .withPermission(iHyriPlayer -> iHyriPlayer.getRank().isSuperior(PlayerRank.VIP_PLUS))
                .withUsage(new CommandUsage().withStringMessage(player -> "/spectate <player>"))
                .withDescription("permit to teleport a player to an other in spectator"));
    }

    @Override
    public void handle(CommandContext ctx) {
        BridgerGamePlayer gamePlayer = plugin.getGame().getPlayer(ctx.getSender());
        if (ctx.getArgs().length == 0) {
            if (gamePlayer.isSpectating()) {
                gamePlayer.quitSpectators();
            } else {
                gamePlayer.joinSpectators();
            }
            return;
        }

        ctx.registerArgument("%player%", "/spectate %player%", hyriCommandOutput -> {
            Player sender = (Player) ctx.getSender();
            BridgerGamePlayer target = plugin.getGame().getPlayer(hyriCommandOutput.get(IHyriPlayer.class).getUniqueId());
            if (target == null) {
                sender.sendMessage(RED + BridgerMessage.MESSAGE_PLAYER_PLAYER_DOES_NOT_EXIST.asString(gamePlayer.getPlayer()));
                return;
            }
            if (target.getUniqueId().equals(sender.getUniqueId())) {
                sender.sendMessage(RED + BridgerMessage.MESSAGE_PLAYER_CAN_NOT_SPECTATE_HIMSELF.asString(gamePlayer.getPlayer()));
                return;
            }
            if (target.isSpectating()) {
                sender.sendMessage(RED + BridgerMessage.MESSAGE_PLAYER_CAN_NOT_SPECTATE_SPECTATOR.asString(gamePlayer.getPlayer()));
                return;
            }
            gamePlayer.joinSpectators(target);
        });
    }
}
