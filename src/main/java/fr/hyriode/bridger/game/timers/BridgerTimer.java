package fr.hyriode.bridger.game.timers;

import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import org.bukkit.scheduler.BukkitTask;

public class BridgerTimer {

    private long startTime;
    private long endTime;
    private final BukkitTask linkedTask;
    private HyriBridgerDuration linkedDuration;

    public BridgerTimer(BukkitTask linkedTask) {
        this.linkedTask = linkedTask;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    public long getActualTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public String getFormattedActualTime() {
        return new HyriBridgerDuration(this.getActualTime()).toFormattedTime();
    }

    public long getFinalTime() {
        return this.endTime - this.startTime;
    }

    public HyriBridgerDuration toFinalDuration() {
        if(this.linkedDuration == null) {
           this.linkedDuration = new HyriBridgerDuration(this.getFinalTime());
        }
        return this.linkedDuration;
    }

    public BukkitTask getLinkedTask() {
        return linkedTask;
    }
}
