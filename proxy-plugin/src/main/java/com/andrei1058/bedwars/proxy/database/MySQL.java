package com.andrei1058.bedwars.proxy.database;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL implements Database {

    private Connection connection;
    private final String host, database, user, pass;
    private final int port;
    private final boolean ssl;

    public MySQL() {
        this.host = BedWarsProxy.config.getYml().getString("database.host");
        this.database = BedWarsProxy.config.getYml().getString("database.database");
        this.user = BedWarsProxy.config.getYml().getString("database.user");
        this.pass = BedWarsProxy.config.getYml().getString("database.pass");
        this.port = BedWarsProxy.config.getYml().getInt("database.port");
        this.ssl = BedWarsProxy.config.getYml().getBoolean("database.ssl");

        if (!connect()) {
            BedWarsProxy.setRemoteDatabase(new NoDatabase());
        } else {
            init();
        }
    }

    public boolean connect() {
        try {
            String url = String.format(
                    "jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=%s&useUnicode=true&characterEncoding=UTF-8",
                    host, port, database, ssl
            );
            connection = DriverManager.getConnection(url, user, pass);
            return true;
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to connect to MySQL", e);
            return false;
        }
    }

    /**
     * Check if database is connected.
     */
    public boolean isConnected() {
        try {
            return connection != null && connection.isValid(2);
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "MySQL connection check failed", e);
            return false;
        }
    }

    public void init() {
        if (!isConnected()) connect();

        try (Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS global_stats (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(200)," +
                    "uuid VARCHAR(200)," +
                    "first_play TIMESTAMP NULL DEFAULT NULL," +
                    "last_play TIMESTAMP NULL DEFAULT NULL," +
                    "wins INT," +
                    "kills INT," +
                    "final_kills INT," +
                    "looses INT," +
                    "deaths INT," +
                    "final_deaths INT," +
                    "beds_destroyed INT," +
                    "games_played INT" +
                    ");");

            s.executeUpdate("CREATE TABLE IF NOT EXISTS quick_buy (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(200)," +
                    "slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200)," +
                    "slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200)," +
                    "slot_25 VARCHAR(200), slot_28 VARCHAR(200), slot_29 VARCHAR(200)," +
                    "slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200)," +
                    "slot_33 VARCHAR(200), slot_34 VARCHAR(200), slot_37 VARCHAR(200)," +
                    "slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200)," +
                    "slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200)" +
                    ");");

            s.executeUpdate("CREATE TABLE IF NOT EXISTS player_levels (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(200)," +
                    "level INT," +
                    "xp INT," +
                    "name VARCHAR(200) CHARACTER SET utf8," +
                    "next_cost INT" +
                    ");");

            s.executeUpdate("CREATE TABLE IF NOT EXISTS player_language (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(200)," +
                    "iso VARCHAR(200)" +
                    ");");

        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to initialize MySQL tables", e);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(BedWarsProxy.getPlugin(), new SessionKeeper(this), 20 * 60, 20 * 3600);
    }

    public boolean hasStats(UUID uuid) {
        if (!isConnected()) connect();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM global_stats WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to check player stats", e);
            return false;
        }
    }

    public void updateLocalCache(UUID uuid) {
        if (!isConnected()) connect();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM global_stats WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StatsCache cs = BedWarsProxy.getStatsCache();
                    cs.setFirstPlay(uuid, rs.getTimestamp("first_play"));
                    cs.setLastPlay(uuid, rs.getTimestamp("last_play"));
                    cs.setWins(uuid, rs.getInt("wins"));
                    cs.setKills(uuid, rs.getInt("kills"));
                    cs.setFinalKills(uuid, rs.getInt("final_kills"));
                    cs.setLosses(uuid, rs.getInt("looses"));
                    cs.setDeaths(uuid, rs.getInt("deaths"));
                    cs.setFinalDeaths(uuid, rs.getInt("final_deaths"));
                    cs.setBedsDestroyed(uuid, rs.getInt("beds_destroyed"));
                    cs.setGamesPlayed(uuid, rs.getInt("games_played"));
                }
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to update local cache", e);
        }
    }

    public Object[] getLevelData(UUID player) {
        if (!isConnected()) connect();
        Object[] result = new Object[]{1, 0, "", 0};

        try (PreparedStatement ps = connection.prepareStatement("SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = ?")) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getInt("level");
                    result[1] = rs.getInt("xp");
                    result[2] = rs.getString("name");
                    result[3] = rs.getInt("next_cost");
                }
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to get player level data", e);
        }
        return result;
    }

    public void setLevelData(UUID player, int level, int xp, String displayName, int nextCost) {
        if (!isConnected()) connect();

        try (PreparedStatement check = connection.prepareStatement("SELECT id FROM player_levels WHERE uuid = ?")) {
            check.setString(1, player.toString());
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO player_levels (uuid, level, xp, name, next_cost) VALUES (?, ?, ?, ?, ?)")) {
                        insert.setString(1, player.toString());
                        insert.setInt(2, level);
                        insert.setInt(3, xp);
                        insert.setString(4, displayName);
                        insert.setInt(5, nextCost);
                        insert.executeUpdate();
                    }
                } else {
                    String sql = (displayName != null) ?
                            "UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid=?" :
                            "UPDATE player_levels SET level=?, xp=? WHERE uuid=?";
                    try (PreparedStatement update = connection.prepareStatement(sql)) {
                        update.setInt(1, level);
                        update.setInt(2, xp);
                        if (displayName != null) {
                            update.setString(3, displayName);
                            update.setInt(4, nextCost);
                            update.setString(5, player.toString());
                        } else {
                            update.setString(3, player.toString());
                        }
                        update.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to set player level data", e);
        }
    }

    public void setLanguage(UUID player, String iso) {
        if (!isConnected()) connect();
        try (PreparedStatement check = connection.prepareStatement("SELECT id FROM player_language WHERE uuid = ?")) {
            check.setString(1, player.toString());
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement update = connection.prepareStatement(
                            "UPDATE player_language SET iso = ? WHERE uuid = ?")) {
                        update.setString(1, iso);
                        update.setString(2, player.toString());
                        update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO player_language (uuid, iso) VALUES (?, ?)")) {
                        insert.setString(1, player.toString());
                        insert.setString(2, iso);
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to set player language", e);
        }
    }

    public String getLanguage(UUID player) {
        if (!isConnected()) connect();
        String defaultIso = LanguageManager.get().getDefaultLanguage().getIso();
        try (PreparedStatement ps = connection.prepareStatement("SELECT iso FROM player_language WHERE uuid = ?"))        {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("iso");
                }
            }
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to get player language", e);
        }
        return defaultIso;
    }

    public void ping() {
        try (Statement s = connection.createStatement()) {
            s.execute("SELECT id FROM player_levels WHERE id=0;");
        } catch (SQLException e) {
            BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to ping database", e);
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                BedWarsProxy.getPlugin().getLogger().log(Level.SEVERE, "Failed to close MySQL connection", e);
            }
        }
    }
}


