package com.andrei1058.bedwars.proxy.api.event;

import com.andrei1058.bedwars.proxy.api.CachedArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public class PlayerReJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private CachedArena arena;

    /**
     * Called when a player rejoins an arena.
     * This is called before sending him to the server.
     *
     * @param player player.
     * @param arena  arena.
     */
    public PlayerReJoinEvent(Player player, CachedArena arena) {
        this.player = player;
        this.arena = arena;
    }

    /**
     * Get player.
     *
     * @return player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get arena.
     *
     * @return arena.
     */
    public CachedArena getArena() {
        return arena;
    }

    /**
     * Bukkit stuff.
     *
     * @return handlers.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Bukkit stuff.
     *
     * @return handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
