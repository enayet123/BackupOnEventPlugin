import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.io.File;
import java.util.logging.Logger;

/**
 * Listens for events that can trigger a backup to
 * take place. Listeners are enabled depending upon
 * config in config.yml.
 */
public class BackupEvents implements Listener {

    BackupOnEvent plugin;
    String prefix;
    Logger logger;

    /**
     * Initializes event listener class
     * @param plugin Refers back to the main class, BackupOnEvent
     * @param logger Logger used to output process information
     */
    public BackupEvents(BackupOnEvent plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        prefix = plugin.prefix;
    }

    /**
     * This event triggers when a player joins a server
     * @param e The event related to a player joining
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        // Hide Join message if required
        if (plugin.getConfig().get("HideMessage.onJoin").equals(true)) e.setJoinMessage("");

        // If event is disabled, return immediately
        if (plugin.getConfig().get("Player.onJoin").equals(false)) return;

        // Notify player of backup if required
        Player p = e.getPlayer();
        if (plugin.getConfig().get("HideMessage.privatelyOnBackup").equals(false))
            p.sendMessage(
                    String.format("%sWelcome %s%s! Preparing a backup...",
                            ChatColor.GREEN,
                            ChatColor.GOLD + p.getDisplayName(),
                            ChatColor.GREEN
                    )
            );

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

    private void createFolder() {
        File f = new File("world_backups");
        if (!f.exists()) {
            if (f.mkdir()) {
                logger.info(prefix + "Created directory 'world_backups'");
            } else {
                logger.info(prefix + "Failed to create directory 'world_backups', shutting down plugin!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    private void backup(Player p) {

        // Notify all players on server if required
        if (plugin.getConfig().get("HideMessage.publiclyOnBackup").equals(false))
            Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + "Attempting to backup...");

        // Backup
        this.createFolder();
        String playerName = p.getDisplayName();
        String worldName = Bukkit.getWorlds().get(0).getName();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                new BackupRunnable(
                        prefix,
                        playerName,
                        worldName,
                        plugin.getConfig().get("HideMessage.publiclyOnBackup").equals(false)
                )
        );

    }

}
