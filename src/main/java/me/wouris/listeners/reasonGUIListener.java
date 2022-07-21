package me.wouris.listeners;

import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.reputationStats;
import me.wouris.model.voteStats;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.Placeholder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class reasonGUIListener implements Listener {

    private final main plugin;
    private final Config config;

    public reasonGUIListener(main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void reasonGUIClick(InventoryClickEvent e) throws SQLException {
        Player p = (Player) e.getWhoClicked();
        OfflinePlayer target;

        try{
            target = this.plugin.getTarget(p.getUniqueId());
        }catch (IllegalArgumentException ex){
            // this will accur if someone has not opened GUI yet and plugin will try to read target when there is none
            // thus throwing this exception
            return;
        }


        if(!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                Placeholder.setPlaceholders(plugin, config.getPositiveReasonGUITitle(), target, p)))
        && (!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                Placeholder.setPlaceholders(plugin, config.getNegativeReasonGUITitle(), target, p))))) { return; }

        e.setCancelled(true);

        String prefix = "";
        if (config.getUsePrefix()){
            prefix = config.getPrefix() + " ";
        }

        List<String> plusReasons = config.getAllPlusRepReasons();
        List<String> minusReasons = config.getAllMinusRepReasons();

        reputationStats playerStats = plugin.getRepDB().getStats(p.getUniqueId());
        reputationStats targetStats = plugin.getRepDB().getStats(target.getUniqueId());
        voteStats voteStats = plugin.getVoteDB().getStats(p.getUniqueId(), target.getUniqueId());

        if (e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                Placeholder.setPlaceholders(plugin, config.getPositiveReasonGUITitle(), target, p)))) {
            if (e.getCurrentItem() != null){
                Material item = e.getCurrentItem().getType();
                for (String reason : plusReasons) {
                    if (item == Material.valueOf(config.getReasonBlock("plus", reason))) {

                        p.closeInventory();

                        // create a reason entry in the database
                        reasonStats reasonStats = new reasonStats(
                                target.getUniqueId().toString(),
                                p.getUniqueId().toString(),
                                "positive",
                                config.getReasonName("plus", ChatColor.stripColor(reason)),
                                new Timestamp(System.currentTimeMillis()));
                        plugin.getReasonDB().createReason(reasonStats);

                        // create target stats or update if exists
                        if (targetStats == null){
                            targetStats = new reputationStats(target.getUniqueId(), 1, 0, null);

                            this.plugin.getRepDB().createStats(targetStats);
                        } else {
                            targetStats.setReputation(targetStats.getReputation() + 1);
                            this.plugin.getRepDB().updateReputation(targetStats);
                        }

                        // create player stats or update if exists
                        setPlayerStats(p, target, playerStats, voteStats, "positive");

                        int rep = targetStats.getReputation();
                        // I could not find any viable solution other than this
                        // rater message
                        TextComponent text = new TextComponent(ChatUtils.format(
                                prefix + Placeholder.setPlaceholders(plugin, config.getPlusRepMessage(), target, p)));
                        if (config.getPlusRepHoverMessage() != null)
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new Text(ChatUtils.format(
                                           Placeholder.setPlaceholders(plugin, config.getPlusRepHoverMessage(), target, p)))));
                        p.sendMessage(text);

                        // target message
                        if (target.isOnline()){
                            text = new TextComponent(ChatUtils.format(
                                    prefix + Placeholder.setPlaceholders(plugin,
                                            config.getTargetPlusRepMessage(), target, p)));
                            if (config.getTargetPlusRepHoverMessage() != null)
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new Text(ChatUtils.format(
                                                Placeholder.setPlaceholders(plugin, config.getTargetPlusRepHoverMessage(), target, p)))));
                            Player targetPlayer = (Player) target;
                            targetPlayer.sendMessage(text);
                        }

                        // remove unnecessary data to save memory space
                        this.plugin.removeData(p.getUniqueId(), target.getUniqueId());
                        return;
                    }
                }
            }else {
                p.openInventory(e.getInventory());
            }
        }else{
            if (e.getCurrentItem() != null){
                Material item = e.getCurrentItem().getType();
                for (String reason : minusReasons) {
                    if (item == Material.valueOf(config.getReasonBlock("minus", reason))) {

                        p.closeInventory();

                        // create a reason entry in the database
                        reasonStats reasonStats = new reasonStats(
                                target.getUniqueId().toString(),
                                p.getUniqueId().toString(),
                                "negative",
                                config.getReasonName("minus", ChatColor.stripColor(reason)),
                                new Timestamp(System.currentTimeMillis()));
                        plugin.getReasonDB().createReason(reasonStats);

                        // create target stats or update if exists
                        if (targetStats == null){
                            targetStats = new reputationStats(target.getUniqueId(), -1, 0, null);
                            this.plugin.getRepDB().createStats(targetStats);
                        } else {
                            targetStats.setReputation(targetStats.getReputation() - 1);
                            this.plugin.getRepDB().updateReputation(targetStats);
                        }

                        // create player stats or update if exists
                        setPlayerStats(p, target, playerStats, voteStats, "negative");

                        // rater message
                        int rep = targetStats.getReputation();
                        TextComponent text = new TextComponent(ChatUtils.format(
                                prefix + Placeholder.setPlaceholders(plugin, config.getMinusRepMessage(), target, p)));
                        if (config.getMinusRepHoverMessage() != null)
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new Text(ChatUtils.format(
                                            Placeholder.setPlaceholders(plugin, config.getMinusRepHoverMessage(), target, p)))));
                        p.sendMessage(text);
                        return;
                    }
                }
            }else {
                p.openInventory(e.getInventory());
            }
        }
    }

    private void setPlayerStats(Player p, OfflinePlayer target, reputationStats playerStats, voteStats voteStats, String decision) throws SQLException {

        if (playerStats == null){
            playerStats = new reputationStats(p.getUniqueId(), 0, 1, Timestamp.valueOf(LocalDateTime.now()));
            this.plugin.getRepDB().createStats(playerStats);
        }else{
            playerStats.setVotes(playerStats.getVotes() + 1);
            playerStats.setLastVote(Timestamp.valueOf(LocalDateTime.now()));
            this.plugin.getRepDB().updateVotes(playerStats);
        }

        if (voteStats == null){
            voteStats = new voteStats(p.getUniqueId(), target.getUniqueId(), 1);
            this.plugin.getVoteDB().createStats(voteStats);
        } else {
            voteStats.setVotes(voteStats.getVotes() + 1);
            this.plugin.getVoteDB().updateVotes(voteStats);
        }
    }
}
