package com.andrei1058.bedwars.proxy;

import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.language.LanguageManager;

public class API implements BedWars {
    /**
     * Get language util.
     *
     * @return utils.
     */
    @Override
    public LanguageUtil getLanguageUtil() {
        return LanguageManager.get();
    }

    /**
     * Get arena util.
     *
     * @return utils.
     */
    @Override
    public ArenaUtil getArenaUtil() {
        return ArenaManager.getInstance();
    }
}
