import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor class used to listen for
 * commands registered for this plugin
 */
public class BackupCommands implements CommandExecutor {

    private BackupOnEvent plugin;

    /**
     * Initializes command executor
     * @param plugin Refers back to the main class, BackupOnEvent
     */
    public BackupCommands(BackupOnEvent plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens for registered commands
     * @param commandSender The entity executing the command
     * @param cmd Which command is being executed
     * @param label The command alias
     * @param args Additional arguments provided
     * @return Reverts to usage if false or apply expected changes
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {

        // If backup command, execute
        if (cmd.getName().equalsIgnoreCase("backup"))
            onCommandBackup(commandSender);

        return true;

    }

    private void onCommandBackup(CommandSender commandSender) {

        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;

            // Check if op status required
            if (plugin.getConfig().get("Player.mustBeOpToUseCommand").equals(true) && !commandSender.isOp()) {
                p.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command."
                 + " Please contact the server administrators if you believe that this is in error.");
                return;
            }

            // Backup with player as reason for trigger
            runAsyncBackup(p.getName());

        } else if (commandSender instanceof ConsoleCommandSender) {
            // Backup with server console as reason for trigger
            runAsyncBackup("CONSOLE");
        }

    }

    private void runAsyncBackup(String name) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                new BackupRunnable(
                        plugin,
                        name,
                        Bukkit.getWorlds().get(0).getName(),
                        plugin.getConfig().get("HideMessage.backupAnnouncement").equals(false)
                )
        );

    }

}