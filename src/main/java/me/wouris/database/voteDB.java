package me.wouris.database;

import me.wouris.main;
import me.wouris.model.voteStats;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class voteDB {

    private final main plugin;

    public voteDB(main plugin) {
        this.plugin = plugin;
    }

    public void initialize() throws SQLException {
        Statement statement = this.plugin.getRepDB().getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS votes(" +
                "uuid VARCHAR(36), " +
                "target VARCHAR(255), " +
                "votes INT)";
        statement.execute(sql);
        try{
            sql = "ALTER TABLE votes ADD COLUMN reason VARCHAR(255)";
            statement.execute(sql);
        } catch (SQLException ignored){
            // Column already exists
        }
        statement.close();
    }

    public boolean hasReachedMaxVotes(UUID player, UUID target) throws SQLException {
        PreparedStatement statement = this.plugin.getRepDB().getConnection()
                .prepareStatement("SELECT votes FROM votes WHERE uuid = ? AND target = ?");
        statement.setString(1, player.toString());
        statement.setString(2, target.toString());

        ResultSet result = statement.executeQuery();


        if (result.next()){
            int votes = result.getInt("votes");
            statement.close();
            return votes < this.plugin.getConfig().getInt("options.maxVotes");
        }

        statement.close();
        return true;
    }

    public voteStats getStats(UUID player, UUID target) throws SQLException{
        PreparedStatement statement = this.plugin.getRepDB().getConnection()
                .prepareStatement("SELECT * FROM votes WHERE uuid = ? AND target = ?");
        statement.setString(1, player.toString());
        statement.setString(2, target.toString());
        ResultSet result = statement.executeQuery();

        if (result.next()){

            int votes = result.getInt("votes");
            voteStats voteStats = new voteStats(player, target, votes);

            statement.close();
            return voteStats;
        }
        statement.close();
        return null;
    }

    public void createStats(voteStats stats) throws SQLException{
        PreparedStatement statement = this.plugin.getRepDB().getConnection()
                .prepareStatement("INSERT INTO votes (uuid, target, votes) VALUES (?,?,?)");

        statement.setString(1, stats.getPlayer().toString());
        statement.setString(2, stats.getTarget().toString());
        statement.setInt(3, stats.getVotes());

        statement.executeUpdate();
        statement.close();
    }

    public void updateVotes(voteStats stats) throws SQLException{
        PreparedStatement statement = this.plugin.getRepDB().getConnection()
                .prepareStatement("UPDATE votes SET votes = ? WHERE uuid = ? AND target = ?");

        statement.setInt(1, stats.getVotes());
        statement.setString(2, stats.getPlayer().toString());
        statement.setString(3, stats.getTarget().toString());

        statement.executeUpdate();
        statement.close();
    }
}
