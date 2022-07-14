package me.wouris.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wouris.main;
import me.wouris.model.reputationStats;
import me.wouris.model.time;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class repPlaceholder extends PlaceholderExpansion {

    private final main plugin;

    public repPlaceholder(main plugin) {
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

        reputationStats stats;
        int rep = 0;
        int votes = 0;
        LocalDateTime lastVote = null;

        try {
            rep = this.plugin.getRepDatabase().getStats(p.getUniqueId()).getReputation();
        } catch (SQLException | NullPointerException ignored) {}

        try {
            votes = this.plugin.getRepDatabase().getStats(p.getUniqueId()).getVotes();
        } catch (SQLException | NullPointerException ignored) {}

        try {
            lastVote = this.plugin.getRepDatabase().getStats(p.getUniqueId()).getLastVote().toLocalDateTime();
        } catch (SQLException | NullPointerException ignored) {}

        if (identifier.equals("time")){
            if (lastVote != null){
                time timeMessage = new time(p, plugin);

                String time = "";
                long days = timeMessage.getDays();
                long hours = timeMessage.getHours();
                long minutes = timeMessage.getMinutes();
                long seconds = timeMessage.getSeconds();

                if (days > 0){
                    time += "&6" + days + "d ";
                }
                if (hours > 0){
                    time += "&6" + hours + "h ";
                }
                if (minutes > 0){
                    time += "&6" + minutes + "m ";
                }
                if (seconds > 0){
                    time += "&6" + seconds + "s ";
                }

                time = time.substring(0, time.length() - 1);
                return time;
            }else{
                return null;
            }
        }

        return switch (identifier) {
            case "target", "player" -> p.getName();
            case "player_reputation", "target_reputation" -> String.valueOf(rep);
            case "player_votes", "target_votes" -> String.valueOf(votes);
            default -> null;
        };
    }
}
