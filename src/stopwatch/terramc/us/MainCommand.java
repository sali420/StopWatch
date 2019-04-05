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

    private int mode = 1; // temporary mode = timer

    private final HashMap<UUID, Boolean> runningMap = new HashMap<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final LocalTime defTime = LocalTime.parse("00:00:00",dtf);
    private LocalTime timeLeft;
    private LocalTime timeRan;
    private int timer = 1;

    private final Main plugin;

    public MainCommand(Main pluginn) { // registering our command
        plugin = pluginn;
        plugin.getCommand("stopwatch").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) { // if our command user is a player

            Player player = (Player) sender;
            runningMap.putIfAbsent(player.getUniqueId(), false); // make sure our hashmap isnt returning null values!!

            if (args.length == 0) { // Do this if they provide no arguments

                if (runningMap.get(player.getUniqueId())) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&9Time remaining: &a"
                            + timeLeft.getHour() + " &ehours &a" + timeLeft.getMinute() + " &eminutes &a" + timeLeft.getSecond() + " &eseconds."));

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
                        + "\n&e/stopwatch &9mode &b[alarm/timer] &7- Display the mode selection menu, or switch between modes by appending the mode you would like to switch to."
                        + "\n&e/stopwatch &astart &b[hh:mm:ss] &7- Starts a timer/alarm. If in alarm mode, a duration must be provided using the hh:mm:ss format."
                        + "\n&e/stopwatch &cstop &7- Stops your currently running timer."));

            }

            if (args.length >= 1 && args[0].equalsIgnoreCase(("mode"))) { // mode selection/menu

                if (args.length == 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&9&lMode Selection Menu."
                    + "\n&7--- &cAvailable Modes &7---"
                    + "\n&eTimer &7- Begins a timer, ticking every second. Stop the timer to record your final time."
                    + "\n&eAlarm &7- Set an alarm for the desired amount of time. Will give an audible alert when the time is reached."));
                }
                else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("timer")) {
                        runningMap.put(player.getUniqueId(), false);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&etimer &cmode &7selected."));
                        mode = 1;
                    }
                    else if (args[1].equalsIgnoreCase("alarm")) {
                        runningMap.put(player.getUniqueId(), false);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&ealarm &cmode &7selected."));
                        mode = 2;
                    }
                    else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&cError: Invalid mode selected."));
                    }
                }
            }

            else if (args.length >= 1 && args[0].equalsIgnoreCase("start") && mode == 2) { // if they /stopwatch start and or add a duration in alarm mode

                if (args.length == 1) { // if they dont add a duration
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "You need to enter a duration. Type /stopwatch to see an example."));
                }

                if (args.length == 2 && !runningMap.get(player.getUniqueId())) { // if they provide a duration and dont have a timer running

                    String input = args[1];

                    try {
                        LocalTime inputTime = LocalTime.parse(input, dtf);
                        float ticks = inputTime.toSecondOfDay();
                        timeLeft = inputTime;

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "Timer started for: " + "&a" + inputTime.getHour() + " &7hours &a" + inputTime.getMinute() + " &7minutes &a" + inputTime.getSecond() + " &7seconds."));

                        runningMap.put(player.getUniqueId(), true);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 3);
                        alarm(player, ticks);

                    } catch (DateTimeException e) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: Invalid format."));
                    }

                }

                else if (args.length == 2 && runningMap.get(player.getUniqueId())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&cError: Timer already running."));
                }

            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("start") && mode == 1) { // if they /stopwatch start and or add a duration in timer mode
                if (!runningMap.get(player.getUniqueId())) { // if they dont have a timer running already
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "Timer started, stop using &e/stopwatch &cstop"));
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 3);
                    runningMap.put(player.getUniqueId(), true);
                    timer(player);
                }
                else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&cError: Timer already running."));
                }
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("stop")) { // if they choose to do /stopwatch stop
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

    private void timer(Player player) { // timer runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                timer++;

                if (!runningMap.get(player.getUniqueId())) {

                    timeRan = defTime.plusSeconds(timer);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "Timer ran for: &a" + timeRan.getHour() + " &7hours &a" + timeRan.getMinute() + " &7minutes &a" + timeRan.getSecond() + " &7seconds."));

                    timer = 1;
                    timeRan = timeRan.plusSeconds(-timer);
                    this.cancel();
                    return;
                }

                if (!player.isOnline()) {
                    timer = 1;
                    timeRan = timeRan.plusSeconds(-timer);
                    runningMap.put(player.getUniqueId(), false);
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 );
    }

    private void alarm(Player player, float ticks) { // alarm runnable
        new BukkitRunnable() {
            @Override
            public void run() {

                try {

                    if (timer < ticks) {
                        timer++;
                    }

                    else if (timer == ticks) {
                        timer = 1;
                        runningMap.put(player.getUniqueId(), false);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&4&l*** &e&lYOUR TIMER HAS FINISHED &4&l***\n "));
                        this.cancel();
                        return;
                    }

                    if (!runningMap.get(player.getUniqueId())) {
                        timer = 1;
                        this.cancel();
                        return;
                    }

                    if (!player.isOnline()) {
                        timer = 1;
                        runningMap.put(player.getUniqueId(), false);
                        this.cancel();
                        return;
                    }

                    timeLeft = timeLeft.plusSeconds(-1);

                } catch (IllegalStateException e) {
                    player.sendMessage("Illegal state exception");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 );
    }
}