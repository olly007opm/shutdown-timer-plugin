package ml.olly.shutdowntimer.handlers;

import ml.olly.shutdowntimer.ShutdownTimer;
import ml.olly.shutdowntimer.commands.ShutdownCommand;
import ml.olly.shutdowntimer.manager.ServerShutdown;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnection implements Listener {
    public PlayerConnection(ShutdownTimer plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (ShutdownCommand.getBar() != null && ShutdownCommand.getBar().isVisible()) {
            ShutdownCommand.getBar().addPlayer(event.getPlayer());
            Bukkit.getLogger().info("Added " + event.getPlayer().getName() + " to the shutdown boss bar");
        }
        if (!ServerShutdown.isOnline()) {
            Player player = event.getPlayer();
            if (!player.hasPermission("s6.stimer.immune")) {
                player.kick(Component.text("Server closed"));
            }
        }
    }
}
