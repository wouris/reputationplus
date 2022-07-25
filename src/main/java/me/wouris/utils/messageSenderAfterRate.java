package me.wouris.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class messageSenderAfterRate {
    public static void sendPlusRepMessage(Player p, String finalPrefix, Config config, OfflinePlayer target){
        TextComponent mainComponent = new TextComponent(ChatUtils.format(finalPrefix +
                PlaceholderAPI.setPlaceholders(p, config.getPlusRepMessage())));
        mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format(
                PlaceholderAPI.setPlaceholders(p, config.getPlusRepHoverMessage()))).create()));
        p.spigot().sendMessage( mainComponent );

        // target message
        if (target.isOnline()){
            Player targetPlayer = (Player) target;

            mainComponent = new TextComponent(ChatUtils.format(finalPrefix +
                    PlaceholderAPI.setPlaceholders(p, config.getTargetPlusRepMessage())));
            mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format(
                    PlaceholderAPI.setPlaceholders(p, config.getTargetPlusRepHoverMessage()))).create()));
            targetPlayer.spigot().sendMessage( mainComponent );
        }
    }

    public static void sendMinusRepMessage(Player p, String finalPrefix, Config config){
        TextComponent mainComponent = new TextComponent(ChatUtils.format(finalPrefix +
                PlaceholderAPI.setPlaceholders(p, config.getMinusRepMessage())));
        mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format(
                PlaceholderAPI.setPlaceholders(p, config.getMinusRepHoverMessage()))).create()));
        p.spigot().sendMessage( mainComponent );
    }
}
