import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Inspects a folder by measuring the total storage
 * space used and keeps track of files in a sorted array
 */
public class FolderVisitor implements FileVisitor<Path> {

    private long bytes = 0;
    private String prefix;
    private String path;
    private ArrayList<Path> files;

    /**
     * Default constructor defines the folder of interest
     * @param prefix Used by announcements/logs
     * @param path Folder of interest
     */
    FolderVisitor(String prefix, String path) {
        this.prefix = prefix;
        this.path = path;
        this.files = new ArrayList<>();
    }

    /**
     * Ensures backups do not take up more storage than
     * that defined in config.yml
     * @param max Maximum storage allowed (In MB)
     */
    void meetStorageRestriction(long max) {

        // Get size of all backups
        this.walkPathTree();
        long maxBytes = (max * 1024 * 1024); // MegaBytes to Bytes

        // Check if limit exceeded
        boolean exceeded = bytes > maxBytes;
        this.logStatus(exceeded, max, bytes);

        // Start removing old backups
        while (exceeded && files.size() > 0) {

            // Attempt to remove file
            File fileToRemove = files.get(0).toFile(); // Get earliest backup
            bytes -= fileToRemove.length(); // Remove file size
            Bukkit.getLogger().info(prefix + ChatColor.YELLOW + "Removing " + fileToRemove.toString());

            // If delete file successful
            if (fileToRemove.delete())
                files.remove(0); // Delete from ArrayList
            else
                break;

            exceeded = bytes > maxBytes; // Update status

        }

    }

    private void walkPathTree() {
        try {
            Files.walkFileTree(Paths.get(this.path), this);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().info(prefix + ChatColor.RED + "Unable to measure folder size");
        }
    }

    private void logStatus(boolean exceeded, long max, long current) {
        if (exceeded)
            Bukkit.getLogger().info(prefix + ChatColor.RED + "Max of " + max + " MB exceeded! Deleting files...");
        else
            Bukkit.getLogger().info(prefix + ChatColor.GREEN + "Currently using: " + current/1000000 + " MB " +
                    "out of " + max + " MB");
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        bytes += file.toFile().length(); // Add up file size
        files.add(file); // Add file to list
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        if (dir.toString().equals("world_backups")) Collections.sort(files); // Sort array of files
        return FileVisitResult.CONTINUE;
    }

}
