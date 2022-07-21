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

public class repGUIReasonDisabled {

    public static Inventory createGUI(Player p, OfflinePlayer target, main plugin, Config config){

        String guiTitle = Placeholder.setPlaceholders(plugin, config.getRepGUITitle(), target, p);
        Inventory inv = Bukkit.createInventory(p, 9*3, ChatUtils.format(guiTitle));

        ItemStack repplus = setBlocks.setBlock( plugin,
                Material.valueOf(config.getPlusRepBlock()),
                config.getPlusRepButtonTitle(),
                config.getPlusRepButtonDescription(),
                target);

        ItemStack repminus = setBlocks.setBlock( plugin,
                Material.valueOf(config.getMinusRepBlock()),
                config.getMinusRepButtonTitle(),
                config.getMinusRepButtonDescription(),
                target);

        ItemStack filler = setBlocks.setBlock( plugin,
                Material.valueOf(config.getFillerBlock()),
                " ",
                null,
                target);

        ItemStack playerHead = setBlocks.setSkull(p, plugin,
                config.getTargetColor() + target.getName(),
                config.getTargetDescription(),
                target);

        ItemStack[] array = new ItemStack[9*3];
        for (int i = 0; i < 9*3; i++){
            switch (i){
                case 12 -> array[i] = repplus;
                case 13 -> array[i] = playerHead;
                case 14 -> array[i] = repminus;
                default -> array[i] = filler;
            }
        }
        inv.setContents(array);

        return inv;
    }
}
