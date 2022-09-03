package me.wouris.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wouris.main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class placeholderAPI_reptop extends PlaceholderExpansion {

    private final main plugin;

    public placeholderAPI_reptop(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "reptop";
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
    public String onRequest(OfflinePlayer p, String identifier){
        String text = "";

        int number;
        if (identifier.contains("name_")){
            try{
                number = Integer.parseInt(identifier.substring(5));
            }catch (StringIndexOutOfBoundsException | NumberFormatException e){
                return ChatUtils.format("&cInvalid placeholder usage!" +
                        "\n" + "&cUsage: &7%reptop_rep_<positive_number>%");
            }

            try {
                text = Bukkit.getOfflinePlayer(plugin.getRepDB().getTop(number).getUUID()).getName();
            } catch (SQLException | NullPointerException e) {
                text = "None";
            }
        }
        if (identifier.contains("rep_")){
            try{
                number = Integer.parseInt(identifier.substring(4));
            }catch (StringIndexOutOfBoundsException | NumberFormatException e){
                return ChatUtils.format("&cInvalid placeholder usage!" +
                        "\n" + "&cUsage: &7%reptop_rep_<positive_number>%");
            }

            try {
                text = String.valueOf(plugin.getRepDB().getTop(number).getReputation());
            } catch (SQLException | NullPointerException e) {
                text = "0";
            }
        }
        return text;
    }
}
