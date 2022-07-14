package me.wouris.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.model.voteStats;
import me.wouris.utils.ChatUtils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class repGUIListener implements Listener {

    private final main plugin;

    public repGUIListener(main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void repGUIClick(InventoryClickEvent e) throws SQLException {
        Player p = (Player) e.getWhoClicked();

        OfflinePlayer target = this.plugin.getTarget(p.getUniqueId());

        if(!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                    PlaceholderAPI.setPlaceholders(target,
                            this.plugin.getConfig().getString("options.gui-options.title"))))) { return; }

        e.setCancelled(true);

        String prefix = "";
        if (this.plugin.getConfig().getBoolean("options.use-prefix")){
            prefix = this.plugin.getConfig().getString("options.prefix") + " ";
        }

        reputationStats playerStats = plugin.getRepDatabase().getStats(p.getUniqueId());
        reputationStats targetStats = plugin.getRepDatabase().getStats(target.getUniqueId());

        voteStats voteStats = plugin.getVoteDB().getStats(p.getUniqueId(), target.getUniqueId());

        if (e.getCurrentItem() != null){
            if(e.getCurrentItem().getType() == Material.GREEN_TERRACOTTA){
                p.closeInventory();
                if (targetStats == null){
                    targetStats = new reputationStats(target.getUniqueId(), 1, 0, null);

                    this.plugin.getRepDatabase().createStats(targetStats);
                    System.out.println("Vote for " + targetStats.getLastVote());
                } else {
                    targetStats.setReputation(targetStats.getReputation() + 1);
                    this.plugin.getRepDatabase().updateReputation(targetStats);
                }

                setPlayerStats(p, target, playerStats, voteStats);
                int rep = targetStats.getReputation();
                // I could not find any viable solution other than this
                // voter message
                TextComponent text = new TextComponent(ChatUtils.format(
                        prefix + PlaceholderAPI.setPlaceholders(target,
                                this.plugin.getConfig().getString("messages.+rep-message"))));
                if (this.plugin.getConfig().getString("messages.+rep-hover-message") != null)
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(ChatUtils.format(
                                    PlaceholderAPI.setPlaceholders(target,
                                            this.plugin.getConfig().getString("messages.+rep-hover-message"))))));
                p.sendMessage(text);

                // target message
                if (target.isOnline()){
                    text = new TextComponent(ChatUtils.format(
                            prefix + PlaceholderAPI.setPlaceholders(p,
                                    this.plugin.getConfig().getString("messages.+rep-message-target"))));
                    if (this.plugin.getConfig().getString("messages.+rep-hover-message-target") != null)
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new Text(ChatUtils.format(
                                        PlaceholderAPI.setPlaceholders(target,
                                                this.plugin.getConfig().getString("messages.+rep-hover-message-target"))))));
                    Player targetPlayer = (Player) target;
                    targetPlayer.sendMessage(text);
                }

                // remove unnecessary data to save memory space
                this.plugin.removeData(p.getUniqueId(), target.getUniqueId());

            }else if (e.getCurrentItem().getType() == Material.RED_TERRACOTTA){
                p.closeInventory();
                if (targetStats == null){
                    targetStats = new reputationStats(target.getUniqueId(), -1, 0, null);
                    this.plugin.getRepDatabase().createStats(targetStats);
                } else {
                    targetStats.setReputation(targetStats.getReputation() - 1);
                    this.plugin.getRepDatabase().updateReputation(targetStats);
                }

                setPlayerStats(p, target, playerStats, voteStats);
                int rep = targetStats.getReputation();
                TextComponent text = new TextComponent(ChatUtils.format(
                        prefix + PlaceholderAPI.setPlaceholders(target,
                                this.plugin.getConfig().getString("messages.-rep-message"))));
                if (this.plugin.getConfig().getString("messages.-rep-hover-message") != null)
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(ChatUtils.format(
                                    PlaceholderAPI.setPlaceholders(target,
                                            this.plugin.getConfig().getString("messages.-rep-hover-message"))))));
                p.sendMessage(text);
            }else{
                p.openInventory(e.getInventory());
            }
        }
    }

    private void setPlayerStats(Player p, OfflinePlayer target, reputationStats playerStats, voteStats voteStats) throws SQLException {

        if (playerStats == null){
            playerStats = new reputationStats(p.getUniqueId(), 0, 1, Timestamp.valueOf(LocalDateTime.now()));
            this.plugin.getRepDatabase().createStats(playerStats);
        }else{
            playerStats.setVotes(playerStats.getVotes() + 1);
            playerStats.setLastVote(Timestamp.valueOf(LocalDateTime.now()));
            this.plugin.getRepDatabase().updateVotes(playerStats);
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
