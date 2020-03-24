package com.andrei1058.bedwars.proxy.database;

import java.util.UUID;

public interface Database {

    /**
     * Initialize database.
     */
    void init();

    /**
     * Update local cache from remote database.
     */
    void updateLocalCache(UUID uuid);

    /**
     * Close connection.
     */
    void close();

    /**
     * Get a player level and xp.
     * <p>
     * args 0 is level.
     * args 1 is xp.
     * args 2 is display name.
     * args 3 next level cost.
     */
    Object[] getLevelData(UUID player);

    /**
     * Set a player level data.
     */
    void setLevelData(UUID player, int level, int xp, String displayName, int nextCost);

    /**
     * Set a player language.
     */
    void setLanguage(UUID player, String iso);

    /**
     * Get a player language.
     */
    String getLanguage(UUID player);
}
