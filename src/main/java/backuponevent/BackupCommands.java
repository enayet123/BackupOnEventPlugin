package backuponevent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Command executor class used to listen for
 * commands registered for this plugin
 */
public class BackupCommands implements CommandExecutor {

    private BackupOnEvent plugin;
    private int intervalTaskId = -1;

    /**
     * Initializes command executor
     * @param plugin Refers back to the main class, BackupOnEvent
     */
    BackupCommands(BackupOnEvent plugin) {
        this.plugin = plugin;
        this.setupInterval();
    }

    /**
     * Listens for registered commands
     * @param commandSender The entity executing the command
     * @param cmd Which command is being executed
     * @param label The command alias
     * @param args Additional arguments provided
     * @return Reverts to usage if false or apply expected changes
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {

        // If backup command, execute
        if (cmd.getName().equalsIgnoreCase(Constants.CMD_BACKUP))
            onCommandBackup(commandSender);

        return true;

    }

    private void onCommandBackup(CommandSender commandSender) {

        // TODO: Implement config from command line

        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;

            // Check if op status required
            if (Objects.equals(plugin.getConfig().get(Constants.BACKUPCMD_OPS_ONLY), true) && !commandSender.isOp()) {
                p.sendMessage(Constants.MSG_REQUIRE_OP);
                return;
            }

            // Backup with player as reason for trigger
            BackupRunnable.run(plugin, p.getName());

        } else if (commandSender instanceof ConsoleCommandSender) {
            // Backup with server console as reason for trigger
            BackupRunnable.run(plugin, Constants.CMD_CONSOLE);
        }

    }

    private void setupInterval() {

        int repeatInterval = plugin.getConfig().getInt(Constants.BACKUPEVENT_REPEAT_MINUTES);
        Bukkit.getLogger().info(String.format(Constants.LOG_INTERVAL_BACKUP_TIME, repeatInterval));
        long interval = (repeatInterval * 60 * 20);

        // Kill current interval task if running
        if (intervalTaskId != -1) Bukkit.getScheduler().cancelTask(intervalTaskId);

        // If interval is being disabled
        if (interval < 1) intervalTaskId = -1;

        // Interval is provided
        else
            intervalTaskId = Bukkit.getScheduler()
                    .runTaskTimer(plugin, new BackupInterval(plugin), interval, interval).getTaskId();

    }

}
