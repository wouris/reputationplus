package me.wouris.database;

import me.wouris.main;
import me.wouris.model.reputationStats;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class reputationDB {

    private Connection connection;
    private final main plugin;

    public reputationDB(main plugin) {
        this.plugin = plugin;
    }

    public Connection getConnection() throws SQLException {
        if (connection != null){
            return connection;
        }

        String sqlURL = "jdbc:mysql://" +
                this.plugin.getConfig().getString("sql-connection.host") + ":" +
                this.plugin.getConfig().getString("sql-connection.port") + "/" +
                this.plugin.getConfig().getString("sql-connection.database");
        String user = this.plugin.getConfig().getString("sql-connection.user");
        String password = this.plugin.getConfig().getString("sql-connection.password");

        this.connection = DriverManager.getConnection(sqlURL, user, password);
        return this.connection;
    }

    public void initialize() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS reputation(" +
                "uuid VARCHAR(36) primary key, " +
                "reputation INT, " +
                "votes INT, " +
                "lastVote TIMESTAMP NULL DEFAULT NULL)";
        statement.execute(sql);
        statement.close();
    }

    public reputationStats getStats(UUID uuid) throws SQLException{
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM reputation WHERE uuid = ?");
        statement.setString(1, uuid.toString());
        ResultSet result = statement.executeQuery();
        if (result.next()){

            int rep = result.getInt("reputation");
            int votes = result.getInt("votes");
            Timestamp lastVote = result.getTimestamp("lastVote");

            reputationStats repStats = new reputationStats(uuid, rep, votes, lastVote);

            statement.close();
            return repStats;
        }
        statement.close();
        return null;
    }

    public void createStats(reputationStats stats) throws SQLException{
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO reputation (uuid, reputation, votes, lastVote) VALUES (?,?,?,?)");

        statement.setString(1, stats.getUUID().toString());
        statement.setInt(2, stats.getReputation());
        statement.setInt(3, stats.getVotes());
        statement.setTimestamp(4, stats.getLastVote());

        statement.executeUpdate();
        statement.close();
    }

    public void updateReputation(reputationStats stats) throws SQLException{
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE reputation SET reputation = ? WHERE uuid = ?");

        statement.setString(2, stats.getUUID().toString());
        statement.setInt(1, stats.getReputation());

        statement.executeUpdate();
        statement.close();
    }

    public void updateVotes(reputationStats stats) throws SQLException{
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE reputation SET votes = ?, lastVote = ? WHERE uuid = ?");

        statement.setInt(1, stats.getVotes());
        statement.setTimestamp(2, stats.getLastVote());
        statement.setString(3, stats.getUUID().toString());


        statement.executeUpdate();
        statement.close();
    }

    public List<reputationStats> getTopList() throws SQLException{
        List<reputationStats> repList = new ArrayList<>();

        Statement statement = getConnection().createStatement();
        String sql = "SELECT * FROM reputation ORDER BY reputation DESC LIMIT 5";
        ResultSet result = statement.executeQuery(sql);

        while (result.next()){
            UUID uuid = UUID.fromString(result.getString("uuid"));
            int rep = result.getInt("reputation");
            reputationStats repStats = new reputationStats(uuid, rep);
            repList.add(repStats);
        }

        statement.close();
        return repList;
    }
}
