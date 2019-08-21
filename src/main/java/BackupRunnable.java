import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class processes the intended files and creates
 * a .zip result. This class is intended to run asynchronously
 * as the Minecraft server runs on a single thread.
 */
public class BackupRunnable implements Runnable {

    private String prefix;
    private String playerName;
    private String worldName;
    private boolean broadcast;

    /**
     * Initializes the runnable with the information required to
     * run a successful backup
     * @param prefix Prefix of a message/announcement
     * @param playerName Name of the player that triggered event
     * @param worldName Name of the world (From server.properties)
     * @param broadcast States if an announcement is to be made
     *                  on completion
     */
    public BackupRunnable(String prefix, String playerName, String worldName, boolean broadcast) {
        this.prefix = prefix;
        this.playerName = playerName;
        this.worldName = worldName;
        this.broadcast = broadcast;
    }

    /**
     * Runs a backup, looking for the default 'world', 'nether' and
     * 'the end'. Available folders are compressed into a zip.
     */
    public void run() {

        // Setup Date and define format
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd_hh'h'mm'm'-'" + playerName + "-" + worldName + "'");

        // Attempt to Zip available world folders
        try {

            ZipUtil.ZipDirs(
                    "world_backups", // Destination
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

        } catch (IOException e) {

            // Announce or log backup failure message
            if (broadcast)
                Bukkit.broadcastMessage(prefix + ChatColor.RED + "Failed to backup world! Please check server logs!");
            else
                Bukkit.getLogger().info(prefix + ChatColor.RED + "Failed to backup world! Please check server logs!");
            e.printStackTrace();

        }

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

}
