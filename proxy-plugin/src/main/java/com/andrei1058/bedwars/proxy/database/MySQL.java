package com.andrei1058.bedwars.proxy.database;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class MySQL implements Database{

    private Connection connection;
    private String host, database, user, pass;
    private int port;
    private boolean ssl;

    /**
     * Create new MySQL connection.
     */
    public MySQL() {
        this.host = BedWarsProxy.config.getYml().getString("database.host");
        this.database = BedWarsProxy.config.getYml().getString("database.database");
        this.user = BedWarsProxy.config.getYml().getString("database.user");
        this.pass = BedWarsProxy.config.getYml().getString("database.pass");
        this.port = BedWarsProxy.config.getYml().getInt("database.port");
        this.ssl = BedWarsProxy.config.getYml().getBoolean("database.ssl");
        if (!connect()){
            BedWarsProxy.setRemoteDatabase(new NoDatabase());
        }
    }

    /**
     * Connect to remote database.
     *
     * @return true if connected successfully.
     */
    public boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&user=" + user
                    + "&password=" + pass + "&useSSL=" + ssl + "&useUnicode=true&characterEncoding=UTF-8");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if database is connected.
     */
    public boolean isConnected() {
        if (connection == null) return false;
        try {
            return connection.isValid(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if player has remote stats.
     */
    public boolean hasStats(UUID uuid) {
        if (!isConnected()) connect();
        boolean result = false;
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT id FROM global_stats WHERE uuid = '" + uuid.toString() + "';")) {
            result =  rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void init() {
        if (!isConnected()) connect();
        try (Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS global_stats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(200), uuid VARCHAR(200), first_play TIMESTAMP NULL DEFAULT NULL, " +
                    "last_play TIMESTAMP NULL DEFAULT NULL, wins INT(200), kills INT(200), " +
                    "final_kills INT(200), looses INT(200), deaths INT(200), final_deaths INT(200), beds_destroyed INT(200), games_played INT(200));");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS quick_buy (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), " +
                    "slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200)," +
                    "slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200)," +
                    "slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS player_levels (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), " +
                    "level INT(200), xp INT(200), name VARCHAR(200) CHARACTER SET utf8, next_cost INT(200));");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement s = connection.createStatement()) {

            s.executeUpdate("CREATE TABLE IF NOT EXISTS player_language (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), " +
                    "iso VARCHAR(200));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement s = connection.createStatement()) {

            s.executeUpdate("CREATE TABLE IF NOT EXISTS WinStreaks (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(200), uuid VARCHAR(200), win_streak INT(200));");

        } catch (SQLException e) {
            e.printStackTrace();
        }



        Bukkit.getScheduler().runTaskTimerAsynchronously(BedWarsProxy.getPlugin(), new SessionKeeper(this), 20*60, 20*3600);
    }

    public void updateLocalCache(UUID uuid) {
        if (!isConnected()) connect();
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM global_stats WHERE uuid = '" + uuid.toString() + "';")) {
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
                cs.setWinStreak(uuid, rs.getInt("win_streak"));//TODO:WINSTREAK
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM WinStreaks WHERE uuid = '" + uuid.toString() + "';")) {
            if (rs.next()) {
                StatsCache cs = BedWarsProxy.getStatsCache();
                cs.setWinStreak(uuid, rs.getInt("win_streak"));//TODO:WINSTREAK
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Object[] getLevelData(UUID player) {
        if (!isConnected()) connect();
        Object[] r = new Object[]{1, 0, "", 0};
        try (ResultSet rs = connection.prepareStatement("SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = '" + player.toString() + "';").executeQuery()) {
            if (rs.next()) {
                r[0] = rs.getInt("level");
                r[1] = rs.getInt("xp");
                r[2] = rs.getString("name");
                r[3] = rs.getInt("next_cost");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public void setLevelData(UUID player, int level, int xp, String displayName, int nextCost) {
        if (!isConnected()) connect();
        try  (ResultSet rs = connection.prepareStatement("SELECT id from player_levels WHERE uuid = '" + player.toString() + "';").executeQuery()) {
            if (!rs.next()) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO player_levels VALUES (?, ?, ?, ?, ?, ?);");
                ps.setInt(1, 0);
                ps.setString(2, player.toString());
                ps.setInt(3, level);
                ps.setInt(4, xp);
                ps.setString(5, displayName);
                ps.setInt(6, nextCost);
                ps.executeUpdate();
            } else {
                PreparedStatement ps;
                if (displayName == null) {
                    ps = connection.prepareStatement("UPDATE player_levels SET level=?, xp=? WHERE uuid = '" + player.toString() + "';");
                } else {
                    ps = connection.prepareStatement("UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid = '" + player.toString() + "';");
                }
                ps.setInt(1, level);
                ps.setInt(2, xp);
                if (displayName != null) {
                    ps.setString(3, displayName);
                    ps.setInt(4, nextCost);
                }
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLanguage(UUID player, String iso) {
        if (!isConnected()) connect();
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT iso FROM player_language WHERE uuid = '" + player.toString() + "';")) {
            Statement s = connection.createStatement();
            if (rs.next()) {
                s.executeUpdate("UPDATE player_language SET iso='" + iso + "' WHERE uuid = '" + player.toString() + "';");
            } else {
                s.executeUpdate("INSERT INTO player_language VALUES (0, '" + player.toString() + "', '" + iso + "');");
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLanguage(UUID player) {
        if (!isConnected()) connect();
        String iso = LanguageManager.get().getDefaultLanguage().getIso();
        try (ResultSet rs = connection.createStatement().executeQuery("SELECT iso FROM player_language WHERE uuid = '" + player.toString() + "';")) {
            if (rs.next()) {
                iso = rs.getString("iso");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return iso;
    }

    /**
     * Ping the database in order to keep the session open.
     */
    public void ping() {
        try (Statement s = connection.createStatement()) {
            s.execute("SELECT id FROM player_levels WHERE id=0;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
