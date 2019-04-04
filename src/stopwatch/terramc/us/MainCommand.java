package stopwatch.terramc.us;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import stopwatch.terramc.us.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.UUID;


public class MainCommand implements CommandExecutor {

    int duration;
    int durRemaining;

    public HashMap<UUID, Integer> taskMap = new HashMap<>();
    public HashMap<UUID, Boolean> runningMap = new HashMap<>();

    private final Main plugin;

    public MainCommand(Main pluginn) { // registering our command
        plugin = pluginn;
        plugin.getCommand("stopwatch").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) { // if our command user is a player

            Player player = (Player) sender;

            if (runningMap.get(player.getUniqueId()) == null) { // make sure our hashmap isnt returning null values!!
                runningMap.put(player.getUniqueId(), false);
            }

            if (args.length == 0) { // Do this if they provide no arguments

                if (runningMap.get(player.getUniqueId()) == true) {
                    int hours = 0;
                    int minutes = 0;
                    int seconds = 0;

                    while (durRemaining > 0) {
                        if (durRemaining >= 3600) {
                            durRemaining = durRemaining - 3600;
                            hours++;
                        } else if (durRemaining >= 60) {
                            durRemaining = durRemaining - 60;
                            minutes++;
                        } else if (durRemaining >= 1) {
                            durRemaining = durRemaining - 1;
                            seconds++;
                        }
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&9Time remaining: &a" + hours + " &ehours &a" + minutes + " &eminutes &a" + seconds + " &eseconds."));
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
                } else if (args.length == 2 && args[1].length() >= 5 && args[1].length() <= 8 && runningMap.get(player.getUniqueId()) != true) { // if they do add a duration
                    String input = args[1];
                    String[] inputSplit = input.split(":");

                    //if(){git test}
                    //else if () {herlo weurl}
                    //else if () {ahh}
                    //else {}

                    int hours;
                    int minutes;
                    int seconds;

                    try {
                        hours = Integer.parseInt(inputSplit[0]);
                        minutes = Integer.parseInt(inputSplit[1]);
                        seconds = Integer.parseInt(inputSplit[2]);

                        hours = hours * plugin.hour;
                        minutes = minutes * plugin.minute;
                        seconds = seconds * plugin.second;

                        duration = hours + minutes + seconds;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "Timer started for: " + "&a" + hours / 3600000 + " &7hours &a" + minutes / 60000 + " &7minutes &a" + seconds / 1000 + " &7seconds."));

                        runningMap.put(player.getUniqueId(), true);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 3);
                        timer(player);
                    } catch (NumberFormatException n) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: You have entered an invalid character or duration."));
                    }

                } else if (args.length >= 1 && runningMap.get(player.getUniqueId()) == true) { // If they have a timer already goin'
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: Timer already running."));
                } else if (args.length == 2 && args[1].length() != 5) { // If they stupid as fuck
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: Invalid format."));
                } else if (args.length >= 3) { // If they stupid as fuck
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&cError: You have entered too many arguments. Type /stopwatch for an example."));
                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("stop")) { // if they choose to do /stopwatch stop
                if (runningMap.get(player.getUniqueId()) != false) { // If runningMap doesnt return false, they must have a timer running... stop it
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

    public void timer(Player player) { // Timer runnable
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    taskMap.put(player.getUniqueId(), getTaskId());
                    runningMap.put(player.getUniqueId(), true);

                    int counter = 1;
                    int dur = duration / 1000;

                    while (counter < dur) {

                        counter++;
                        durRemaining = dur - counter;

                        if (runningMap.get(player.getUniqueId()) == false) {
                            durRemaining = 0;
                            this.cancel();
                            return;
                        }

                        if (player.isOnline() == false) {
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