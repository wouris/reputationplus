package me.wouris.listeners;

import me.wouris.GUIs.minusRepReasonGUI;
import me.wouris.GUIs.plusRepReasonGUI;
import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.model.voteStats;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.Placeholder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.SQLException;

public class repGUIListener implements Listener {

    private final main plugin;
    private final Config config;

    public repGUIListener(main plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void repGUIClick(InventoryClickEvent e) throws SQLException {
        Player p = (Player) e.getWhoClicked();

        OfflinePlayer target = this.plugin.getTarget(p.getUniqueId());

        if(!e.getView().getTitle().equalsIgnoreCase(ChatUtils.format(
                    Placeholder.setPlaceholders(plugin, config.getRepGUITitle(), target, p)))) { return; }

        e.setCancelled(true);

        String prefix = "";
        if (config.getUsePrefix()){
            prefix = config.getPrefix() + " ";
        }

        reputationStats playerStats = plugin.getRepDB().getStats(p.getUniqueId());
        reputationStats targetStats = plugin.getRepDB().getStats(target.getUniqueId());

        voteStats voteStats = plugin.getVoteDB().getStats(p.getUniqueId(), target.getUniqueId());

        if (e.getCurrentItem() != null){
            if(e.getCurrentItem().getType() == Material.valueOf(config.getPlusRepBlock())){
                p.openInventory(plusRepReasonGUI.createGUI(plugin, p, target, config));
            }else if (e.getCurrentItem().getType() == Material.valueOf(config.getMinusRepBlock())){
                p.openInventory(minusRepReasonGUI.createGUI(plugin, p, target, config));
            }else{
                p.openInventory(e.getInventory());
            }
        }
    }
}
