package com.andrei1058.bedwars.proxy.event;

import com.andrei1058.bedwars.proxy.arenamanager.CachedArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaCacheRemoveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private CachedArena arena;

    /**
     * Called when an arena is removed from the list.
     */
    public ArenaCacheRemoveEvent(CachedArena a) {
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
