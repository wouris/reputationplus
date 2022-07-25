package me.wouris.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.GUIs.customReasonGUI;
import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.reputationStats;
import me.wouris.model.voteStats;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.messageSenderAfterRate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
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
            // this will occur if someone has not opened GUI yet and plugin will try to read target when there is none
            // thus throwing this exception
            return;
        }


        if(!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                PlaceholderAPI.setPlaceholders(p, config.getPositiveReasonGUITitle())))
        && (!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                PlaceholderAPI.setPlaceholders(p, config.getNegativeReasonGUITitle()))))) { return; }

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
                PlaceholderAPI.setPlaceholders(p, config.getPositiveReasonGUITitle())))) {
            if (e.getCurrentItem() != null){
                Material item = e.getCurrentItem().getType();
                if (e.getCurrentItem().getType().equals(Material.NAME_TAG)){
                    customReasonGUI.manageGUI(p, plugin, config, "positive");
                    return;
                }
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
                        // rater message
                        messageSenderAfterRate.sendPlusRepMessage(p, prefix, config, target);

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
                if (e.getCurrentItem().getType().equals(Material.NAME_TAG)){
                    customReasonGUI.manageGUI(p, plugin, config, "negative");
                    return;
                }

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
                        messageSenderAfterRate.sendMinusRepMessage(p, prefix, config);

                        // remove unnecessary data to save memory space
                        this.plugin.removeData(p.getUniqueId(), target.getUniqueId());

                        return;
                    }
                }
            }else {
                p.openInventory(e.getInventory());
            }
        }
    }

    private void setPlayerStats(Player p, OfflinePlayer target, reputationStats playerStats, voteStats voteStats, String decision) throws SQLException {

        customReasonGUI.setPlayerStats(p, target, playerStats, voteStats, this.plugin);
    }
}
