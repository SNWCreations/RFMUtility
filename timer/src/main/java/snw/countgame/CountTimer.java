package snw.countgame;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class CountTimer extends BukkitRunnable {
    private int ms;
    private int secs;
    private boolean isOnce;
    private int ticked;

    public void start() {
        isOnce = true;
        runTaskTimer(CountGame.getInstance(), 0L, 1L);
    }

    @Override
    public void run() {
        int fadein = 0;
        if (ticked++ == 0) {
            fadein = 20;
        } else if (ticked >= 20) {
            isOnce = true;
            ms = ms + 50;
            if (ms >= 1000) {
                secs++;
                ms = 0;
            }
        }

        if (isOnce) {
            isOnce = false;
            String msstr = String.valueOf(ms);
            for (Player i : Bukkit.getOnlinePlayers()) {
                i.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "预测时间 " + (((secs == 3 && ms != 0 || secs > 3)) ? ChatColor.MAGIC + "XX:XX" : (secs < 10 ? "0" : "") + secs + "." + ((msstr.length() == 3) ? msstr.substring(0, 2) : ("0" + msstr.charAt(0)))), "", fadein, 60, 10);
            }
        }
    }

    public void stop() {
        cancel();
        Bukkit.broadcastMessage(ChatColor.RED + "计时停止。");
        String msstr = String.valueOf(ms);
        Bukkit.getScheduler().runTaskLater(CountGame.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(IT -> IT.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "预测时间 " + secs + "." + ((msstr.length() == 3) ? msstr.substring(0, 2) : "00"), "", 20, 100, 10)), 140L);
    }
}
