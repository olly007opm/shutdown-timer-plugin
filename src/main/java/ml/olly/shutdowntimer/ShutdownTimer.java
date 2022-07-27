package ml.olly.shutdowntimer;

import ml.olly.shutdowntimer.commands.ShutdownCommand;
import ml.olly.shutdowntimer.handlers.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ShutdownTimer extends JavaPlugin {

    public ShutdownCommand shutdownCommand;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("[ShutdownTimer] Plugin enabled");

        shutdownCommand = new ShutdownCommand(this);
        Objects.requireNonNull(getCommand("shutdown")).setExecutor(shutdownCommand);

        new PlayerConnection(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("[ShutdownTimer] Plugin disabled");
    }
}
