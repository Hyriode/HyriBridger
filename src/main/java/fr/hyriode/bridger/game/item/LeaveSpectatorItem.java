package fr.hyriode.bridger.game.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.bridger.HyriBridger;
import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveSpectatorItem extends HyriItem<HyriBridger> {

    public LeaveSpectatorItem(HyriBridger plugin) {
        super(plugin, "leave_spectator_item", () -> HyriLanguageMessage.get("item.global.leave"), null, new ItemStack(Material.INK_SACK, 1, (short) 1));
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        BridgerGamePlayer bridgerGamePlayer = HyriBridger.get().getGame().getPlayer(event.getPlayer());
        if (bridgerGamePlayer == null || !bridgerGamePlayer.isSpectator()) {
            return;
        }

        bridgerGamePlayer.setSpectator(false);
    }

}
