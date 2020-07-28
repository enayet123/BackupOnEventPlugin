import org.bukkit.ChatColor;

class Constants {

    // Global constants
    private static final String PLUGIN_NAME = "BackupOnEvent";
    private static final String PREFIX = String.format("%s[%s%s%s]%s ",
            ChatColor.WHITE, ChatColor.BLUE, PLUGIN_NAME, ChatColor.WHITE, ChatColor.RESET);

    // Plugin status messages
    static final String STATUS_ACTIVATED = PREFIX + ChatColor.GREEN + "Activated";
    static final String STATUS_DEACTIVATED = PREFIX + ChatColor.RED + "Deactivated";

    // Properties for config.yml
    static final String CONFIG_HEADER = "BackupWorlds lets you toggle which worlds to include in backups\n" +
            "You can enable/disable the events that will trigger a backup to happen from RunBackupOn\n" +
            "repeatInterval runs a backup every time X minutes has passed, 0 means disabled\n" +
            "Messages and announcements can be hidden\n" +
            "onJoin and onQuit will hide the 'x has joined the server' messages\n" +
            "opsOnly restricts the /backup command to ops only\n" +
            "Setting maxInMegaBytes to 0 will provide unlimited disk space\n" +
            "Setting minimumIntervalInMinutes to 0 will allow concurrent backups\n" +
            "AutoUpdate will download the latest version from bukkit.org when an Op joins the server";

    static final String BACKUPWORLDS = "BackupWorlds";
    static final String BACKUPWORLDS_CUSTOM = BACKUPWORLDS + ".custom_named_world";

    private static final String BACKUPEVENT = "RunBackupOn";
    private static final String REPEAT = ".repeatIntervals";
    static final String BACKUPEVENT_JOIN = BACKUPEVENT + ".playerJoin";
    static final String BACKUPEVENT_QUIT = BACKUPEVENT + ".playerQuit";
    static final String BACKUPEVENT_LAST_TO_QUIT = BACKUPEVENT + ".lastPlayerQuit";
    static final String BACKUPEVENT_REPEAT_MINUTES = BACKUPEVENT + REPEAT + ".minutes";
    static final String BACKUPEVENT_REPEAT_WHEN_ONLINE = BACKUPEVENT + REPEAT + ".whenPlayersAreOnline";

    private static final String HIDE_MSG = "HideMessage";
    static final String HIDE_MSG_JOIN = HIDE_MSG + ".onJoin";
    static final String HIDE_MSG_QUIT = HIDE_MSG + ".onQuit";
    static final String HIDE_MSG_ANNOUNCE = HIDE_MSG + ".backupAnnouncement";

    static final String BACKUPCMD_OPS_ONLY = "BackupCommand.opsOnly";

    private static final String BACKUPSTORAGE = "BackupStorage";
    static final String BACKUPSTORAGE_MAX_MB = BACKUPSTORAGE + ".maxInMegaBytes";
    static final String BACKUPSTORAGE_MIN_INTERVAL_MINUTES = BACKUPSTORAGE + ".minimumIntervalInMinutes";

    static final String AUTO_UPDATE_ENABLED = "AutoUpdate.enabled";

    // Plugin commands
    static final String CMD_BACKUP = "backup";
    static final String CMD_CONSOLE = "CONSOLE";

    // Runnable task names
    static final String RUNNABLE_INTERVAL = "INTERVAL";

    // File naming and folders
    static final String DATE_FORMAT = "yyyy-MM-dd_HH'h'mm'm'-'%s'";
    static final String TARGET_DIR = "backups";
    static final String EXT = ".zip";
    static final String SESSION_LOCK = "session.lock";

    // Log messages
    private static final String WARNING = ChatColor.YELLOW + "WARNING" + ChatColor.RESET + ": ";
    static final String LOG_FOLDER_NOT_EXIST = PREFIX + ChatColor.RED + PLUGIN_NAME + " folder does not exist";
    static final String LOG_FOLDER_CREATED = PREFIX + ChatColor.GREEN + PLUGIN_NAME + " folder has been created";
    static final String LOG_FOLDER_CREAT_FAILED = PREFIX + ChatColor.RED + "Failed to create " + PLUGIN_NAME + " folder";
    static final String LOG_KEY_RETURNED_NULL = PREFIX + ChatColor.RED + "Fetching keys returned null!";
    static final String LOG_OBSELETE = PREFIX + ChatColor.RED + "Plugin is obsolete as all backups are disabled!";
    static final String LOG_SHUTTING_DOWN = PREFIX + ChatColor.RED + "Shutting down plugin...";
    static final String LOG_NO_PLAYERS_ONLINE = PREFIX + ChatColor.YELLOW + "No players are online, skipping backup";
    static final String LOG_CREATED_BACKUP = PREFIX + ChatColor.GREEN + "Created backup %s";
    static final String LOG_FAILED_BACKUP = PREFIX + ChatColor.RED + "Failed to backup world! Please check server logs!";
    static final String LOG_CREATED_TARGET_DIR = PREFIX + "Created directory '" + TARGET_DIR + "'";
    static final String LOG_FAILED_TARGET_DIR = PREFIX + "Failed to create directory '" + TARGET_DIR +
            "', shutting down plugin!";
    static final String LOG_REMOVING_FILE = PREFIX + ChatColor.YELLOW + "Removing %s";
    static final String LOG_FAILED_MEASURING_FOLDER = PREFIX + ChatColor.RED + "Unable to measure folder size";
    static final String LOG_EXCEEDED_MAX = PREFIX + ChatColor.RED + "Max of %d MB exceeded! Deleting files...";
    static final String LOG_CURRENTLY_USING = PREFIX + ChatColor.GREEN + "Currently using: %d MB out of %d MB";
    static final String LOG_FAILED_SAVING_FILE = PREFIX + WARNING + "Could not save file %s";
    static final String LOG_BACKUP_ATTEMPT = PREFIX + ChatColor.YELLOW + "Attempting to backup...";
    static final String LOG_CANNOT_CREATE_DIRECTORIES = PREFIX + "Cannot create directories";
    static final String LOG_CANNOT_DELETE_EXISTING = PREFIX + "Cannot delete existing zip file: %s";
    static final String LOG_ZIP_FILE_EXISTS = PREFIX + "Zip file already exists: %s";
    static final String LOG_SOURCE_DIRS_NULL = PREFIX + "Source directories are null";
    static final String LOG_SOURCE_DOESNT_EXIST = PREFIX + "Source directory doesn't exists: %s";
    static final String LOG_INTERVAL_BACKUP_TIME = PREFIX + "Interval backups running every %d minutes";

    // Player messages
    static final String MSG_REQUIRE_OP = PREFIX + ChatColor.RED + "I'm sorry, but you do not have permission to " +
            "perform this command. Please contact the server administrators if you believe that this is in error.";
    static final String MSG_UPDATE_AVAILABLE = PREFIX + ChatColor.GREEN + "Update available! Type " + ChatColor.YELLOW +
            "/reload confirm";

}
