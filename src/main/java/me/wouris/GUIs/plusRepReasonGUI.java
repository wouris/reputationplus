package me.wouris.GUIs;

import me.wouris.main;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.Placeholder;
import me.wouris.utils.setBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class plusRepReasonGUI {

    public static Inventory createGUI(main plugin, Player p, OfflinePlayer target, Config config){

        Inventory inv = Bukkit.createInventory(p, 9, ChatUtils.format(
                Placeholder.setPlaceholders(plugin, config.getPositiveReasonGUITitle(), target, p)));

        List<String> reasonObjects = config.getAllPlusRepReasons();

        for(int i = 0; i < reasonObjects.size(); i++){
            String name = config.getReasonName("plus", reasonObjects.get(i));

            if (reasonObjects.get(i).equals("custom-reason")){
                Material material = Material.valueOf(config.getReasonBlock("plus", "custom-reason"));
                List<String> description = config.getReasonDesc("plus", "custom-reason");
                ItemStack item = setBlocks.setBlock(plugin, material, name, description, target);
                inv.setItem(8, item);
                continue;
            }

            Material material = Material.valueOf(config.getReasonBlock("plus", reasonObjects.get(i)));
            List<String> description = config.getReasonDesc("plus", reasonObjects.get(i));
            ItemStack item = setBlocks.setBlock(plugin, material, name, description, target);
            inv.setItem(i, item);
        }

        return inv;
    }
}
