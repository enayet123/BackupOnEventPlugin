import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Creates backups every time its called by a TaskTimer
 */
public class BackupInterval implements Runnable {

    private BackupOnEvent plugin;
    private boolean lastWasSkipped = false; // Prevents log spamming

    /**
     * Sets up backup interval runnable
     * @param plugin Plugin used for backing up
     */
    BackupInterval(BackupOnEvent plugin) { this.plugin = plugin; }

    /**
     * Runs a backup
     */
    @Override
    public void run() {

        // If interval requires a player to be online
        if (plugin.getConfig().getBoolean("RunBackupOn.repeatIntervals.whenPlayersAreOnline"))
            // If no one is online, return early
            if (Bukkit.getOnlinePlayers().size() == 0) {
                if (!lastWasSkipped)
                    Bukkit.getLogger().info(plugin.prefix + ChatColor.YELLOW + "No players are online, skipping backup");
                lastWasSkipped = true;
                return;
            }

        BackupRunnable.run(plugin, "INTERVAL", Bukkit.getWorlds().get(0).getName());
        lastWasSkipped = false;

    }

}
