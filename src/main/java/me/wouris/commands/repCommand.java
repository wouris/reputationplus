package me.wouris.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.model.time;
import me.wouris.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class repCommand implements CommandExecutor{

    private final main plugin;

    public repCommand(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player p){

            String prefix = "";
            if (this.plugin.getConfig().getBoolean("options.use-prefix")){
                prefix = this.plugin.getConfig().getString("options.prefix") + " ";
            }

            if (p.hasPermission("reputationplus.command")){
                if (args.length == 1) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    this.plugin.setTarget(p.getUniqueId(), target.getUniqueId());

                    boolean enabledInterval = this.plugin.getConfig().getBoolean("options.interval-options.enabled");

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
                            p.sendMessage(ChatUtils.format(prefix + PlaceholderAPI.setPlaceholders(p,
                                    this.plugin.getConfig().getString("options.interval-options.message"))));
                            return true;
                        }
                    }


                    boolean canVoteForThemselves = this.plugin.getConfig().getBoolean("options.can-self-vote");
                    if (target.getName().equals(p.getName())){
                        if (!canVoteForThemselves) {
                            p.sendMessage(ChatUtils.format(prefix +
                                    this.plugin.getConfig().getString("options.no-self-vote-message")));
                            return true;
                        }
                    }

                    boolean canVote = true;
                    try{
                       canVote = this.plugin.getVoteDB().hasReachedMaxVotes(p.getUniqueId(), target.getUniqueId());
                    }catch (SQLException ignored){}

                    if (!canVote){
                        p.sendMessage(ChatUtils.format(prefix +
                                this.plugin.getConfig().getString("options.maxVotes-message")));
                        return true;
                    }

                    if (target.hasPlayedBefore() || target.isOnline()){

                        String guiTitle = PlaceholderAPI.setPlaceholders(target, this.plugin.getConfig().getString("options.gui-options.title"));
                        Inventory inv = Bukkit.createInventory(p, 9*3,
                                ChatUtils.format(guiTitle));

                        Material material = Material.valueOf(this.plugin.getConfig().getString("options.gui-options.+rep-button.block"));
                        ItemStack repplus = new ItemStack(material, 1);
                        ItemMeta repplusMeta = repplus.getItemMeta();
                        repplusMeta.setDisplayName(ChatUtils.format(this.plugin.getConfig().getString("options.gui-options.+rep-button.title")));
                        List<String> repplusLore = this.plugin.getConfig().getStringList("options.gui-options.+rep-button.description");
                        repplusLore.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(target, text)));
                        repplusMeta.setLore(repplusLore);
                        repplus.setItemMeta(repplusMeta);

                        material = Material.valueOf(this.plugin.getConfig().getString("options.gui-options.-rep-button.block"));
                        ItemStack repminus = new ItemStack(material, 1);
                        ItemMeta repminusMeta = repminus.getItemMeta();
                        repminusMeta.setDisplayName(ChatUtils.format(this.plugin.getConfig().getString("options.gui-options.-rep-button.title")));
                        List<String> repminusLore = this.plugin.getConfig().getStringList("options.gui-options.-rep-button.description");
                        repminusLore.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(target, text)));
                        repminusMeta.setLore(repminusLore);
                        repminus.setItemMeta(repminusMeta);

                        material = Material.valueOf(this.plugin.getConfig().getString("options.gui-options.filler.block"));
                        ItemStack filler = new ItemStack(material, 1);
                        ItemMeta fillerMeta = filler.getItemMeta();
                        fillerMeta.setDisplayName(" ");
                        filler.setItemMeta(fillerMeta);

                        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                        ItemMeta playerHeadMeta = playerHead.getItemMeta();
                        playerHeadMeta.setDisplayName(
                                ChatUtils.format(this.plugin.getConfig().getString(
                                                "options.gui-options.target-head.name-color")) + target.getName());
                        List<String> playerHeadLore = this.plugin.getConfig().getStringList("options.gui-options.target-head.description");
                        playerHeadLore.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(target, text)));
                        playerHeadMeta.setLore(playerHeadLore);
                        SkullMeta playerMeta = (SkullMeta) playerHead.getItemMeta();
                        playerMeta.setOwningPlayer(target);
                        playerHead.setItemMeta(playerHeadMeta);

                        ItemStack[] array = new ItemStack[9*3];
                        for (int i = 0; i < 9*3; i++){
                            if(i == 12){
                                array[i] = repplus;
                            } else if (i == 14){
                                array[i] = repminus;
                            } else if (i == 4) {
                                array[i] = playerHead;
                            } else {
                                array[i] = filler;
                            }
                        }
                        inv.setContents(array);

                        p.openInventory(inv);
                    }else{
                        p.sendMessage(ChatUtils.format(prefix +
                                PlaceholderAPI.setPlaceholders(target,
                                        this.plugin.getConfig().getString("messages.player-never-seen"))));
                    }
                }else {
                    reputationStats stats;
                    int reputation = 0;
                    int votes = 0;
                    try {
                        stats = plugin.getRepDatabase().getStats(p.getUniqueId());
                        try{
                            reputation = stats.getReputation();
                            votes = stats.getVotes();
                        } catch (NullPointerException ignored) {}
                    } catch (SQLException ignored) {}

                    boolean usePrefix = this.plugin.getConfig().getBoolean("options.rep-command.use-prefix");
                    List<String> messages = this.plugin.getConfig().getStringList("options.rep-command.messages");
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
                String message = this.plugin.getConfig().getString("messages.no-permission");
                p.sendMessage(
                        ChatUtils.format(prefix + message));
                return true;
            }
        }
        return true;
    }
}
