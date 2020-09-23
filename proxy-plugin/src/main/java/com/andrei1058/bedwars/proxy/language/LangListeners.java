package com.andrei1058.bedwars.proxy.language;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;
import java.util.UUID;

public class LangListeners implements Listener {

    private static HashMap<UUID, String> preLoadedLanguage = new HashMap<>();

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e == null) return;
        Player p = e.getPlayer();
        final UUID u = p.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(BedWarsProxy.getPlugin(), () -> {
            String iso = BedWarsProxy.getRemoteDatabase().getLanguage(u);
            if (LanguageManager.get().isLanguageExist(iso)) {
                if (BedWarsProxy.config.getYml().getStringList(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES).contains(iso))
                    iso = LanguageManager.get().getDefaultLanguage().getIso();
                if (preLoadedLanguage.containsKey(u)) {
                    preLoadedLanguage.replace(u, iso);
                } else {
                    preLoadedLanguage.put(u, iso);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        if (e == null) return;
        final Player p = e.getPlayer();
        if (preLoadedLanguage.containsKey(e.getPlayer().getUniqueId())) {
            LanguageManager.get().setPlayerLanguage(e.getPlayer(), preLoadedLanguage.get(e.getPlayer().getUniqueId()), true);
            preLoadedLanguage.remove(e.getPlayer().getUniqueId());
        }
    }
}
