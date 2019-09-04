import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.time.Instant;
import java.util.logging.Logger;

/**
 * Listens for events that can trigger a backup to
 * take place. Listeners are enabled depending upon
 * config in config.yml.
 */
public class BackupEvents implements Listener {

    private BackupOnEvent plugin;
    private String prefix;
    private Logger logger;
    private String worldName;
    private Instant lastBackup;
    private boolean updateQueued = false;

    /**
     * Initializes event listener class
     * @param plugin Refers back to the main class, BackupOnEvent
     * @param logger Logger used to output process information
     */
    public BackupEvents(BackupOnEvent plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.prefix = plugin.prefix;
        this.worldName = Bukkit.getWorlds().get(0).getName();
        this.lastBackup = Instant.EPOCH;
    }

    /**
     * This event triggers when a player joins a server
     * @param e The event related to a player joining
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new UpdateRunnable(plugin, e.getPlayer(), this));

        // Hide Join message if required
        if (plugin.getConfig().get("HideMessage.onJoin").equals(true)) e.setJoinMessage("");

        // If event is disabled, return immediately
        if (plugin.getConfig().get("Player.onJoin").equals(false)) return;

        // Run backup
        this.backup(e.getPlayer());

    }

    /**
     * This event triggers when a player leaves a server
     * @param e The event related to a player leaving
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        // Hide Quit message if required
        if (plugin.getConfig().get("HideMessage.onQuit").equals(true)) e.setQuitMessage("");

        // If event is disabled, return immediately
        if (plugin.getConfig().get("Player.onQuit").equals(false)) return;

        this.backup(e.getPlayer());

    }

    /**
     * Returns if an update is queued
     * @return is update queued
     */
    public boolean getUpdateQueued() { return updateQueued; }

    /**
     * Sets updateQueued to true, this can only be set to false through a reload/restart
     */
    public void setUpdateQueued() { updateQueued = true; }

    private void createFolder() {

        // Create backup folder
        File f = new File(worldName + "_backups");
        if (!f.exists())
            if (f.mkdir()) {
                logger.info(prefix + "Created directory '" + worldName + "_backups'");
            } else {
                logger.info(prefix + "Failed to create directory '" + worldName + "_backups', shutting down plugin!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }

    }

    private void backup(Player p) {

        // If minimum interval requirement not met
        if (!minimumIntervalPassed()) return;

        // Notify all players on server if required
        if (plugin.getConfig().get("HideMessage.backupAnnouncement").equals(false))
            Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + "Attempting to backup...");

        // Backup
        BackupRunnable.run(plugin, p.getDisplayName(), worldName);
        lastBackup = Instant.now();

    }

    private boolean minimumIntervalPassed() {

        // If no minimum set, return true
        int min;
        if ((min = (plugin.getConfig().getInt("BackupStorage.minimumIntervalInMinutes") * 60)) == 0) return true;

        // Check if minimum interval has passed
        return ((Instant.now().getEpochSecond() - min) > lastBackup.getEpochSecond());

    }

}
