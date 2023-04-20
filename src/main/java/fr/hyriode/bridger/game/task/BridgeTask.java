package fr.hyriode.bridger.game.task;

import fr.hyriode.bridger.game.player.BridgerGamePlayer;
import fr.hyriode.bridger.game.timers.BridgerTimer;
import fr.hyriode.hyrame.actionbar.ActionBar;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public class BridgeTask extends BukkitRunnable {

    private final BridgerGamePlayer gamePlayer;
    private BukkitTask task;
    private boolean isRunning = false;

    public BridgeTask(BridgerGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void start() {
        this.isRunning = true;
        this.gamePlayer.setTimer(new BridgerTimer());
        this.gamePlayer.getTimer().start();
        this.task = this.runTaskTimer(gamePlayer.getPlugin(), 1, 1);
    }

    public void stop() {
        this.isRunning = false;
        this.task.cancel();
        this.gamePlayer.getTimer().end();
    }

    @Override
    public void run() {
        if (this.gamePlayer.getTimer().getActualTime() > Duration.ofHours(10).toMillis() - 1) {
            gamePlayer.endBridging(false);
            return;
        }
        new ActionBar(ChatColor.DARK_AQUA + this.gamePlayer.getTimer().getFormattedActualTime()).send(gamePlayer.getPlayer());
    }
}
