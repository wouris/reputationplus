package me.wouris.utils;

import me.wouris.main;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

import java.io.InputStream;
import java.net.URL;

public class updateChecker {

    private final main plugin;
    private final int resourceID;

    public updateChecker(main plugin, int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public void getLatest(Consumer<String> consumer){
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream input = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceID).openStream();

            Scanner scanner = new Scanner(input)){
            if (scanner.hasNext()){
                consumer.accept(scanner.next());
            }
        } catch (IOException exception){
            this.plugin.getLogger().info("Cannot check for latest version!" + exception.getMessage());
            }
        });
    }
}
