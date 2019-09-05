import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;

import static org.bukkit.Bukkit.*;

/**
 * Listens for events that can trigger a backup to
 * take place. Listeners are enabled depending upon
 * config in config.yml.
 */
public class BackupEvents implements Listener {

    private BackupOnEvent plugin;
    private String prefix;
    private String worldName = getWorlds().get(0).getName();
    private Instant lastBackup = Instant.EPOCH;
    private boolean updateQueued = false;

    /**
     * Initializes event listener class
     * @param plugin Refers back to the main class, BackupOnEvent
     */
    BackupEvents(BackupOnEvent plugin) {
        this.plugin = plugin;
        this.prefix = plugin.prefix;
    }

    /**
     * This event triggers when a player joins a server
     * @param e The event related to a player joining
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        getScheduler().runTaskAsynchronously(plugin, new UpdateRunnable(plugin, e.getPlayer(), this));

        // Hide Join message if required
        if (plugin.getConfig().get("HideMessage.onJoin").equals(true)) e.setJoinMessage("");

        // If event is disabled, return immediately
        if (plugin.getConfig().get("RunBackupOn.playerJoin").equals(false)) return;

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

        // Check if player was last player to leave and this event type is enabled
        if (getOnlinePlayers().size() == 0 && plugin.getConfig().get("RunBackupOn.lastPlayerQuit").equals(true))
            this.backup(e.getPlayer());

        // Else if quit backups are enabled, run backup
        else if (plugin.getConfig().get("RunBackupOn.playerQuit").equals(true))
            this.backup(e.getPlayer());

    }

    /**
     * Returns if an update is queued
     * @return is update queued
     */
    boolean getUpdateQueued() { return updateQueued; }

    /**
     * Sets updateQueued to true, this can only be set to false through a reload/restart
     */
    void setUpdateQueued() { updateQueued = true; }

    private void backup(Player p) {

        // If minimum interval requirement not met
        if (!minimumIntervalPassed()) return;

        // Notify all players on server if required
        if (plugin.getConfig().get("HideMessage.backupAnnouncement").equals(false))
            broadcastMessage(prefix + ChatColor.YELLOW + "Attempting to backup...");

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
