package me.wouris.GUIs;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.main;
import me.wouris.utils.ChatUtils;
import me.wouris.utils.Config;
import me.wouris.utils.setBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class plusRepReasonGUI {

    public static Inventory createGUI(main plugin, Player p, OfflinePlayer target, Config config){

        Inventory inv = Bukkit.createInventory(p, 9, ChatUtils.format(
                PlaceholderAPI.setPlaceholders(p, config.getPositiveReasonGUITitle())));

        List<String> reasonObjects = config.getAllPlusRepReasons();

        for(int i = 0; i < reasonObjects.size(); i++){
            String name = config.getReasonName("plus", reasonObjects.get(i));
            Material material = Material.valueOf(config.getReasonBlock("plus", reasonObjects.get(i)));
            List<String> description = PlaceholderAPI.setPlaceholders(p, config.getReasonDesc("plus", reasonObjects.get(i)));
            ItemStack item = setBlocks.setBlock(plugin, material, name, description, target);
            inv.setItem(i, item);
        }

        Material material = Material.valueOf(config.getCustomReasonBlock());
        List<String> description = PlaceholderAPI.setPlaceholders(p, config.getCustomReasonDesc());
        String name = PlaceholderAPI.setPlaceholders(p, config.getCustomReasonGUITitle());
        ItemStack item = setBlocks.setBlock(plugin, material, name, description, target);
        inv.setItem(8, item);

        return inv;
    }
}
