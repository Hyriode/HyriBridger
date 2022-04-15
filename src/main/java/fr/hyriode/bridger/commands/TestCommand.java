package fr.hyriode.bridger.commands;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.bridger.Bridger;
import fr.hyriode.bridger.game.BridgerGamePlayer;
import org.bukkit.entity.Player;

public class TestCommand extends HyriCommand<Bridger> {

    public TestCommand(Bridger plugin) {
        super(plugin, new HyriCommandInfo("test")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/test")
                .withDescription("test"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final BridgerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(((Player)ctx.getSender()).getUniqueId());

        if(ctx.getArgs()[0].equalsIgnoreCase("start")) {
            gamePlayer.startBridging();
        }else if(ctx.getArgs()[0].equalsIgnoreCase("end")) {
            gamePlayer.endBridging(true);
        }
    }
}
