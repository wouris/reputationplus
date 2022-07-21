package me.wouris.database;

import me.wouris.main;
import me.wouris.model.reasonStats;
import me.wouris.model.reputationStats;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// the reason why am I making reason SQL instead of json is that I just could not find any suitable solution for my needs
public class reasonDB {

    private main plugin;

    public reasonDB(main plugin){ this.plugin = plugin;}

    public void initialize() throws SQLException {
        Statement statement = plugin.getRepDB().getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS reasons(" +
                "target VARCHAR(36), " +
                "rater VARCHAR(36), " +
                "decision VARCHAR(8), " +
                "reason VARCHAR(255), " +
                "votedOn TIMESTAMP NULL DEFAULT NULL)";
        statement.execute(sql);
        statement.close();
    }

    public reasonStats getReason(UUID target, UUID player) throws SQLException{
        PreparedStatement statement = plugin.getRepDB().getConnection()
                .prepareStatement("SELECT * FROM reasons WHERE target = ? AND rater = ?");
        statement.setString(1, target.toString());
        statement.setString(2, player.toString());
        ResultSet result = statement.executeQuery();
        if (result.next()){
            return getReasonStats(target, statement, result);
        }
        else
            return null;
    }

    public reasonStats getDecision(UUID target, UUID player, Timestamp time) throws SQLException{
        PreparedStatement statement = plugin.getRepDB().getConnection()
                .prepareStatement("SELECT * FROM reasons WHERE target = ? AND rater = ? AND votedOn = ?");
        statement.setString(1, target.toString());
        statement.setString(2, player.toString());
        statement.setTimestamp(3, time);
        ResultSet result = statement.executeQuery();
        if (result.next())
            return getReasonStats(target, statement, result);
        else
            return null;
    }

    private reasonStats getReasonStats(UUID target, PreparedStatement statement, ResultSet result) throws SQLException {
        String reason = result.getString("reason");
        String rater = result.getString("rater");
        String decision = result.getString("decision");
        Timestamp votedOn = result.getTimestamp("votedOn");

        reasonStats stats = new reasonStats(target.toString(), rater, decision, reason, votedOn);
        statement.close();
        return stats;
    }

    public void createReason(reasonStats stats) throws SQLException{
        PreparedStatement statement = plugin.getRepDB().getConnection()
                .prepareStatement("INSERT INTO reasons (target, rater, decision,reason, votedOn) VALUES (?,?,?,?,?)");

        statement.setString(1, stats.getTarget());
        statement.setString(2, stats.getRater());
        statement.setString(3, stats.getDecision());
        statement.setString(4, stats.getReason());
        statement.setTimestamp(5, stats.getDate());

        statement.executeUpdate();
        statement.close();
    }

    public void removeReason(reasonStats stats) throws SQLException{
        PreparedStatement statement = plugin.getRepDB().getConnection()
                .prepareStatement("DELETE FROM reasons WHERE target = ? AND rater = ?");

        statement.setString(1, stats.getTarget());
        statement.setString(2, stats.getRater());

        statement.executeUpdate();
        statement.close();
    }

    public List<reasonStats> getLatestVoters(String target) throws SQLException{
        List<reasonStats> voterList = new ArrayList<>();

        int size = 0;

        PreparedStatement statement = plugin.getRepDB().getConnection()
                .prepareStatement("SELECT * FROM reasons WHERE target = ? ORDER BY votedOn DESC");
        statement.setString(1, target);
        ResultSet result = statement.executeQuery();

        ResultSet rowCount;
        if (result.next()){
            PreparedStatement statement1 = plugin.getRepDB().getConnection().prepareStatement("SELECT COUNT(*) FROM reasons WHERE target = ?");
            statement1.setString(1, result.getString("target"));
            rowCount = statement1.executeQuery();
            if (rowCount.next()){
                size = rowCount.getInt(1);
            }
            statement1.close();
        }

        if (size > 7){
            PreparedStatement statement1 = plugin.getRepDB().getConnection()
                    .prepareStatement("DELETE FROM reasons WHERE target = ? ORDER BY votedOn ASC LIMIT 1;");
            statement1.setString(1, result.getString("target"));
            statement1.executeUpdate();
            statement1.close();
            size = 7;
        }
        if (size != 0){
            for (int i = 0; i < size; i++){
                String rater = result.getString("rater");
                String reason = result.getString("reason");
                String decision = result.getString("decision");
                Timestamp votedOn = result.getTimestamp("votedOn");
                reasonStats stats = new reasonStats(target, rater, decision, reason, votedOn);
                voterList.add(stats);
                result.next();
            }
        }
        statement.close();
        return voterList;
    }
}
