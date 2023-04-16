package fr.hyriode.bridger.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.util.BridgerHead;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleportLeaderboardsItem extends HyriItem<HyriBridger> {

    public TeleportLeaderboardsItem(HyriBridger plugin) {
        super(plugin, "leaderboard_island", () -> HyriLanguageMessage.get("item.leaderboard-island.name"), null, ItemBuilder.asHead(BridgerHead.TROPHY).build());
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final BridgerGamePlayer gamePlayer = HyriBridger.get().getGame().getPlayer(player);

        gamePlayer.teleportToLeaderboards();
    }

}
