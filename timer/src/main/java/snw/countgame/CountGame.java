package snw.countgame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import snw.countgame.commands.OKCommand;
import snw.countgame.commands.StartCountCommand;

public final class CountGame extends JavaPlugin {
    private static CountGame INSTANCE;
    private CountTimer timer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerCommand("cgc", new StartCountCommand());
        registerCommand("cgok", new OKCommand());
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CountGame getInstance() {
        return INSTANCE;
    }

    public CountTimer getTimer() {
        return timer;
    }

    public void setTimer(CountTimer countTimer) {
        timer = countTimer;
    }

    private void registerCommand(String cmdName, CommandExecutor executor) {
        PluginCommand cmd = Bukkit.getPluginCommand(cmdName);
        if (cmd == null) {
            Bukkit.getConsoleSender().sendMessage("[CountGame] " + ChatColor.RED + "插件加载失败。" + cmdName + " 命令未正常注册，请联系作者寻求帮助。");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            cmd.setExecutor(executor);
        }
    }
}
