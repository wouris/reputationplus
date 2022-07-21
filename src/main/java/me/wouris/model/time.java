package me.wouris.model;

import me.wouris.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class time {

    private long days = 0;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;

    public time(OfflinePlayer p, main plugin){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastVote;
        try {
            lastVote = plugin.getRepDB().getStats(p.getUniqueId()).getLastVote().toLocalDateTime();
        } catch (NullPointerException e) {
            return;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        lastVote.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss"));
        String time = "";

        StringBuilder intervalNum = new StringBuilder();
        StringBuilder intervalIndicator = new StringBuilder();
        String intervalConfig = plugin.getConfig().getString("options.interval-options.interval");
        if (intervalConfig != null){
            intervalConfig = intervalConfig.replaceAll("\\s", "");
            for (int i = 0; i < intervalConfig.length(); i++) {
                if (Character.isDigit(intervalConfig.charAt(i))) {
                    intervalNum.append(intervalConfig.charAt(i));
                } else {
                    intervalIndicator.append(intervalConfig.charAt(i));
                }
            }
        }

        int interval = Integer.parseInt(String.valueOf(intervalNum));
        String indicator = String.valueOf(intervalIndicator);

        switch (indicator) {
            case "s" -> lastVote = lastVote.plusSeconds(interval);
            case "m" -> lastVote = lastVote.plusMinutes(interval);
            case "h" -> lastVote = lastVote.plusHours(interval);
            case "d" -> lastVote = lastVote.plusDays(interval);
        }

        this.days = now.until(lastVote, ChronoUnit.DAYS);
        now = now.plusDays(days);

        this.hours = now.until(lastVote, ChronoUnit.HOURS);
        now = now.plusHours(hours);

        this.minutes = now.until(lastVote, ChronoUnit.MINUTES);
        now = now.plusMinutes(minutes);

        this.seconds = now.until(lastVote, ChronoUnit.SECONDS);
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

}
