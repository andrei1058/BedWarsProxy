package com.andrei1058.bedwars.proxy.api;

import java.util.UUID;

@Deprecated
public interface RemoteReJoin {

    /**
     * Remove re-join data.
     */
    void destroy();

    /**
     * Get user uuid.
     *
     * @return user uuid.
     */
    UUID getUUID();

    /**
     * Get arena.
     *
     * @return arena.
     **/
    CachedArena getArena();
}
