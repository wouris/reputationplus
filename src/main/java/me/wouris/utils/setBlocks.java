package me.wouris.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wouris.main;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class setBlocks {

    public static ItemStack setBlock(main Plugin, Material material, String name, List<String> blockDescription, OfflinePlayer target) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(ChatUtils.format(PlaceholderAPI.setPlaceholders(target, name)));
        if (blockDescription != null){
            blockDescription.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(target, text)));
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
            blockDescription.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(p, text)));
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
            blockDescription.replaceAll(text -> ChatUtils.format(PlaceholderAPI.setPlaceholders(p, text)));
            itemMeta.setLore(blockDescription);
        }
        skullMeta.setOwningPlayer(p);
        item.setItemMeta(skullMeta);
        return item;
    }
}
