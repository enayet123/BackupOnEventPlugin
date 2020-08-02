package backuponevent;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class processes the intended files and creates
 * a .zip result. This class is intended to run asynchronously
 * as the Minecraft server runs on a single thread.
 */
public class BackupRunnable implements Runnable {

    private BackupOnEvent plugin;
    private String triggerName;
    private boolean broadcast;

    /**
     * Initializes the runnable with the information required to
     * run a successful backup
     * @param plugin Plugin to get prefix of a message/announcement and disk allocation
     * @param triggerName Name of the entity that triggered event
     * @param broadcast States if an announcement is to be made
     *                  on completion
     */
    private BackupRunnable(BackupOnEvent plugin, String triggerName, boolean broadcast) {
        this.plugin = plugin;
        this.triggerName = triggerName;
        this.broadcast = broadcast;
    }

    /**
     * Runs a backup, looking for the default 'world', 'nether' and
     * 'the end'. Available folders are compressed into a zip.
     */
    public void run() {

        // Ensure folder is available
        this.createFolder();

        // Setup Date and define format
        Date date = new Date();
        DateFormat format = new SimpleDateFormat(String.format(Constants.DATE_FORMAT, triggerName));

        // Define filename
        String file = ((format.format(date).length() <= 255) ? // Zip file name (Ensuring length is < Windows MAX_PATH)
                format.format(date):format.format(date).substring(0, 254)) + Constants.EXT;

        // Attempt to Zip available world folders
        try {
            ZipUtil.ZipDirs(
                Constants.TARGET_DIR, // Destination
                file,
                true, f -> true, // Delete existing?
                getAvailableDirs() // Source folders
            );

            // Announce or log backup success message
            this.log(String.format(Constants.LOG_CREATED_BACKUP, file));

            // Verify storage constraints are met
            if (plugin.getConfig().getInt(Constants.BACKUPSTORAGE_MAX_MB) != 0)
                new FolderVisitor().meetStorageRestriction(plugin.getConfig().getInt(Constants.BACKUPSTORAGE_MAX_MB));

        } catch (IOException e) {

            // Announce or log backup failure message
            this.log(Constants.LOG_FAILED_BACKUP);

            e.printStackTrace();

        }

    }

    /**
     * Run asynchronous backup
     * @param plugin Plugin to get prefix of a message/announcement and disk allocation
     * @param name Name of the entity that triggered event
     */
    static void run(BackupOnEvent plugin, String name) {

        // Run asynchronous backup
        boolean announce = Objects.equals(plugin.getConfig().get(Constants.HIDE_MSG_ANNOUNCE), false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new BackupRunnable(plugin, name, announce));

    }

    private String[] getAvailableDirs() {

        // Map of possible folders
        Map<String, Boolean> sources = new HashMap<>();
        Set<String> worlds = Objects.requireNonNull(plugin.getConfig()
                .getConfigurationSection(Constants.BACKUPWORLDS)).getKeys(false);
        for (String world: worlds)
            sources.put(world, false);

        // Return available folders
        return sources.keySet().stream().filter(x -> new File(x).exists()).toArray(String[]::new);

    }

    private void createFolder() {

        // Create backup folder
        File f = new File(Constants.TARGET_DIR);
        if (!f.exists())
            if (f.mkdir()) {
                Bukkit.getLogger().info(Constants.LOG_CREATED_TARGET_DIR);
            } else {
                Bukkit.getLogger().info(Constants.LOG_FAILED_TARGET_DIR);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }

    }

    private void log(String msg) {

        if (broadcast)
            Bukkit.broadcastMessage(msg);
        else
            Bukkit.getLogger().info(msg);

    }

}
