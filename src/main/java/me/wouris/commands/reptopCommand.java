package me.wouris.commands;

import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
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
    private final Config config;

    public reptopCommand(main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p){

            String prefix = "";
            if (config.getUsePrefix()){
                prefix = config.getPrefix() + " ";
            }

            if (p.hasPermission("reputationplus.command")){
                List<reputationStats> repList;
                List<String> messageFormat = config.getRepTopFormat();
                // list all players with highest reputation from sql
                try {
                    repList = plugin.getRepDB().getTopList();
                } catch (SQLException e) {
                    p.sendMessage(ChatUtils.format(prefix + config.getRepTopNoPlayers()));
                    return true;
                }

                messageFormat.subList(0, messageFormat.size() - 1).forEach(s -> {
                    p.sendMessage(ChatUtils.format(s));
                });

                for (int i = 0; i < repList.size(); i++){
                    String name = Bukkit.getOfflinePlayer(repList.get(i).getUUID()).getName();
                    String message = messageFormat.get(messageFormat.size()-1)
                            .replace("{position}", String.valueOf(i + 1))
                            .replace("{name}", name)
                            .replace("{reputation}", String.valueOf(repList.get(i).getReputation()));
                    p.sendMessage(ChatUtils.format(message));
                }

            }else{
                String message = config.getNoPermissionMessage();
                p.sendMessage(
                        ChatUtils.format(prefix + message));
                return true;
            }
        }

        return true;
    }
}
