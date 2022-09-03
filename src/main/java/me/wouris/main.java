package me.wouris;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.commands.reloadCommand;
import me.wouris.commands.repCommand;
import me.wouris.commands.reptopCommand;
import me.wouris.database.reasonDB;
import me.wouris.database.reputationDB;
import me.wouris.database.voteDB;
import me.wouris.listeners.reasonGUIListener;
import me.wouris.listeners.repGUIListener;
import me.wouris.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public final class main extends JavaPlugin {

    private reputationDB reputationDB;
    private voteDB voteDB;
    private reasonDB reasonDB;
    private HashMap<UUID, UUID> whoRatesWho;
    // this is only used for the custom reason GUI
    private HashMap<UUID, String> decision;
    private Config config;
    private Plugin instance;

    public Plugin getPlugin() { return instance; }
    public Config config() { return config; }
    public reputationDB getRepDB() {
        return reputationDB;
    }
    public voteDB getVoteDB() { return voteDB; }
    public reasonDB getReasonDB() {
        return reasonDB;
    }
    public void setDecision(UUID player, UUID target) { whoRatesWho.put(player, target); }
    public void removeDecision(UUID player, UUID target) { whoRatesWho.remove(player, target); }
    public void setTarget(UUID player, UUID target) { whoRatesWho.put(player, target); }
    public void removeData(UUID player, UUID target) { whoRatesWho.remove(player, target); }
    public OfflinePlayer getTarget(UUID player) { return Bukkit.getOfflinePlayer(whoRatesWho.get(player)); }

    @Override
    public void onEnable() {

        whoRatesWho = new HashMap<>();
        decision = new HashMap<>();
        this.instance = this;

        // register config
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        config = new Config(this);

        checkForUpdate();
        // sql database
        boolean isDisabled = setSQL();

        setPlaceholders();

        // register Listeners
        getServer().getPluginManager().registerEvents(new repGUIListener(this, config), this);
        getServer().getPluginManager().registerEvents(new reasonGUIListener(this, config), this);

        //register commands
        getCommand("rep").setExecutor(new repCommand(this, config));
        getCommand("reptop").setExecutor(new reptopCommand(this, config));
        getCommand("repreload").setExecutor(new reloadCommand(this, config));

        if (isDisabled) {
            getLogger().severe("Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private boolean setSQL(){
        boolean isDisabled = false;

        try {
            this.reputationDB = new reputationDB(this, config);
            this.voteDB = new voteDB(this);
            this.reasonDB = new reasonDB(this);
            reputationDB.initialize();
            voteDB.initialize();
            reasonDB.initialize();
            getLogger().info(ChatUtils.format("&aConnected to the database!"));
        } catch (SQLException e) {
            isDisabled = true;
            getLogger().severe("Could not connect to the database! Please check your connection settings!");
            System.out.println(e.getMessage());
        }

        return isDisabled;
    }

    private void checkForUpdate(){
        new updateChecker(this, 103334).getLatest(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version))
                getLogger().info(ChatUtils.format("&aPlugin is up to date! Version: &2" + version));
            else{
                getLogger().warning(ChatUtils.format("&6Plugin has new update!"));
                getLogger().warning(ChatUtils.format("&6Your version: &c" + this.getDescription().getVersion()
                        + " &8| &6New version: &a" + version));
            }
        });
    }

    private void setPlaceholders(){
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new placeholderAPI(this).register();
            new placeholderAPI_reptop(this).register();
        }
    }
}
