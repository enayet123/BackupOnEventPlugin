import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Main class called by Bukkit/Spigot/Paper to initialize
 * plugin
 */
public class BackupOnEvent extends JavaPlugin {

    String prefix;

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

        // Setup commands
        this.setupCommands();

        // Setup config file
        this.setupConfigFile();

        // Register event triggers
        this.getServer().getPluginManager().registerEvents(new BackupEvents(this), this);

    }

    /**
     * Announces deactivation when plugin is disabled
     */
    @Override
    public void onDisable() {
        Bukkit.getLogger().info(prefix + ChatColor.RED + "Deactivated");
    }

    /**
     * Returns the file which contains the plugin
     * @return File containing plugin
     */
    File getPluginFile() { return this.getFile(); }

    private void setupConfigFile() {

        this.createFolder();
        getConfig().options().copyDefaults(true);
        getConfig().options().header("You can enable/disable the events that will trigger a backup to happen\n" +
                "intervalMinutes runs a backup every time X minutes has passed, 0 means disabled\n" +
                "Messages and announcements can be hidden\n" +
                "onJoin and onQuit will hide the 'x has joined the server' messages\n" +
                "opsOnly restricts the /backup command to ops only\n" +
                "Setting maxInMegaBytes to 0 will provide unlimited disk space\n" +
                "Setting minimumIntervalInMinutes to 0 will allow concurrent backups\n" +
                "AutoUpdate will download the latest version from bukkit.org when an Op joins the server");
        getConfig().addDefault("RunBackupOn.playerJoin", true);
        getConfig().addDefault("RunBackupOn.playerQuit", false);
        getConfig().addDefault("RunBackupOn.lastPlayerQuit", false);
        getConfig().addDefault("RunBackupOn.intervalMinutes", 0);
        getConfig().addDefault("HideMessage.onJoin", false);
        getConfig().addDefault("HideMessage.onQuit", false);
        getConfig().addDefault("HideMessage.backupAnnouncement", false);
        getConfig().addDefault("BackupCommand.opsOnly", true);
        getConfig().addDefault("BackupStorage.maxInMegaBytes", 1024);
        getConfig().addDefault("BackupStorage.minimumIntervalInMinutes", 1);
        getConfig().addDefault("AutoUpdate.enabled", true);
        saveConfig();

    }

    private void createFolder() {

        // Create folder for data
        if (!getDataFolder().exists()) {
            Bukkit.getLogger().info(prefix + ChatColor.RED + "BackupOnEvent folder does not exist");
            if (getDataFolder().mkdir())
                Bukkit.getLogger().info(prefix + ChatColor.GREEN + "BackupOnEvent folder has been created");
            else
                Bukkit.getLogger().info(prefix + ChatColor.RED + "Failed to create BackupOnEvent folder");
        }

    }

    private void setupCommands() {
        getCommand("backup").setExecutor(new BackupCommands(this));
    }

}
