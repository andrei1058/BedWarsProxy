package com.andrei1058.bedwars.proxy.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface BedWars {

    /**
     * Get language util.
     *
     * @return utils.
     */
    LanguageUtil getLanguageUtil();

    interface LanguageUtil {
        /**
         * Save default data if not exists in the language files.
         * Save a default message on all languages files.
         *
         * @param path message path.
         * @param data message value.
         */
        void saveIfNotExists(String path, Object data);

        /**
         * Get installed language list.
         *
         * @return available languages list.
         */
        List<Language> getLanguages();

        /**
         * Get message in player's language.
         *
         * @param p    target player.
         * @param path message path.
         * @return color translated message.
         */
        String getMsg(Player p, String path);

        /**
         * Retrieve a player language.
         *
         * @param p target player.
         * @return player language.
         */
        Language getPlayerLanguage(Player p);

        /**
         * Get a string list in player's language.
         *
         * @param p    target player.
         * @param path list path.
         * @return translated list with translated colors.
         */
        List<String> getList(Player p, String path);

        /**
         * Check if a language is loaded.
         * Iso example: en, ro etc.
         *
         * @param iso language iso code.
         * @return true if the language is loaded.
         */
        boolean isLanguageExist(String iso);

        /**
         * Add a language to the list.
         *
         * @param language new language.
         * @return true if the language was added successfully.
         */
        boolean addLanguage(Language language);

        /**
         * Get a language by iso code.
         *
         * @param iso language code. Ex: ro, en.
         * @return NULL if not found.
         */
        Language getLang(String iso);

        /**
         * Change server default language.
         *
         * @param defaultLanguage language.
         */
        void setDefaultLanguage(Language defaultLanguage);


        /**
         * Get server default language.
         *
         * @return server default language.
         */
        Language getDefaultLanguage();
    }

    interface ArenaUtil {
        /**
         * Destroy available rejoins for the given arena.
         *
         * @param arena target arena.
         */
        void destroyReJoins(CachedArena arena);

        /**
         * Get available rejoin session for a player.
         *
         * @param player player uuid.
         * @return NULL if not found.
         */
        RemoteReJoin getReJoin(UUID player);

        /**
         * Get an arena by server and map identifier.
         *
         * @param server           server id.
         * @param remoteIdentifier remote map identifier.
         * @return arena.
         */
        CachedArena getArena(String server, String remoteIdentifier);

        /**
         * Add a player to the most filled arena from a group.
         *
         * @param p     target player.
         * @param group arena group.
         * @return true if joined successfully.
         */
        boolean joinRandomFromGroup(Player p, String group);

        /**
         * Add a player to the most filled arena.
         * Check if is the party owner first.
         *
         * @param p target player.
         * @return true if joined successfully.
         */
        boolean joinRandomArena(Player p);

        /**
         * Remove an arena and destroy its data.
         * @param a arena.
         */
        void disableArena(CachedArena a);
    }

    /**
     * Get arena util.
     *
     * @return utils.
     */
    ArenaUtil getArenaUtil();
}
