package me.wouris.GUIs;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.reputationStats;
import me.wouris.model.voteStats;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.messageSenderAfterRate;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class customReasonGUI {

    public static void manageGUI(Player p, main plugin, Config config, final String decision) {

        // create items
        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagMeta = nameTag.getItemMeta();
        nameTagMeta.setDisplayName(ChatUtils.format(PlaceholderAPI.setPlaceholders(p, config.getCustomReasonGUITitle())));
        nameTagMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nameTag.setItemMeta(nameTagMeta);

        // find prefix
        String prefix = "";
        if (config.getUsePrefix()){
            prefix = config.getPrefix() + " ";
        }

        // find target
        OfflinePlayer target;
        try{
            target = plugin.getTarget(p.getUniqueId());
        }catch (IllegalArgumentException ex){
            // this will occur if someone has not opened GUI yet and plugin will try to read target when there is none
            // thus throwing this exception
            return;
        }

        // create anvil gui
        String finalPrefix = prefix;
        AnvilGUI.Builder gui = new AnvilGUI.Builder()
                .plugin(plugin.getPlugin())
                .itemLeft(nameTag)
                .title(ChatUtils.format(config.getCustomReasonGUITitle()))
                .text(ChatUtils.format(config.getCustomReasonGUITitle()))
                .onComplete((player, text) -> {
                    String plusMinus;
                    if (decision.equals("&a+rep")){
                        plusMinus = "plus";
                    } else {
                        plusMinus = "minus";
                    }

                    // get stats
                    reputationStats playerStats = null;
                    reputationStats targetStats = null;
                    voteStats voteStats = null;
                    try {
                        playerStats = plugin.getRepDB().getStats(p.getUniqueId());
                    } catch (SQLException ignored) {}
                    try{
                        targetStats = plugin.getRepDB().getStats(target.getUniqueId());
                    } catch (SQLException ignored) {}
                    try{
                        voteStats = plugin.getVoteDB().getStats(p.getUniqueId(), target.getUniqueId());
                    } catch (SQLException ignored) {}

                    // create a reason entry in the database
                    reasonStats reasonStats = new reasonStats(
                            target.getUniqueId().toString(),
                            player.getUniqueId().toString(),
                            decision,
                            text,
                            new Timestamp(System.currentTimeMillis()));
                    try {
                        plugin.getReasonDB().createReason(reasonStats);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // create target stats or update if exists
                    if (targetStats == null){
                        if (decision.equals("positive"))
                            targetStats = new reputationStats(target.getUniqueId(), 1, 0, null);
                        else
                            targetStats = new reputationStats(target.getUniqueId(), -1, 0, null);

                        try {
                            plugin.getRepDB().createStats(targetStats);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        if (decision.equals("positive"))
                            targetStats.setReputation(targetStats.getReputation() + 1);
                        else
                            targetStats.setReputation(targetStats.getReputation() - 1);

                        try {
                            plugin.getRepDB().updateReputation(targetStats);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // create player stats or update if exists
                    try {
                        setPlayerStats(p, target, playerStats, voteStats, plugin);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // I could not find any viable solution other than this
                    if (decision.equals("positive")){
                        // rater message
                        messageSenderAfterRate.sendPlusRepMessage(p, finalPrefix, config, target);
                    } else {
                        messageSenderAfterRate.sendMinusRepMessage(p, finalPrefix, config);
                    }

                    // remove unnecessary data to save memory space
                    plugin.removeData(p.getUniqueId(), target.getUniqueId());
                    return AnvilGUI.Response.close();
                });

        gui.open(p);
    }


    public static void setPlayerStats(Player p, OfflinePlayer target, reputationStats playerStats, voteStats voteStats, main plugin) throws SQLException {
        if (playerStats == null){
            playerStats = new reputationStats(p.getUniqueId(), 0, 1, Timestamp.valueOf(LocalDateTime.now()));
            plugin.getRepDB().createStats(playerStats);
        }else{
            playerStats.setVotes(playerStats.getVotes() + 1);
            playerStats.setLastVote(Timestamp.valueOf(LocalDateTime.now()));
            plugin.getRepDB().updateVotes(playerStats);
        }

        if (voteStats == null){
            voteStats = new voteStats(p.getUniqueId(), target.getUniqueId(), 1);
            plugin.getVoteDB().createStats(voteStats);
        } else {
            voteStats.setVotes(voteStats.getVotes() + 1);
            plugin.getVoteDB().updateVotes(voteStats);
        }
    }
}
