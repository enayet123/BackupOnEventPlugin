import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

/**
 * Main class called by Bukkit/Spigot/Paper to initialize
 * plugin
 */
public class BackupOnEvent extends JavaPlugin {

    static final String BACKUP_WORLDS = "BackupWorlds";
    private String pluginName;
    String prefix;

    /**
     * Defines a message prefix, prepares necessary
     * files/folders, commands and registers events
     * from Backup Events when plugin has
     * successfully initialized
     */
    @Override
    public void onEnable() {
        this.pluginName = getDescription().getName();
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

        // Disable plugin if redundant
        if (this.pluginIsObsolete())
            Bukkit.getPluginManager().disablePlugin(this);

        // Register event triggers
        else
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
        getConfig().options().header(
                "BackupWorlds lets you toggle which worlds to include in backups\n" +
                "You can enable/disable the events that will trigger a backup to happen from RunBackupOn\n" +
                "repeatInterval runs a backup every time X minutes has passed, 0 means disabled\n" +
                "Messages and announcements can be hidden\n" +
                "onJoin and onQuit will hide the 'x has joined the server' messages\n" +
                "opsOnly restricts the /backup command to ops only\n" +
                "Setting maxInMegaBytes to 0 will provide unlimited disk space\n" +
                "Setting minimumIntervalInMinutes to 0 will allow concurrent backups\n" +
                "AutoUpdate will download the latest version from bukkit.org when an Op joins the server");
        getConfig().addDefault("BackupWorlds.world", true);
        getConfig().addDefault("BackupWorlds.world_nether", true);
        getConfig().addDefault("BackupWorlds.world_the_end", true);
        getConfig().addDefault("BackupWorlds.custom_named_world", false);
        getConfig().addDefault("RunBackupOn.playerJoin", true);
        getConfig().addDefault("RunBackupOn.playerQuit", false);
        getConfig().addDefault("RunBackupOn.lastPlayerQuit", false);
        getConfig().addDefault("RunBackupOn.repeatIntervals.minutes", 0);
        getConfig().addDefault("RunBackupOn.repeatIntervals.whenPlayersAreOnline", true);
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
            Bukkit.getLogger().info(prefix + ChatColor.RED + pluginName + " folder does not exist");
            if (getDataFolder().mkdir())
                Bukkit.getLogger().info(prefix + ChatColor.GREEN + pluginName + " folder has been created");
            else
                Bukkit.getLogger().info(prefix + ChatColor.RED + "Failed to create " + pluginName + " folder");
        }
    }

    private void setupCommands() {
        getCommand("backup").setExecutor(new BackupCommands(this));
    }

    private boolean pluginIsObsolete() {

        // Make sure plugin isn't redundant
        try {
            Set<String> worlds = getConfig().getConfigurationSection(BACKUP_WORLDS).getKeys(false);
            if (!worlds.isEmpty())
                for (String world : worlds)
                    if (getConfig().get(String.format("%s.%s", BACKUP_WORLDS, world)).equals(true))
                        return false;
        } catch (Exception e) {
            Bukkit.getLogger().info(prefix + ChatColor.RED + "Fetching keys returned null!");
        }

        Bukkit.getLogger().info(prefix + ChatColor.RED + "Plugin is obsolete as all backups are disabled!");
        Bukkit.getLogger().info(prefix + ChatColor.RED + "Shutting down plugin...");
        return true;

    }

}
