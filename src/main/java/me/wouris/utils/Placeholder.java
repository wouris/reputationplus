package me.wouris.utils;

import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.time;
import org.bukkit.OfflinePlayer;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Placeholder {

    public static String setPlaceholders(main plugin, String text, OfflinePlayer target, OfflinePlayer player) {

        int rep = 0;
        int votes = 0;

        if (text.contains("%rep_target_reputation%")) {
            try {
                rep = plugin.getRepDB().getStats(target.getUniqueId()).getReputation();
            } catch (SQLException | NullPointerException ignored) {}
            text = text.replaceAll("%rep_target_reputation%", String.valueOf(rep));
        }

        if (text.contains("%rep_target_votes%")) {
            try {
                votes = plugin.getRepDB().getStats(target.getUniqueId()).getVotes();
            } catch (SQLException | NullPointerException ignored) {}
            text = text.replaceAll("%rep_target_votes%", String.valueOf(votes));
        }

        if (text.contains("%rep_target%")) {
            text = text.replaceAll("%rep_target%", target.getName());
        }

        if (text.contains("%rep_player_reputation%")) {
            try {
                rep = plugin.getRepDB().getStats(player.getUniqueId()).getReputation();
            } catch (SQLException | NullPointerException e) { rep = 0; }
            text = text.replaceAll("%rep_player_reputation%", String.valueOf(rep));
        }

        if (text.contains("%rep_player_votes%")) {
            try {
                votes = plugin.getRepDB().getStats(player.getUniqueId()).getVotes();
            } catch (SQLException | NullPointerException e) { votes = 0; }
            text = text.replaceAll("%rep_player_votes%", String.valueOf(votes));
        }

        if (text.contains("%rep_player%")) {
            text = text.replaceAll("%rep_player%", player.getName());
        }

        if (text.contains("%rep_time%")) {
            LocalDateTime lastVote = null;

            try {
                lastVote = plugin.getRepDB().getStats(player.getUniqueId()).getLastVote().toLocalDateTime();
            } catch (SQLException | NullPointerException e) {
                e.printStackTrace();
            }


            if (lastVote != null) {
                time timeMessage = new time(player, plugin);

                String time = "";
                long days = timeMessage.getDays();
                long hours = timeMessage.getHours();
                long minutes = timeMessage.getMinutes();
                long seconds = timeMessage.getSeconds();

                if (days > 0) {
                    time += "&6" + days + "d ";
                }
                if (hours > 0) {
                    time += "&6" + hours + "h ";
                }
                if (minutes > 0) {
                    time += "&6" + minutes + "m ";
                }
                if (seconds > 0) {
                    time += "&6" + seconds + "s ";
                }

                time = time.substring(0, time.length() - 1);
                text = text.replaceAll("%rep_time_until%", time);
            }
        }
        return text;
    }
}
