package fr.hyriode.bridger.game;

import fr.hyriode.bridger.api.duration.HyriBridgerDuration;
import fr.hyriode.bridger.game.timers.BridgerScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BridgerSession {

    private BridgerScore scoreFirst;
    private BridgerScore scoreSecond;
    private BridgerScore scoreThird;

    public void add(Player player, HyriBridgerDuration duration) {
        if(this.scoreFirst == null) {
            this.scoreFirst = new BridgerScore(duration, player);
            return;
        }
        if(this.scoreSecond == null) {
            if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                if(this.scoreFirst.getPlayer().equals(player)) {
                    this.scoreFirst.setDuration(duration);
                }else {
                    this.scoreSecond = this.scoreFirst;
                    this.scoreFirst = new BridgerScore(duration, player);
                }
            }else {
                this.scoreSecond = new BridgerScore(duration, player);
            }
            return;
        }
        if(this.scoreThird == null) {
            if(duration.getExactTime() < this.scoreSecond.getDuration().getExactTime()) {
                if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                    if(this.scoreFirst.getPlayer().equals(player)) {
                        this.scoreFirst.setDuration(duration);
                    }else {
                        this.scoreThird = this.scoreSecond;
                        this.scoreSecond = this.scoreFirst;
                        this.scoreFirst  = new BridgerScore(duration, player);
                    }
                }else {
                    if(this.scoreSecond.getPlayer().equals(player)) {
                        this.scoreSecond.setDuration(duration);
                    }else {
                        if(!this.scoreFirst.getPlayer().equals(player)) {
                            this.scoreThird = this.scoreSecond;
                            this.scoreSecond = new BridgerScore(duration, player);
                        }
                    }
                }
            }else {
                if(!this.scoreFirst.getPlayer().equals(player) && !this.scoreSecond.getPlayer().equals(player)) {
                    this.scoreThird = new BridgerScore(duration, player);
                }
            }
            return;
        }
        if(duration.getExactTime() < this.scoreThird.getDuration().getExactTime()) {
            if(this.scoreThird.getPlayer().equals(player)) {
                this.scoreThird = null;
            }
            if(duration.getExactTime() < this.scoreSecond.getDuration().getExactTime()) {
                if(this.scoreSecond.getPlayer().equals(player)) {
                    this.scoreSecond = this.scoreThird;
                    this.scoreThird = null;
                }
                if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                    if(this.scoreFirst.getPlayer().equals(player)) {
                        this.scoreFirst.setDuration(duration);
                    }else {
                        this.scoreThird = this.scoreSecond;
                        this.scoreSecond = this.scoreFirst;
                        this.scoreFirst  = new BridgerScore(duration, player);
                    }
                }else {
                    if(this.scoreSecond.getPlayer().equals(player)) {
                        this.scoreSecond.setDuration(duration);
                    }else {
                        if(!this.scoreFirst.getPlayer().equals(player)) {
                            this.scoreThird = this.scoreSecond;
                            this.scoreSecond = new BridgerScore(duration, player);
                        }
                    }
                }
            }else {
                if(this.scoreThird.getPlayer().equals(player)) {
                    this.scoreThird.setDuration(duration);
                }else {
                    if(!this.scoreFirst.getPlayer().equals(player) && !this.scoreSecond.getPlayer().equals(player)) {
                        this.scoreThird = new BridgerScore(duration, player);
                    }
                }
            }
        }
    }

    public void removeScoresOf(Player player) {
        if(this.scoreFirst != null) {
            if(this.scoreFirst.getPlayer().equals(player)) {
                this.scoreFirst = this.scoreSecond;
                this.scoreSecond = this.scoreThird;
                this.scoreThird = null;
            }
        }
        if(this.scoreSecond != null) {
            if(this.scoreSecond.getPlayer().equals(player)) {
                this.scoreSecond = this.scoreThird;
                this.scoreThird = null;
            }
        }
        if(this.scoreThird != null) {
            if (this.scoreThird.getPlayer().equals(player)) {
                this.scoreThird = null;
            }
        }
    }

    public String getFormattedTop(int i) {
        StringBuilder returnString = new StringBuilder(ChatColor.GRAY + "none:" + ChatColor.YELLOW + " 0.000");
        for (int i1 = 0; i1 < i; i1++) {
            returnString.append(" ");
        }
        if(i == 1) {
            if(this.scoreFirst != null) {
                return ChatColor.GRAY + this.scoreFirst.getPlayer().getName() + ": " + ChatColor.YELLOW + this.scoreFirst.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }else if(i == 2) {
            if(this.scoreSecond != null) {
                return ChatColor.GRAY + this.scoreSecond.getPlayer().getName() + ": " + ChatColor.YELLOW + this.scoreSecond.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }else {
            if (this.scoreThird != null) {
                return ChatColor.GRAY + this.scoreThird.getPlayer().getName() + ": " + ChatColor.YELLOW + this.scoreThird.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }
    }
}
