package me.wouris.utils;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private final Plugin plugin;

    public Config(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getHost() {
        return plugin.getConfig().getString("sql-connection.host");
    }

    public String getPort() {
        return plugin.getConfig().getString("sql-connection.port");
    }

    public String getDatabase() {
        return plugin.getConfig().getString("sql-connection.database");
    }

    public String getUser() {
        return plugin.getConfig().getString("sql-connection.user");
    }

    public String getPassword() {
        return plugin.getConfig().getString("sql-connection.password");
    }

    public int getMaxVotes() {
        return plugin.getConfig().getInt("options.maxVotes");
    }

    public String getMaxVotesMessage() {
        return plugin.getConfig().getString("options.maxVotes-message");
    }

    public boolean getUsePrefix() {
        return plugin.getConfig().getBoolean("options.use-prefix");
    }

    public String getPrefix() {
        return plugin.getConfig().getString("options.prefix");
    }

    public boolean getCanSelfVote() {
        return plugin.getConfig().getBoolean("options.can-self-vote");
    }

    public String getNoSelfVoteMessage() {
        return plugin.getConfig().getString("options.no-self-vote-message");
    }

    public boolean getUsePrefixRepCommand() {
        return plugin.getConfig().getBoolean("options.rep-command.use-prefix");
    }

    public List<String> getRepCommandMessages() {
        return plugin.getConfig().getStringList("options.rep-command.messages");
    }

    public boolean getUseInterval() {
        return plugin.getConfig().getBoolean("options.interval-options.enabled");
    }

    public String getInterval() {
        return plugin.getConfig().getString("options.interval-options.interval");
    }

    public String getIntervalMessage() {
        return plugin.getConfig().getString("options.interval-options.message");
    }

    public String getRepGUITitle() {
        return plugin.getConfig().getString("options.gui-options.title");
    }

    public boolean getShowRecentReasons() {
        return plugin.getConfig().getBoolean("options.gui-options.show-latest-voters.enabled");
    }

    public String getSignTitle() {
        return plugin.getConfig().getString("options.gui-options.show-latest-voters.sign-title");
    }

    public String getTargetColor() {
        return plugin.getConfig().getString("options.gui-options.target-head.name-color");
    }

    public List<String> getTargetDescription() {
        return plugin.getConfig().getStringList("options.gui-options.target-head.description");
    }

    public String getPlusRepBlock() {
        return plugin.getConfig().getString("options.gui-options.+rep-button.block");
    }

    public String getPlusRepButtonTitle() {
        return plugin.getConfig().getString("options.gui-options.+rep-button.title");
    }

    public List<String> getPlusRepButtonDescription() {
        return plugin.getConfig().getStringList("options.gui-options.+rep-button.description");
    }

    public String getMinusRepBlock() {
        return plugin.getConfig().getString("options.gui-options.-rep-button.block");
    }

    public String getMinusRepButtonTitle() {
        return plugin.getConfig().getString("options.gui-options.-rep-button.title");
    }

    public List<String> getMinusRepButtonDescription() {
        return plugin.getConfig().getStringList("options.gui-options.-rep-button.description");
    }

    public String getFillerBlock() {
        return plugin.getConfig().getString("options.gui-options.filler.block");
    }

    public String getPlusRepMessage() {
        return plugin.getConfig().getString("messages.+rep-message");
    }

    public String getPlusRepHoverMessage() {
        return plugin.getConfig().getString("messages.+rep-hover-message");
    }

    public String getMinusRepMessage() {
        return plugin.getConfig().getString("messages.-rep-message");
    }

    public String getMinusRepHoverMessage() {
        return plugin.getConfig().getString("messages.-rep-hover-message");
    }

    public String getTargetPlusRepMessage() {
        return plugin.getConfig().getString("messages.+rep-message-target");
    }

    public String getTargetPlusRepHoverMessage() {
        return plugin.getConfig().getString("messages.+rep-hover-message-target");
    }

    public String getPlayerNeverSeenMessage() {
        return plugin.getConfig().getString("messages.player-never-seen");
    }

    public String getNoPermissionMessage() {
        return plugin.getConfig().getString("messages.no-permission");
    }

    public String getConfigReloadMessage() {
        return plugin.getConfig().getString("messages.config-reload");
    }

    public String getPositiveReasonGUITitle(){
        return plugin.getConfig().getString("options.gui-options.reason-gui-options.positive-title");
    }

    public String getNegativeReasonGUITitle(){
        return plugin.getConfig().getString("options.gui-options.reason-gui-options.negative-title");
    }

    public List<String> getAllPlusRepReasons(){
        List<String> reasons = new ArrayList<>();
        for (String key : plugin.getConfig().getConfigurationSection(
                "options.gui-options.reason-gui-options.+rep-reasons").getKeys(false)){
            reasons.add(key);
        }
        return reasons;
    }

    public List<String> getAllMinusRepReasons(){
        List<String> reasons = new ArrayList<>();
        for (String key : plugin.getConfig().getConfigurationSection(
                "options.gui-options.reason-gui-options.-rep-reasons").getKeys(false)){
            reasons.add(key);
        }
        return reasons;
    }

    public String getReasonName(String plusMinus, String path){
        if (plusMinus.equals("plus"))
            return plugin.getConfig().getString("options.gui-options.reason-gui-options.+rep-reasons."
                    + path + ".name");
        else
            return plugin.getConfig().getString("options.gui-options.reason-gui-options.-rep-reasons."
                    + path + ".name");
    }

    public String getReasonBlock(String plusMinus, String path){
        if (plusMinus.equals("plus"))
            return plugin.getConfig().getString("options.gui-options.reason-gui-options.+rep-reasons."
                    + path + ".material");
        else
            return plugin.getConfig().getString("options.gui-options.reason-gui-options.-rep-reasons."
                    + path + ".material");
    }

    public List<String> getReasonDesc(String plusMinus, String path){
        if (plusMinus.equals("plus"))
            return plugin.getConfig().getStringList("options.gui-options.reason-gui-options.+rep-reasons."
                    + path + ".description");
        else
            return plugin.getConfig().getStringList("options.gui-options.reason-gui-options.-rep-reasons."
                    + path + ".description");
    }

    public String getLatestVoterTitle(){
        return plugin.getConfig().getString("options.gui-options.show-latest-voters.voter-head.title");
    }

    public List<String> getLatestVoterDesc(){
        return plugin.getConfig().getStringList("options.gui-options.show-latest-voters.voter-head.description");
    }

    public String getCustomReasonGUITitle(){
        return plugin.getConfig().getString("options.gui-options.reason-gui-options.custom-reason.title");
    }

    public String getCustomReasonBlock(){
        return plugin.getConfig().getString("options.gui-options.reason-gui-options.custom-reason.material");
    }

    public List<String> getCustomReasonDesc(){
        return plugin.getConfig().getStringList("options.gui-options.reason-gui-options.custom-reason.description");
    }

    public int getRepTopLimit(){
        return plugin.getConfig().getInt("options.reptop-command.limit");
    }

    public String getRepTopNoPlayers(){
        return plugin.getConfig().getString("options.reptop-command.no-players-found");
    }

    public List<String> getRepTopFormat(){
        return plugin.getConfig().getStringList("options.reptop-command.format");
    }
}
