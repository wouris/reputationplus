package me.wouris.commands;

import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class reptopCommand implements CommandExecutor {

    private final main plugin;

    public reptopCommand(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p){

            if (p.hasPermission("reputationplus.command")){
                List<reputationStats> repList;
                // list all players with highest reputation from sql
                try {
                    repList = plugin.getRepDatabase().getTopList();
                } catch (SQLException ignored) {
                    p.sendMessage(ChatUtils.format("&8[&bReputation&3+&8] &cNo players found!"));
                    return true;
                }
                // send top list to player via message
                p.sendMessage(ChatUtils.format("&bTop Rated Players:"));
                for (int i = 0; i < repList.size(); i++){
                    String name = Bukkit.getOfflinePlayer(repList.get(i).getUUID()).getName();
                    if (repList.get(i).getReputation() > 0){
                        p.sendMessage(ChatUtils.format((i+1) + ": &3" + name + " &a- &3" + repList.get(i).getReputation()));
                    }else{
                        p.sendMessage(ChatUtils.format((i+1) + ": &3" + name + " &a- &c" + repList.get(i).getReputation()));
                    }
                }

            }else{
                p.sendMessage(
                        ChatUtils.format("&8[&bReputation&3+&8] &cYou don't have permission to use this command!"));
                return true;
            }
        }

        return true;
    }
}
