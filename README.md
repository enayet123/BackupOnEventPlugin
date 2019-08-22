# BackupOnEventPlugin
## A Minecraft plugin that backs up a server when an event is triggered
When backing up, the plugin will look for the default `world`, the `nether` and `the end`. It will only backup folders related to the `level-name` defined in the `server.properties` generated by the Minecraft server.

`config.yml` can be used to define which events trigger a backup and to control backup announcement or messages.

**Events that trigger a backup**:
  - Player join event
  - Player quit event

## Default config.yml
```yaml
# You can enable/disable the events that will trigger a backup to happen
# Messages and announcements can be hidden
# onJoin and onQuit will hide the 'x has joined the server' messages
# Setting maxInMegaBytes to 0 will provide unlimited space
Player:
  onJoin: true
  onQuit: false
HideMessage:
  onJoin: false
  onQuit: false
  privatelyOnBackup: false
  publiclyOnBackup: false
BackupStorage:
  maxInMegaBytes: 1024
```
