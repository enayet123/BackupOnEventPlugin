import org.bukkit.Bukkit;

/**
 * Creates backups every time its called by a TaskTimer
 */
public class BackupInterval implements Runnable {

    private BackupOnEvent plugin;

    /**
     * Sets up backup interval runnable
     * @param plugin Plugin used for backing up
     */
    BackupInterval(BackupOnEvent plugin) { this.plugin = plugin; }

    /**
     * Runs a backup
     */
    @Override
    public void run() { BackupRunnable.run(plugin, "TIMER", Bukkit.getWorlds().get(0).getName()); }

}
