import net.gravitydevelopment.updater.Updater;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Checks for plugin update from bukkit.org
 * Auto updates can backupEvents disabled from the config.yml file
 */
public class UpdateRunnable implements Runnable {

    private BackupOnEvent plugin;
    private Player player;
    private BackupEvents backupEvents;
    private final String msg;

    /**
     * Initializes the runnable with the information required to
     * run a successful update
     * @param plugin Plugin to get prefix of a message/Use on updater
     * @param player Player triggering the event
     * @param backupEvents Reference back to calling class used to
     *                     update boolean value (Prevent multi downloads)
     */
    UpdateRunnable(BackupOnEvent plugin, Player player, BackupEvents backupEvents) {
        this.plugin = plugin;
        this.player = player;
        this.backupEvents = backupEvents;
        this.msg = plugin.prefix + ChatColor.GREEN + "Update available! Type " + ChatColor.YELLOW + "/reload confirm";
    }

    /**
     * Runs an update as long as config allows, player is Op and
     * there is not already an update queued.
     */
    @Override
    public void run() {

        // If not allowed by config or player is not Op, return
        if (!plugin.getConfig().getBoolean("AutoUpdate.enabled") || !player.isOp()) return;

        // Is there an update already queued, message and return
        if (backupEvents.isUpdateQueued()) { player.sendMessage(msg); return; }

        // Else check for update
        Updater updater = new Updater(plugin, 336739, plugin.getPluginFile(), Updater.UpdateType.DEFAULT, true);

        // Get, message and update result
        if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
            player.sendMessage(msg);
            backupEvents.setUpdateQueued();
        }

    }

}
