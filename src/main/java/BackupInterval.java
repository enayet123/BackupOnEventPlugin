import org.bukkit.Bukkit;

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
        if (plugin.getConfig().getBoolean(Constants.BACKUPEVENT_REPEAT_WHEN_ONLINE))
            // If no one is online, return early
            if (Bukkit.getOnlinePlayers().size() == 0) {
                if (!lastWasSkipped)
                    Bukkit.getLogger().info(Constants.LOG_NO_PLAYERS_ONLINE);
                lastWasSkipped = true;
                return;
            }

        BackupRunnable.run(plugin, Constants.RUNNABLE_INTERVAL);
        lastWasSkipped = false;

    }

}
