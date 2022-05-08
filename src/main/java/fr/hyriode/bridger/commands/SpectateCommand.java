package fr.hyriode.bridger.commands;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectateCommand extends HyriCommand<Bridger> {

    public SpectateCommand(Bridger plugin) {
        super(plugin, new HyriCommandInfo("spectate")
                .withAliases("spec", "sp", "spect")
                .withPermission(iHyriPlayer -> iHyriPlayer.getRank().isStaff())
                .withUsage("/spectate <player>/<island number>")
                .withDescription("permit to teleport a player to an other in spectator"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        handleArgument(ctx, "off", hyriCommandOutput -> {
            BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) ctx.getSender());
            gamePlayer.init(this.plugin, this.plugin.getGame().getFirstEmplacementEmptyAndTakeIt());
        });

        handleArgument(ctx, "leave", hyriCommandOutput -> {
            BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) ctx.getSender());
            gamePlayer.init(this.plugin, this.plugin.getGame().getFirstEmplacementEmptyAndTakeIt());
        });

        handleArgument(ctx, "reset", hyriCommandOutput -> {
            BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) ctx.getSender());
            gamePlayer.init(this.plugin, this.plugin.getGame().getFirstEmplacementEmptyAndTakeIt());
        });

        handleArgument(ctx, "%player%", hyriCommandOutput -> {
            BridgerGamePlayer target = this.plugin.getGame().getPlayer(hyriCommandOutput.get(IHyriPlayer.class).getUniqueId());
            if(target != null) {
                BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) ctx.getSender());
                if(!target.getUUID().equals(gamePlayer.getUUID())) {
                    if(!target.isSpec()) {
                        if(gamePlayer.isBridging()) {
                            gamePlayer.endBridging(false);
                            this.plugin.getGame().getEmplacements().set(gamePlayer.getPlayerNumber(), false);
                            gamePlayer.initSpec(this.plugin, target);
                        }
                    }else {
                        gamePlayer.getPlayer().sendMessage(ChatColor.RED + Bridger.getLanguageManager().getValue(gamePlayer.getUUID(), "message.player.cant-spectate"));
                    }
                }else {
                    gamePlayer.getPlayer().sendMessage(ChatColor.RED + Bridger.getLanguageManager().getValue(gamePlayer.getUUID(), "message.player.cant-spectate"));
                }
            }
        });
    }
}
