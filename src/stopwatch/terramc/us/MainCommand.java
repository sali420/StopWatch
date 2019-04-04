package stopwatch.terramc.us;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import stopwatch.terramc.us.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainCommand implements CommandExecutor {

    private int durRemaining;

    private final HashMap<UUID, Boolean> runningMap = new HashMap<>();

    private final Main plugin;

    public MainCommand(Main pluginn) { // registering our command
        plugin = pluginn;
        plugin.getCommand("stopwatch").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) { // if our command user is a player

            Player player = (Player) sender;

            // make sure our hashmap isnt returning null values!!
            runningMap.putIfAbsent(player.getUniqueId(), false);

            if (args.length == 0) { // Do this if they provide no arguments

                if (runningMap.get(player.getUniqueId())) {

                    int hours=0;
                    int minutes=0;
                    int seconds=0;

                    while (durRemaining > 0) {
                        if (durRemaining >= 3600) {
                            durRemaining = durRemaining - 3600;
                            hours++;
                        }
                        else if (durRemaining >= 60) {
                            durRemaining = durRemaining - 60;
                            minutes++;
                        }
                        else if (durRemaining > 1) {
                            durRemaining = durRemaining - 1;
                            seconds++;
                        }
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&9Time remaining: &a"
                            + hours + " &ehours &a" + minutes + " &eminutes &a" + seconds + " &eseconds."));

                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix
                            + "You have no timer set. Type /stopwatch menu for a list of available commands."));
                }

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("menu")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix
                        + "&9&lMain Menu"
                        + "\n&7--- &aAvailable Commands &7---"
                        + "\n&e/stopwatch &7- Displays the current time remaining until your timer finishes."
                        + "\n&e/stopwatch &9menu &7- Displays this help menu."
                        + "\n&e/stopwatch &astart &bhr:min:sec &7- Starts a timer for the given duration. Example: /stopwatch start 0:5:30 starts a 5 min 30 sec timer."
                        + "\n&e/stopwatch &cstop &7- Stops your currently running timer."));

            }

            if (args.length >= 1 && args[0].equalsIgnoreCase("start")) { // if they /stopwatch start and or add a duration

                if (args.length == 1) { // if they dont add a duration
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "You need to enter a duration. Type /stopwatch to see an example."));
                }

                if (args.length == 2) { // if they provide a duration

                    String input = args[1];
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

                    try {
                        LocalTime inputTime = LocalTime.parse(input, dtf);
                        int hours = inputTime.getHour(); int minutes = inputTime.getMinute(); int seconds = inputTime.getSecond();
                        int duration = hours * plugin.hour + minutes * plugin.minute + seconds * plugin.second;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "Timer started for: " + "&a" + hours + " &7hours &a" + minutes + " &7minutes &a" + seconds + " &7seconds."));

                        runningMap.put(player.getUniqueId(), true);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 3);
                        timer(player, duration);

                    } catch (DateTimeException e) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: Invalid format."));
                    }

                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("stop")) { // if they choose to do /stopwatch stop
                if (runningMap.get(player.getUniqueId())) { // If they have a timer running... stop it
                    runningMap.put(player.getUniqueId(), false);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "Timer stopped."));
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                } else { // if there is no timer running
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: you are not currently running a timer."));
                }
            } else if (args.length >= 1 && !args[0].equalsIgnoreCase("start") && !args[0].equalsIgnoreCase("stop") && !args[0].equalsIgnoreCase("menu")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "Invalid argument specified. Type /stopwatch for an example."));
            }
        } else { // if some dumbass tries to use the console like a retard
            sender.sendMessage("[StopWatch] This command only available to players.");
        }

        return true;

    }

    private void timer(Player player, int duration) { // Timer runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    runningMap.put(player.getUniqueId(), true);

                    int counter = 1;
                    int dur = duration / 1000;

                    while (counter < dur) {

                        counter++;
                        durRemaining = dur - counter;

                        if (!runningMap.get(player.getUniqueId())) {
                            durRemaining = 0;
                            this.cancel();
                            return;
                        }

                        if (!player.isOnline()) {
                            runningMap.put(player.getUniqueId(), false);
                            durRemaining = 0;
                            this.cancel();
                            return;
                        }

                        Thread.sleep(1000);
                    }

                    runningMap.put(player.getUniqueId(), false);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&4&l*** &e&lYOUR TIMER HAS FINISHED &4&l***\n "));

                } catch (InterruptedException e) {
                    player.sendMessage("Please contact an administrator if you see this message and let them know which command you used.");
                } catch (IllegalStateException e) {
                    player.sendMessage("Illegal state exception");
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}