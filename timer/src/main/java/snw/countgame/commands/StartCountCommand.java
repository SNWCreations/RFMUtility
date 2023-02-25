package snw.countgame.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import snw.countgame.CountGame;
import snw.countgame.CountTimer;

public final class StartCountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        CountTimer a = CountGame.getInstance().getTimer();
        if (a == null) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "计数开始。");
            CountTimer timer = new CountTimer();
            CountGame.getInstance().setTimer(timer);
            timer.start();
        } else {
            if (a.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "计数已经结束。请等待结果。");
            } else {
                a.stop();
            }
        }
        return true;
    }
}
