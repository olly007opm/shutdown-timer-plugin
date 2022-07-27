package ml.olly.shutdowntimer.manager;

import ml.olly.shutdowntimer.commands.ShutdownCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class ShutdownBar {
    private final ShutdownCommand shutdownCommand;
    private int taskID = -1;

    public ShutdownBar(ShutdownCommand shutdownCommand) {
        this.shutdownCommand = shutdownCommand;
    }

    public void removeBar() {
        ShutdownCommand.getBar().setVisible(false);
        shutdownCommand.setBar(null);
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void updateBar(double secondsParam) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(shutdownCommand.getPlugin(), new Runnable() {
            final double totalSeconds = secondsParam;
            double seconds = secondsParam;
            double progress = 1.0;
            boolean finalSound = false;

            @Override
            public void run() {
                String barMessage;
                if (seconds == 60) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.showTitle(Title.title(Component.text("§6§lWARNING"), Component.text("§6§lServer shutting down in 60 seconds")));
                        String sound = "minecraft:block.note_block.bit";
                        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                    }
                }
                if (seconds > 60) {
                    progress = (seconds - 61) / (totalSeconds - 61);
                    ShutdownCommand.getBar().setColor(BarColor.YELLOW);
                    ShutdownCommand.getBar().setStyle(BarStyle.SOLID);
                    String formattedTime = getFormattedTime((int) seconds);

                    barMessage = MessageFormat.format("§eServer shutting down in §6{0}", formattedTime);
                    ShutdownCommand.getBar().setTitle(barMessage);
                } else if (seconds <= 60 && seconds > 10) {
                    progress = (seconds - 11) / 49;
                    ShutdownCommand.getBar().setColor(BarColor.RED);
                    ShutdownCommand.getBar().setStyle(BarStyle.SOLID);
                    barMessage = MessageFormat.format("§cServer shutting down in §4{0} §cseconds", seconds);
                    ShutdownCommand.getBar().setTitle(barMessage);
                } else {
                    progress = seconds / 10;
                    ShutdownCommand.getBar().setColor(BarColor.RED);
                    ShutdownCommand.getBar().setStyle(BarStyle.SEGMENTED_10);
                    barMessage = MessageFormat.format("§4§lSERVER SHUTTING DOWN IN {0}", seconds);
                    ShutdownCommand.getBar().setTitle(barMessage);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String sound = "minecraft:block.note_block.bit";
                        player.playSound(player.getLocation(), sound, 1.0F, finalSound ? 1.0F : 0.0F);
                        String titleMessage;
                        if (seconds > 0) {
                            titleMessage = MessageFormat.format("§4§lSERVER SHUTTING DOWN IN {0}", seconds);
                        } else {
                            titleMessage = "§4§lSERVER SHUTTING DOWN";
                            finalSound = true;
                        }
                        player.showTitle(Title.title(Component.text("§4§lWARNING"), Component.text(titleMessage)));
                    }
                }

                ShutdownCommand.getBar().setProgress(progress >= 0 ? progress : 0);
                if (seconds < 0) {
                    Bukkit.getScheduler().cancelTask(taskID);
                    removeBar();
                    ServerShutdown.closeServer(shutdownCommand.getPlugin());
                }
                seconds = seconds - 1;
            }
        }, 0, 20);
    }

    public static String getFormattedTime(int sec) {
        if (sec <= 60) {
            return sec + (sec == 1 ? " second" : " seconds");
        }
        String formattedHours;
        String formattedMinutes;
        String formattedSeconds;
        if (sec <= 3600) {
            formattedHours = "";
        } else {
            formattedHours = String.valueOf(sec / 3600);
        }
        if ((sec % 3600) / 60 < 10) {
            formattedMinutes = "0" + (sec % 3600) / 60;
        } else {
            formattedMinutes = String.valueOf((sec % 3600) / 60);
        }
        if (sec % 60 < 10) {
            formattedSeconds = "0" + sec % 60;
        } else {
            formattedSeconds = String.valueOf(sec % 60);
        }
        String formattedTime;
        if (!formattedHours.equals("")) {
            formattedTime = MessageFormat.format("{0}:{1}:{2}", formattedHours, formattedMinutes, formattedSeconds);
        } else {
            formattedTime = MessageFormat.format("{0}:{1}", formattedMinutes, formattedSeconds);
        }
        return formattedTime;
    }
}