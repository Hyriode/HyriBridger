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
    private BridgerTimer timer;
    private boolean isRunning = false;

    public BridgeTask(BridgerGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void start() {
        this.isRunning = true;
        this.timer = new BridgerTimer();
        this.timer.start();
        this.task = this.runTaskTimer(gamePlayer.getPlugin(), 1, 0);
    }

    public void stop() {
        this.isRunning = false;
        this.task.cancel();
        this.timer.end();
    }

    @Override
    public void run() {
        if (timer.getActualTime() > Duration.ofHours(10).toMillis() - 1) {
            gamePlayer.endBridging(false);
            return;
        }
        new ActionBar(ChatColor.DARK_AQUA + timer.getFormattedActualTime()).send(gamePlayer.getPlayer());
    }

    public BridgerGamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public BukkitTask getTask() {
        return task;
    }

    public BridgerTimer getTimer() {
        return timer;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
