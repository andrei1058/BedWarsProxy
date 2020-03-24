package com.andrei1058.bedwars.proxy.language;

import com.andrei1058.bedwars.proxy.api.BedWars;
import com.andrei1058.bedwars.proxy.api.Language;
import com.andrei1058.bedwars.proxy.api.event.PlayerLangChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanguageManager implements BedWars.LanguageUtil {

    private Language defaultLanguage;
    private HashMap<Player, Language> langByPlayer = new HashMap<>();
    private List<com.andrei1058.bedwars.proxy.api.Language> languages = new ArrayList<>();
    private static LanguageManager INSTANCE;

    private LanguageManager(){
        INSTANCE = this;
    }

    public static void init(){
        new LanguageManager();
    }

    public static LanguageManager get(){
        return INSTANCE;
    }

    @Override
    public List<Language> getLanguages() {
        return languages;
    }

    @Override
    public String getMsg(Player p, String path) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage()).getMsg(path);
    }

    @Override
    public Language getPlayerLanguage(Player p) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage());
    }

    @Override
    public List<String> getList(Player p, String path) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage()).getList(path);
    }

    @Override
    public void saveIfNotExists(String path, Object data) {
        for (Language l : languages) {
            if (!l.exists(path)) {
                l.set(path, data);
            }
        }
    }

    @Override
    public boolean isLanguageExist(String iso) {
        return languages.stream().anyMatch(p -> p.getIso().equalsIgnoreCase(iso));
    }

    @Override
    public boolean addLanguage(Language language){
        if (language == null || isLanguageExist(language.getIso())) return false;
        return languages.add(language);
    }

    @Override
    public Language getLang(String iso) {
        for (Language l : languages) {
            if (l.getIso().equalsIgnoreCase(iso)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Change a player language and refresh
     * scoreboard and custom join items.
     * @param p target player.
     * @param iso language iso.
     * @param onLogin if it is requested at login.
     */
    public void setPlayerLanguage(Player p, String iso, boolean onLogin) {

        if (onLogin) {
            if (getDefaultLanguage().getIso().equalsIgnoreCase(iso)) return;
        }

        Language newLang = get().getLang(iso);

        if (!onLogin) {
            Language oldLang = langByPlayer.containsKey(p) ? getPlayerLanguage(p) : getLanguages().get(0);
            PlayerLangChangeEvent e = new PlayerLangChangeEvent(p, oldLang, newLang);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return;
        }

        if (langByPlayer.containsKey(p)) {
            langByPlayer.replace(p, newLang);
        } else {
            langByPlayer.put(p, newLang);
        }
    }

    @Override
    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
