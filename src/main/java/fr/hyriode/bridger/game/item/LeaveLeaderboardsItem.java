package fr.hyriode.bridger.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveLeaderboardsItem extends HyriItem<HyriBridger> {

    public LeaveLeaderboardsItem(HyriBridger plugin) {
        super(plugin, "leave_leaderboards", () -> HyriLanguageMessage.get("item.global.leave"), null, new ItemStack(Material.INK_SACK, 1, (short) 1));
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final BridgerGamePlayer gamePlayer = HyriBridger.get().getGame().getPlayer(event.getPlayer());

        gamePlayer.spawnPlayer();
    }

}
