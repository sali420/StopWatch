package stopwatch.terramc.us;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    final String prefix = "&c&lStop&6&lWatch &8» &7";

    @Override
    public void onEnable() {
        new MainCommand(this); // initializing our MainCommand
    }

    @Override
    public void onDisable() {
        getLogger().info("- Bye bye :(");
    }

}
