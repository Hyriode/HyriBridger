package fr.hyriode.bridger.game.timers;

import fr.hyriode.bridger.api.BridgerDuration;
import org.bukkit.scheduler.BukkitTask;

public class BridgerTimer {

    private final BukkitTask linkedTask;
    private long startTime = 0;
    private long endTime = 0;
    private BridgerDuration linkedDuration = null;

    public BridgerTimer(BukkitTask linkedTask) {
        this.linkedTask = linkedTask;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.linkedDuration = null;
    }

    public void end() {
        if (this.startTime != 0) {
            this.endTime = System.currentTimeMillis();
        }
    }

    public long getActualTime() {
        if (this.startTime != 0) {
            return System.currentTimeMillis() - this.startTime;
        }
        return 0;
    }

    public String getFormattedActualTime() {
        return new BridgerDuration(this.getActualTime()).toFormattedTime();
    }

    public long getFinalTime() {
        if (this.endTime != 0 && this.startTime != 0) {
            return this.endTime - this.startTime;
        }
        return 0;
    }

    public BridgerDuration toFinalDuration() {
        if (this.linkedDuration == null && this.startTime != 0 && this.endTime != 0) {
            this.linkedDuration = new BridgerDuration(this.getFinalTime());
        }
        return this.linkedDuration;
    }

    public BukkitTask getLinkedTask() {
        return linkedTask;
    }
}