import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class called by Bukkit/Spigot/Paper to initialize
 * plugin
 */
public class BackupOnEvent extends JavaPlugin {

    protected String prefix;

    /**
     * Defines a message prefix, prepares necessary
     * files/folders and registers events from Backup Events
     * when plugin has successfully initialized
     */
    @Override
    public void onEnable() {

        // Announcement prefix
        prefix = String.format("%s[%s%s%s]%s ",
                ChatColor.WHITE,
                ChatColor.BLUE,
                this.getDescription().getName(),
                ChatColor.WHITE,
                ChatColor.RESET
        );

        // State plugin is active
        Bukkit.getLogger().info(prefix + ChatColor.GREEN + "Activated");

        // Setup config file
        this.createFolder();
        getConfig().options().copyDefaults(true);
        getConfig().options().header("You can enable/disable the events that will trigger a backup to happen\n" +
                "Messages and announcements can be hidden\n" +
                "onJoin and onQuit will hide the 'x has joined the server' messages\n" +
                "Setting maxInMegaBytes to 0 will provide unlimited space\n" +
                "Setting minimumIntervalInMinutes to 0 will allow back to back backups");
        getConfig().addDefault("Player.onJoin", true);
        getConfig().addDefault("Player.onQuit", false);
        getConfig().addDefault("HideMessage.onJoin", false);
        getConfig().addDefault("HideMessage.onQuit", false);
        getConfig().addDefault("HideMessage.backupAnnouncement", false);
        getConfig().addDefault("BackupStorage.maxInMegaBytes", 1024);
        getConfig().addDefault("BackupStorage.minimumIntervalInMinutes", 1);
        saveConfig();

        // Register event triggers
        this.getServer().getPluginManager().registerEvents(new BackupEvents(this, Bukkit.getLogger()), this);

    }

    /**
     * Announces deactivation when plugin is disabled
     */
    @Override
    public void onDisable() {
        Bukkit.getLogger().info(prefix + ChatColor.RED + "Deactivated");
    }

    private void createFolder() {

        // Create folder for data
        if (!getDataFolder().exists()) {
            Bukkit.getLogger().info(prefix + ChatColor.RED + "BackupOnEvent folder does not exist");
            getDataFolder().mkdir();
            Bukkit.getLogger().info(prefix + ChatColor.GREEN + "BackupOnEvent folder has been created");
        }

    }



}
