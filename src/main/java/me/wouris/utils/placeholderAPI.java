package me.wouris.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class placeholderAPI extends PlaceholderExpansion {

    private final main plugin;

    public placeholderAPI(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rep";
    }

    @Override
    public @NotNull String getAuthor() {
        return "wouris";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String identifier) {
        String text = "";
        OfflinePlayer target;

        try{
            target = plugin.getTarget(p.getUniqueId());
        } catch (IllegalArgumentException e) { target = p; }

        // need to be all if not else if because of option to use multiple placeholders
        if (identifier.equals("player_reputation"))
            try {
                text = String.valueOf(plugin.getRepDB().getStats(p.getUniqueId()).getReputation());
            } catch (SQLException | NullPointerException e) {
                text = 0 + "";
            }

        if (identifier.equals("target_reputation"))
            try {
                text = String.valueOf(plugin.getRepDB().getStats(target.getUniqueId()).getReputation());
            } catch (SQLException | NullPointerException e) {
                text = 0 + "";
            }

        if (identifier.equals("player"))
            text = p.getName();

        if (identifier.equals("target"))
            text = target.getName();

        if (identifier.equals("player_votes"))
            try {
                text = String.valueOf(plugin.getRepDB().getStats(p.getUniqueId()).getVotes());
            } catch (SQLException | NullPointerException e) {
                text = 0 + "";
            }

        if (identifier.equals("target_votes"))
            try {
                text = String.valueOf(plugin.getRepDB().getStats(target.getUniqueId()).getVotes());
            } catch (SQLException | NullPointerException e) {
                text = 0 + "";
            }


        if (identifier.equals("time_ago"))
            try {
                text = new PrettyTime().format(plugin.getRepDB().getStats(p.getUniqueId()).getLastVote().toLocalDateTime());
            } catch (SQLException | NullPointerException ignored) {}

        if (identifier.equals("time_until")){
            LocalDateTime lastVote = null;

            try {
                lastVote = plugin.getRepDB().getStats(p.getUniqueId()).getLastVote().toLocalDateTime();
            } catch (SQLException | NullPointerException e) {
                // this should never happen
                e.printStackTrace();
            }


            if (lastVote != null) {
                time timeMessage = new time(p, plugin);

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

                text = time.substring(0, time.length() - 1);
            }
        }

        return text;
    }
}
