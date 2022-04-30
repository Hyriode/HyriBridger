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
        StringBuilder debugCode = new StringBuilder("Classement debug code: ");
        if(this.scoreFirst == null) {
            debugCode.append("0");
            this.scoreFirst = new BridgerScore(duration, player);
            player.sendMessage(debugCode.toString());
            return;
        }
        if(this.scoreSecond == null) {
            debugCode.append("1");
            if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                debugCode.append("0");
                if(this.scoreFirst.getPlayer().equals(player)) {
                    debugCode.append("0");
                    this.scoreFirst.setDuration(duration);
                }else {
                    debugCode.append("1");
                    this.scoreSecond = this.scoreFirst;
                    this.scoreFirst = new BridgerScore(duration, player);
                }
            }else {
                debugCode.append("1");
                if(!this.scoreFirst.getPlayer().equals(player)) {
                    debugCode.append("a");
                    this.scoreSecond = new BridgerScore(duration, player);
                }
            }
            player.sendMessage(debugCode.toString());
            return;
        }
        if(this.scoreThird == null) {
            debugCode.append("2");
            if(duration.getExactTime() < this.scoreSecond.getDuration().getExactTime()) {
                debugCode.append("0");
                if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                    debugCode.append("0");
                    if(this.scoreFirst.getPlayer().equals(player)) {
                        this.scoreFirst.setDuration(duration);
                    }else {
                        this.scoreThird = this.scoreSecond;
                        this.scoreSecond = this.scoreFirst;
                        this.scoreFirst  = new BridgerScore(duration, player);
                    }
                }else {
                    debugCode.append("1");
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
                debugCode.append("1");
                if(!this.scoreFirst.getPlayer().equals(player) && !this.scoreSecond.getPlayer().equals(player)) {
                    this.scoreThird = new BridgerScore(duration, player);
                }
            }
            player.sendMessage(debugCode.toString());
            return;
        }
        if(duration.getExactTime() < this.scoreThird.getDuration().getExactTime()) {
            debugCode.append("3");
            if(this.scoreThird.getPlayer().equals(player)) {
                debugCode.append("a");
                this.scoreThird = null;
            }
            if(duration.getExactTime() < this.scoreSecond.getDuration().getExactTime()) {
                debugCode.append("0");
                if(this.scoreSecond.getPlayer().equals(player)) {
                    debugCode.append("a");
                    this.scoreSecond = this.scoreThird;
                    this.scoreThird = null;
                }
                if(duration.getExactTime() < this.scoreFirst.getDuration().getExactTime()) {
                    debugCode.append("0");
                    if(this.scoreFirst.getPlayer().equals(player)) {
                        debugCode.append("0");
                        this.scoreFirst.setDuration(duration);
                    }else {
                        debugCode.append("1");
                        this.scoreThird = this.scoreSecond;
                        this.scoreSecond = this.scoreFirst;
                        this.scoreFirst  = new BridgerScore(duration, player);
                    }
                }else {
                    debugCode.append("1");
                    if(this.scoreSecond.getPlayer().equals(player)) {
                        debugCode.append("0");
                        this.scoreSecond.setDuration(duration);
                    }else {
                        debugCode.append("1");
                        if(!this.scoreFirst.getPlayer().equals(player)) {
                            debugCode.append("a");
                            this.scoreThird = this.scoreSecond;
                            this.scoreSecond = new BridgerScore(duration, player);
                        }
                    }
                }
            }else {
                debugCode.append("1");
                if(this.scoreThird.getPlayer().equals(player)) {
                    debugCode.append("0");
                    this.scoreThird.setDuration(duration);
                }else {
                    debugCode.append("1");
                    if(!this.scoreFirst.getPlayer().equals(player) && !this.scoreSecond.getPlayer().equals(player)) {
                        debugCode.append("a");
                        this.scoreThird = new BridgerScore(duration, player);
                    }
                }
            }
        }
        player.sendMessage(debugCode.toString());
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
                return ChatColor.GRAY + this.scoreFirst.getPlayer().getDisplayName() + ": " + ChatColor.YELLOW + this.scoreFirst.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }else if(i == 2) {
            if(this.scoreSecond != null) {
                return ChatColor.GRAY + this.scoreSecond.getPlayer().getDisplayName() + ": " + ChatColor.YELLOW + this.scoreSecond.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }else {
            if (this.scoreThird != null) {
                return ChatColor.GRAY + this.scoreThird.getPlayer().getDisplayName() + ": " + ChatColor.YELLOW + this.scoreThird.getDuration().toFormattedTime();
            }
            return returnString.toString();
        }
    }
}
