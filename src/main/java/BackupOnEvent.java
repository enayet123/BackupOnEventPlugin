import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Main class called by Bukkit/Spigot/Paper to initialize
 * plugin
 */
public class BackupOnEvent extends JavaPlugin {

    /**
     * Prepares necessary files/folders, commands
     * and registers events from Backup Events when
     * plugin has successfully initialized
     */
    @Override
    public void onEnable() {
        // State plugin is active
        Bukkit.getLogger().info(Constants.STATUS_ACTIVATED);

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
        Bukkit.getLogger().info(Constants.STATUS_DEACTIVATED);
    }

    /**
     * Returns the file which contains the plugin
     * @return File containing plugin
     */
    File getPluginFile() { return this.getFile(); }

    private void setupConfigFile() {
        List<World> worlds = Bukkit.getWorlds();

        this.createFolder();
        getConfig().options().copyDefaults(true);
        getConfig().options().header(Constants.CONFIG_HEADER);
        for (World world : worlds)
            getConfig().addDefault(String.format("%s.%s", Constants.BACKUPWORLDS, world.getName()), true);
        getConfig().addDefault(Constants.BACKUPWORLDS_PLUGINS, false);
        getConfig().addDefault(Constants.BACKUPWORLDS_CUSTOM, false);
        getConfig().addDefault(Constants.BACKUPEVENT_JOIN, true);
        getConfig().addDefault(Constants.BACKUPEVENT_QUIT, false);
        getConfig().addDefault(Constants.BACKUPEVENT_LAST_TO_QUIT, false);
        getConfig().addDefault(Constants.BACKUPEVENT_REPEAT_MINUTES, 0);
        getConfig().addDefault(Constants.BACKUPEVENT_REPEAT_WHEN_ONLINE, true);
        getConfig().addDefault(Constants.HIDE_MSG_JOIN, false);
        getConfig().addDefault(Constants.HIDE_MSG_QUIT, false);
        getConfig().addDefault(Constants.HIDE_MSG_ANNOUNCE, false);
        getConfig().addDefault(Constants.BACKUPCMD_OPS_ONLY, true);
        getConfig().addDefault(Constants.BACKUPSTORAGE_MAX_MB, 1024);
        getConfig().addDefault(Constants.BACKUPSTORAGE_MIN_INTERVAL_MINUTES, 1);
        getConfig().addDefault(Constants.AUTO_UPDATE_ENABLED, true);
        saveConfig();

    }

    private void createFolder() {
        // Create folder for data
        if (!getDataFolder().exists()) {
            Bukkit.getLogger().info(Constants.LOG_FOLDER_NOT_EXIST);
            Bukkit.getLogger().info((getDataFolder().mkdir() ?
                    Constants.LOG_FOLDER_CREATED : Constants.LOG_FOLDER_CREAT_FAILED));
        }
    }

    private void setupCommands() {
        Objects.requireNonNull(getCommand(Constants.CMD_BACKUP)).setExecutor(new BackupCommands(this));
    }

    private boolean pluginIsObsolete() {

        // Make sure plugin isn't redundant
        try {
            Set<String> worlds = Objects.requireNonNull(getConfig()
                    .getConfigurationSection(Constants.BACKUPWORLDS)).getKeys(false);
            if (!worlds.isEmpty())
                for (String world : worlds)
                    if (Objects.equals(getConfig().get(String.format("%s.%s", Constants.BACKUPWORLDS, world)), true))
                        return false;
        } catch (Exception e) {
            Bukkit.getLogger().severe(Constants.LOG_KEY_RETURNED_NULL);
        }

        Bukkit.getLogger().info(Constants.LOG_OBSELETE);
        Bukkit.getLogger().info(Constants.LOG_SHUTTING_DOWN);
        return true;

    }

}
