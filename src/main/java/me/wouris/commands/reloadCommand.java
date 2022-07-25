package me.wouris.commands;

import me.wouris.main;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class reloadCommand implements CommandExecutor {

    private final main plugin;
    private final Config config;

    public reloadCommand(main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String prefix = "";
        if (this.plugin.getConfig().getBoolean("options.use-prefix")){
            prefix = this.plugin.getConfig().getString("options.prefix") + " ";
        }

        if (sender instanceof Player p){
            if (p.hasPermission("reputationplus.admin")){
                this.plugin.reloadConfig();
                String message = this.plugin.getConfig().getString("messages.config-reload");
                p.sendMessage(ChatUtils.format(prefix + message));
            }else{
                String message = this.plugin.getConfig().getString("messages.no-permission");
                p.sendMessage(ChatUtils.format(prefix + message));
            }
            return true;
        }else{
            this.plugin.reloadConfig();
            String message = this.plugin.getConfig().getString("messages.config-reload");
            this.plugin.getLogger().info("Config reloaded!");
            return true;
        }
    }
}
