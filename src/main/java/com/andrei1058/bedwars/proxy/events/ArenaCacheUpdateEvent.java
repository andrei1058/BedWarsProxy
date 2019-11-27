package com.andrei1058.bedwars.proxy.events;

import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaCacheUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private CachedArena arena;

    /**
     * Called when an arena is enabled successfully. It's called after a restart as well.
     */
    public ArenaCacheUpdateEvent(CachedArena a) {
        this.arena = a;
    }

    /**
     * Get the arena
     */
    public CachedArena getArena() {
        return arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
