package ml.olly.shutdowntimer.commands;

import ml.olly.shutdowntimer.ShutdownTimer;
import ml.olly.shutdowntimer.manager.ServerShutdown;
import ml.olly.shutdowntimer.manager.ShutdownBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShutdownCommand implements CommandExecutor {

    private final ShutdownTimer plugin;
    private final ShutdownBar shutdownBar = new ShutdownBar(this);
    private static BossBar bar;

    public ShutdownCommand(ShutdownTimer plugin) {
       this.plugin = plugin;
    }

    public ShutdownTimer getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String [] args
    ) {
        if (args.length != 1) {
            return false;
        }

        // /shutdown cancel
        if (args[0].equals("cancel")) {
            if (getBar() != null) {
                shutdownBar.removeBar();
                sender.sendMessage("§4§lS6 §8§l>> §cCanceled server shutdown");
            } else if (ServerShutdown.getTaskID() != -1) {
                Bukkit.getScheduler().cancelTask(ServerShutdown.getTaskID());
                sender.sendMessage("§4§lS6 §8§l>> §cCanceled server shutdown");
            } else {
                sender.sendMessage("§4§lS6 §8§l>> §cBar does not exist");
            }
            return true;
        }

        String timeFormat = args[0].substring(args[0].length() - 1);
        String secondsString = args[0].substring(0, args[0].length() - 1);
        int seconds;
        try {
            Integer.parseInt(secondsString);
        } catch (NumberFormatException e) {
            sender.sendMessage("§4§lS6 §8§l>> §c" + secondsString + "is not a valid integer.");
            return false;
        }

        // Check if the bar already exists
        if (getBar() != null && getBar().isVisible()) {
            sender.sendMessage("§4§lS6 §8§l>> §cBoss bar already exists");
            return true;
        }

        seconds = Integer.parseInt(args[0].substring(0, args[0].length() - 1));
        if (timeFormat.equals("h")) {
            seconds = seconds * 3600;
        } else if (timeFormat.equals("m")) {
            seconds = seconds * 60;
        } else if (!timeFormat.equals("s")) {
            sender.sendMessage("§4§lS6 §8§l>> §cInvalid time format");
            return false;
        }
        sender.sendMessage("§4§lS6 §8§l>> §cShutting down server in " + ShutdownBar.getFormattedTime(seconds));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§4§lS6 §8§l>> §cServer will shut down in " + ShutdownBar.getFormattedTime(seconds));
        }

        setBar(Bukkit.createBossBar("§eServer shutting down", BarColor.YELLOW, BarStyle.SOLID));
        for (Player player : Bukkit.getOnlinePlayers()) {
            getBar().addPlayer(player);
        }
        getBar().setVisible(true);
        shutdownBar.updateBar(seconds);
        return true;
    }

    public static BossBar getBar() {
        return bar;
    }

    public void setBar(BossBar bar) {
        ShutdownCommand.bar = bar;
    }
}
