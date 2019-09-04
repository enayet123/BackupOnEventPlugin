import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
    private String prefix;
    private String triggerName;
    private String worldName;
    private boolean broadcast;

    /**
     * Initializes the runnable with the information required to
     * run a successful backup
     * @param plugin Plugin to get prefix of a message/announcement and disk allocation
     * @param triggerName Name of the entity that triggered event
     * @param worldName Name of the world (From server.properties)
     * @param broadcast States if an announcement is to be made
     *                  on completion
     */
    public BackupRunnable(BackupOnEvent plugin, String triggerName, String worldName, boolean broadcast) {
        this.prefix = plugin.prefix;
        this.plugin = plugin;
        this.triggerName = triggerName;
        this.worldName = worldName;
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
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'-'" + triggerName + "-" + worldName + "'");

        // Attempt to Zip available world folders
        try {

            ZipUtil.ZipDirs(
                worldName + "_backups", // Destination
                ((format.format(date).length() <= 255) ? // Zip file name (Ensuring length is < Windows MAX_PATH)
                        format.format(date):format.format(date).substring(0, 254)) + ".zip",
                true, f -> true, // Delete existing?
                getAvailableDirs() // Source folders
            );

            // Announce or log backup success message
            if (broadcast)
                Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "Created backup " + format.format(date) + ".zip");
            else
                Bukkit.getLogger().info(prefix + ChatColor.GREEN + "Created backup " + format.format(date) + ".zip");

            // Verify storage constraints are met
            if (plugin.getConfig().getInt("BackupStorage.maxInMegaBytes") != 0)
                new FolderVisitor(prefix,worldName + "_backups").meetStorageRestriction(
                        plugin.getConfig().getInt("BackupStorage.maxInMegaBytes")
                );

        } catch (IOException e) {

            // Announce or log backup failure message
            if (broadcast)
                Bukkit.broadcastMessage(prefix + ChatColor.RED + "Failed to backup world! Please check server logs!");
            else
                Bukkit.getLogger().info(prefix + ChatColor.RED + "Failed to backup world! Please check server logs!");
            e.printStackTrace();

        }

    }

    /**
     * Run asynchronous backup
     * @param plugin Plugin to get prefix of a message/announcement and disk allocation
     * @param name Name of the entity that triggered event
     * @param world Name of the world (From server.properties)
     */
    public static void run(BackupOnEvent plugin, String name, String world) {

        // Run asynchronous backup
        boolean announce = plugin.getConfig().get("HideMessage.backupAnnouncement").equals(false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new BackupRunnable(plugin, name, world, announce));

    }

    private String[] getAvailableDirs() {

        // Map of possible folders
        Map<String, Boolean> sources = new HashMap<String, Boolean>() {{
            put(worldName, false);
            put(worldName + "_nether", false);
            put(worldName + "_the_end", false);
        }};

        // Return available folders
        return sources.keySet().stream().filter(x -> new File(x).exists()).toArray(String[]::new);

    }

    private Path[] getAvailableDirsPath() {

        // Map of possible folders
        Map<String, Boolean> sources = new HashMap<String, Boolean>() {{
            put(worldName, false);
            put(worldName + "_nether", false);
            put(worldName + "_the_end", false);
        }};

        // Return available folders
        return sources.keySet().stream().filter(x -> new File(x).exists()).map(x -> Paths.get(x)).toArray(Path[]::new);

    }

    private void createFolder() {

        // Create backup folder
        File f = new File(worldName + "_backups");
        if (!f.exists())
            if (f.mkdir()) {
                Bukkit.getLogger().info(prefix + "Created directory '" + worldName + "_backups'");
            } else {
                Bukkit.getLogger().info(prefix + "Failed to create directory '" + worldName +
                        "_backups', shutting down plugin!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }

    }

}
