package com.andrei1058.bedwars.proxy.api.event;

import com.andrei1058.bedwars.proxy.api.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public class PlayerLangChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private Player player;
    private Language oldLang, newLang;

    /**
     * Called when a Player changes his language.
     *
     * @param p       target player.
     * @param oldLang old language.
     * @param newLang new language.
     */
    public PlayerLangChangeEvent(Player p, Language oldLang, Language newLang) {
        this.player = p;
        this.oldLang = oldLang;
        this.newLang = newLang;
    }

    /**
     * Check if event is cancelled.
     *
     * @return true if canceled.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Change value.
     *
     * @param cancelled cancel event.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get Player.
     *
     * @return player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get old Language.
     *
     * @return old language.
     */
    public Language getOldLang() {
        return oldLang;
    }


    /**
     * Get new Language.
     *
     * @return new language.
     */
    public Language getNewLang() {
        return newLang;
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
