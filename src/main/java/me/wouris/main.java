package me.wouris;

import me.wouris.commands.reloadCommand;
import me.wouris.commands.repCommand;
import me.wouris.commands.reptopCommand;
import me.wouris.database.reputationDB;
import me.wouris.database.voteDB;
import me.wouris.listeners.repGUIListener;
import me.wouris.utils.logger;
import me.wouris.utils.repPlaceholder;
import me.wouris.utils.updateChecker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public final class main extends JavaPlugin {

    private reputationDB reputationDB;
    private voteDB voteDB;
    private HashMap<UUID, UUID> whoRatesWho;

    public reputationDB getRepDatabase() {
        return reputationDB;
    }
    public voteDB getVoteDB() { return voteDB; }
    public void setTarget(UUID player, UUID target) { whoRatesWho.put(player, target); }
    public void removeData(UUID player, UUID target) { whoRatesWho.remove(player, target); }
    public OfflinePlayer getTarget(UUID player) { return Bukkit.getOfflinePlayer(whoRatesWho.get(player)); }

    @Override
    public void onEnable() {

        boolean isDisabled = false;
        whoRatesWho = new HashMap<>();

        // register config
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        new updateChecker(this, 103334).getLatest(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version))
                logger.log(logger.LogLevel.INFO, "Plugin is up to date! Version: " + version);
            else{
                logger.log(logger.LogLevel.WARNING, "Plugin has new update!");
                logger.log(logger.LogLevel.WARNING, "Your version: " + this.getDescription().getVersion()
                 + " | New version: " + version);
            }
        });

        // sql database
        try {
            this.reputationDB = new reputationDB(this);
            this.voteDB = new voteDB(this);
            reputationDB.initialize();
            voteDB.initialize();
            logger.log(logger.LogLevel.SUCCESS, "Connected to the database!");
        } catch (SQLException e) {
            isDisabled = true;
            logger.log(logger.LogLevel.ERROR,
                    "Could not connect to the database! Please check your connection settings!");
            System.out.println(e.getMessage());
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new repPlaceholder(this).register();
        }else{
            logger.log(logger.LogLevel.ERROR, "PlaceholderAPI is not installed! " +
                    "Please install it and use command /papi ecloud download Player to use this plugin!");
            isDisabled = true;
        }

        // register Listeners
        getServer().getPluginManager().registerEvents(new repGUIListener(this), this);

        //register commands
        getCommand("rep").setExecutor(new repCommand(this));
        getCommand("reptop").setExecutor(new reptopCommand(this));
        getCommand("repreload").setExecutor(new reloadCommand(this));

        if (isDisabled) {
            logger.log(logger.LogLevel.ERROR, "Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        logger.log(logger.LogLevel.INFO, "Disabling plugin...");
    }
}
