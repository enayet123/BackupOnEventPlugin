# BackupOnEventPlugin
## A Minecraft plugin that backs up the world based on event triggers and interval timers
When backing up, the plugin will look for the default worlds, `world`, the `nether` and `the end` as well as any custom named world folders.

RunBackupOn section in `config.yml` defines which events trigger a backup and to control backup announcement or messages. Backups are also supported on an interval basis in minutes using the `intervalMinutes` field. Setting this to `0` disabled interval backups. 

The `lastPlayerQuit` field makes the plugin wait until the player count reaches `0` before a backup is run. 

Backups can have a minimum time interval set, using the `minimumIntervalInMinutes` field, which prevents backups being triggered multiple times by different users within the defined time period. Setting this to `0` allows for concurrent backups.
 
The `maxInMegaBytes` field defines the maximum amount of storage the plugin can use for backups in MegaBytes. If this is exceeded the plugin will delete the oldest backup available. Setting this to `0` provides unlimited storage space only limited by hardware.

Immediate backups can be run by a user in-game or on console using ``/backup``. This can is restricted to Ops by default but can be changed using the `opsOnly` field. 

The plugin now supports automatic backups. This can be disabled by setting `enabled` to ``false``.

**Events that can trigger a backup**:
  - Player join event
  - Player quit event
  - Player with correct permission executing `\backup` command
  - Player count reaching `0`
  - Interval timer reaching the end of its cycle

## Default config.yml
```yaml
# BackupWorlds lets you toggle which worlds (folders) to include in backups
# You can enable/disable the events that will trigger a backup to happen from RunBackupOn
# repeatInterval runs a backup every time X minutes has passed, 0 means disabled
# Messages and announcements can be hidden
# onJoin and onQuit will hide the 'x has joined the server' messages
# opsOnly restricts the /backup command to ops only
# Setting maxInMegaBytes to 0 will provide unlimited disk space
# Setting minimumIntervalInMinutes to 0 will allow concurrent backups
# AutoUpdate will download the latest version from bukkit.org when an Op joins the server
BackupWorlds:
  world: true
  world_nether: true
  world_the_end: true
  plugins: false
  custom_named_world: false
RunBackupOn:
  playerJoin: true
  playerQuit: false
  lastPlayerQuit: false
  repeatIntervals:
    minutes: 0
    whenPlayersAreOnline: true
HideMessage:
  onJoin: false
  onQuit: false
  backupAnnouncement: false
BackupCommand:
  opsOnly: true
BackupStorage:
  maxInMegaBytes: 1024
  minimumIntervalInMinutes: 1
AutoUpdate:
  enabled: true
```
