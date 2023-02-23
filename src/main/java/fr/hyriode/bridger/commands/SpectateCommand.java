package fr.hyriode.bridger.commands;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.ChatColor.RED;

public class SpectateCommand extends HyriCommand<HyriBridger> {

    public SpectateCommand(HyriBridger plugin) {
        super(plugin, new HyriCommandInfo("spectate")
                .withAliases("spec", "sp", "spect")
                .withPermission(iHyriPlayer -> iHyriPlayer.getRank().isSuperior(PlayerRank.VIP_PLUS))
                .withUsage("/spectate <player>")
                .withDescription("permit to teleport a player to an other in spectator"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) ctx.getSender());

        if (gamePlayer.isSpectating()) {
            gamePlayer.quitSpectators();
        }

        handleArgument(ctx,"%player%", hyriCommandOutput -> {
            final BridgerGamePlayer target = this.plugin.getGame().getPlayer(hyriCommandOutput.get(IHyriPlayer.class).getUniqueId());
            final Player sender = (Player) ctx.getSender();

            if (target == null) {
                sender.sendMessage(RED + this.getValue(sender.getUniqueId(), "message.player.player-does-not-exist"));
                return;
            }

            if (target.getUniqueId() == sender.getUniqueId()) {
                sender.sendMessage(RED + this.getValue(sender.getUniqueId(), "message.player.can-not-spectate-himself"));
                return;
            }

            if (target.isSpectating()) {
                sender.sendMessage(RED + this.getValue(sender.getUniqueId(), "message.player.can-not-spectate-spectator"));
                return;
            }

            gamePlayer.joinSpectators(target);
        });

        gamePlayer.joinSpectators(null);
    }

    private String getValue(UUID uuid, String key) {
        return HyriLanguageMessage.get(key).getValue(uuid);
    }
}
