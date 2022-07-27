package ml.olly.shutdowntimer.manager;

import ml.olly.shutdowntimer.ShutdownTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ServerShutdown {

    private static boolean online = true;
    private static int taskID = -1;

    public static void closeServer(ShutdownTimer plugin) {
        Bukkit.getLogger().warning("Kicking all players...");
        online = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("s6.stimer.immune")) {
                Bukkit.getLogger().info(player.getName() + " was immune from being kicked");
            } else {
                player.kick(Component.text("Server closed"));
                Bukkit.getLogger().info(player.getName() + " was kicked");
            }
        }
        Bukkit.getLogger().warning("Server shutting down in 10 seconds");
        Bukkit.getLogger().warning("This can be stopped with the command /shutdown cancel");
        taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.getLogger().warning("Server shutting down...");
            online = true;
            Bukkit.savePlayers();
            Bukkit.shutdown();
        }, 200L);
    }

    public static boolean isOnline() {
        return online;
    }

    public static int getTaskID() {
        return taskID;
    }
}
