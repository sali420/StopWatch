package stopwatch.terramc.us;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {

    private final Main plugin;
    SPlayer sPlayer = new SPlayer();

    public PlayTimeCommand(Main pluginn) {
        plugin = pluginn;
        plugin.getCommand("playtime").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            sPlayer.setPlayTime(player.getTicksLived());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.prefix + "&3Total playtime: &a" + (sPlayer.getPlayTime().getDayOfMonth() - 1)
                    + " &7days&6, &a" + sPlayer.getPlayTime().getHour()
                    + " &7hours&6, &a" + sPlayer.getPlayTime().getMinute()
                    + " &7minutes&6, &a" + sPlayer.getPlayTime().getSecond()
                    + " &7seconds&6."));
        } else {
            sender.sendMessage("yo!");
        }
        return true;
    }

}
