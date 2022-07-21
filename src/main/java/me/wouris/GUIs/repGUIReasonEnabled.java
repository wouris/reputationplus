package me.wouris.GUIs;

import me.wouris.main;
import me.wouris.model.reasonStats;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class repGUIReasonEnabled {

    public static Inventory createGUI(Player p, OfflinePlayer target, main plugin, Config config) throws SQLException {
        String guiTitle = Placeholder.setPlaceholders(plugin, config.getRepGUITitle(), target, p);
        Inventory inv = Bukkit.createInventory(p, 9*4, ChatUtils.format(guiTitle));

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

        ItemStack sign = setBlocks.setBlock( plugin,
                Material.OAK_SIGN,
                config.getSignTitle(),
                null,
                target);

        List<reasonStats> latestVoters = plugin.getReasonDB().getLatestVoters(target.getUniqueId().toString());

        ItemStack[] array = new ItemStack[9*4];
        for (int i = 0; i < 9*4; i++){
            switch (i){
                case 3 -> array[i] = repplus;
                case 4 -> array[i] = playerHead;
                case 5 -> array[i] = repminus;
                case 13 -> array[i] = sign;
                case 19, 20, 21, 22, 23, 24, 25 -> {
                    try{
                        String text = null;
                        OfflinePlayer rater = Bukkit.getOfflinePlayer(UUID.fromString(latestVoters.get(i-19).getRater()));
                        String reason = latestVoters.get(i-19).getReason();

                        String decisionSQL = latestVoters.get(i-19).getDecision();
                        String decision;
                        if (decisionSQL.equals("positive"))
                            decision = "&a+rep";
                        else
                            decision = "&c-rep";

                        String configTitle = config.getLatestVoterTitle();
                        // need to overwrite placeholder handling for this
                        if (config.getLatestVoterTitle().contains("%rep_player%")){
                            configTitle = configTitle.replaceAll("%rep_player%", rater.getName());
                        }
                        configTitle = Placeholder.setPlaceholders(plugin, configTitle, target, p);
                        configTitle = configTitle.replaceAll("%rep_decision%", decision);
                        configTitle = configTitle.replaceAll("%rep_reason%", reason);

                        List<String> configDescription = config.getLatestVoterDesc();
                        for(int j = 0; j < configDescription.size(); j++){

                            configDescription.set(j, ChatUtils.format(Placeholder.setPlaceholders(plugin,
                                    configDescription.get(j).replaceAll("%rep_decision%", decision), target, p)));

                            configDescription.set(j,ChatUtils.format( configDescription.get(j).replaceAll("%rep_reason%", reason)));

                            Timestamp lastVote;
                            lastVote = latestVoters.get(i-19).getDate();
                            PrettyTime time = new PrettyTime();
                            configDescription.set(j,ChatUtils.format( configDescription.get(j).replaceAll("%rep_time_ago%", time.format(lastVote))));
                        }

                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) skull.getItemMeta();
                        meta.setOwningPlayer(rater);
                        meta.setLore(configDescription);
                        meta.setDisplayName(ChatUtils.format(configTitle));
                        skull.setItemMeta(meta);
                        array[i] = skull;




//                        ItemStack skull = setBlocks.setSkullLatestVoters(rater, plugin, text, Collections.singletonList(reason), target);
//                                Placeholder.setPlaceholders(plugin, config.getLatestVoterTitle(), target, rater),
//                                // placeholders will be set in setSkull method
//                                config.getLatestVoterDesc(),
//                                reason,
//                               target);
//                        array[i] = skull;
                    } catch (IndexOutOfBoundsException ignored){ /*place nothing */ }
                }
                default -> array[i] = filler;
            }
        }
        inv.setContents(array);

        return inv;
    }
}
