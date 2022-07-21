package me.wouris.utils;

import me.wouris.main;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

public class setBlocks {

    public static ItemStack setBlock(main Plugin, Material material, String name, List<String> blockDescription, OfflinePlayer target) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(ChatUtils.format(Placeholder.setPlaceholders(Plugin, name, target, null)));
        if (blockDescription != null){
            blockDescription.replaceAll(text -> ChatUtils.format(Placeholder.setPlaceholders(Plugin, text, target, null)));
            itemMeta.setLore(blockDescription);
        }
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setSkull(OfflinePlayer p, main Plugin, String name, List<String> blockDescription, OfflinePlayer target){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta itemMeta = item.getItemMeta();
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setDisplayName(ChatUtils.format(name));
        if (blockDescription != null){
            for (int i = 0; i < blockDescription.size(); i++){
                blockDescription.set(i, ChatUtils.format(Placeholder.setPlaceholders(Plugin, blockDescription.get(i), target, p)));
            }
            itemMeta.setLore(blockDescription);
        }
        skullMeta.setOwningPlayer(target);
        item.setItemMeta(skullMeta);
        return item;
    }

    public static ItemStack setSkullLatestVoters(OfflinePlayer p, main Plugin, String name, List<String> blockDescription, OfflinePlayer target){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta itemMeta = item.getItemMeta();
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setDisplayName(ChatUtils.format(name));
        if (blockDescription != null){
            for (int i = 0; i < blockDescription.size(); i++){
                blockDescription.set(i, ChatUtils.format(Placeholder.setPlaceholders(Plugin, blockDescription.get(i), target, p)));
            }
            itemMeta.setLore(blockDescription);
        }
        skullMeta.setOwningPlayer(p);
        item.setItemMeta(skullMeta);
        return item;
    }
}
