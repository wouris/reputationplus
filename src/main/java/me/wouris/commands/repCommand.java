package me.wouris.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.GUIs.repGUIRatersDisabled;
import me.wouris.GUIs.repGUIRatersEnabled;
import me.wouris.main;
import me.wouris.model.time;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class repCommand implements CommandExecutor{

    private final main plugin;
    private final Config config;

    public repCommand(main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player p){

            String prefix = "";
            if (config.getUsePrefix()){
                prefix = config.getPrefix() + " ";
            }

            if (p.hasPermission("reputationplus.command")){
                if (args.length == 1) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    this.plugin.setTarget(p.getUniqueId(), target.getUniqueId());

                    boolean enabledInterval = config.getUseInterval();

                    if (enabledInterval){
                        boolean canVote = false;

                        time Time = new time(p, plugin);
                        if (Time.getDays() <= 0) {
                            if (Time.getHours() <= 0) {
                                if (Time.getMinutes() <= 0) {
                                    if (Time.getSeconds() <= 0) {
                                        canVote = true;
                                    }
                                }
                            }
                        }

                        if (!canVote){
                            p.sendMessage(ChatUtils.format(prefix + PlaceholderAPI.setPlaceholders(
                                    p, config.getIntervalMessage())));
                            return true;
                        }
                    }


                    boolean canVoteForThemselves = config.getCanSelfVote();
                    if (target.getName().equals(p.getName())){
                        if (!canVoteForThemselves) {
                            p.sendMessage(ChatUtils.format(prefix + PlaceholderAPI.setPlaceholders(
                                    p, config.getNoSelfVoteMessage())));
                            return true;
                        }
                    }

                    boolean canVote = true;
                    try{
                       canVote = this.plugin.getVoteDB().hasReachedMaxVotes(p.getUniqueId(), target.getUniqueId());
                    }catch (SQLException ignored){}

                    if (!canVote){
                        p.sendMessage(ChatUtils.format(prefix +
                                PlaceholderAPI.setPlaceholders(p, config.getMaxVotesMessage())));
                        return true;
                    }

                    if (target.hasPlayedBefore() || target.isOnline()){
                        boolean showRaters = config.getShowRecentRaters();
                        Inventory inv;
                        if (showRaters){
                            try {
                                inv = repGUIRatersEnabled.createGUI(p, target, this.plugin, config);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else{
                            inv = repGUIRatersDisabled.createGUI(p, target, this.plugin, config);
                        }
                        p.openInventory(inv);
                    }else{
                        p.sendMessage(ChatUtils.format(prefix +
                                PlaceholderAPI.setPlaceholders(p, config.getPlayerNeverSeenMessage())));
                    }
                }else {
                    boolean usePrefix = config.getUsePrefixRepCommand();
                    List<String> messages = config.getRepCommandMessages();
                    for (String message : messages){
                        if (usePrefix){
                            p.sendMessage(ChatUtils.format(prefix + PlaceholderAPI.setPlaceholders(p, message)));
                        } else {
                            p.sendMessage(ChatUtils.format(PlaceholderAPI.setPlaceholders(p, message)));
                        }
                    }
                    return true;
                }
            }else{
                String message = config.getNoPermissionMessage();
                p.sendMessage(
                        ChatUtils.format(PlaceholderAPI.setPlaceholders(p, prefix + message)));
                return true;
            }
        }
        return true;
    }
}
