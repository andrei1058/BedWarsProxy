package com.andrei1058.bedwars.proxy.language;

import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.configuration.ConfigPath;
import com.andrei1058.bedwars.proxy.configuration.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class Language extends PluginConfig {

    private String iso, prefix = "";
    private static HashMap<Player, Language> langByPlayer = new HashMap<>();
    private static List<Language> languages = new ArrayList<>();
    private static Language defaultLanguage;

    public Language(Plugin plugin, String iso) {
        super(plugin, "messages_" + iso, "plugins/" + plugin.getName() + "/Languages");
        this.iso = iso;
        languages.add(this);
    }

    /**
     * Set chat prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get scoreboard strings.
     */
    public static List<String> getScoreboard(Player p, String path, String alternative) {
        Language language = getPlayerLanguage(p);
        if (language.exists(path)) {
            return language.l(path);
        }
        return language.l(alternative);
    }

    /**
     * Get language display name.
     */
    public String getLangName() {
        return getYml().getString("name");
    }

    /**
     * Get message in player's language.
     */
    public static String getMsg(Player p, String path) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage()).m(path);
    }

    /**
     * Retrieve a player language.
     */
    public static Language getPlayerLanguage(Player p) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage());
    }

    /**
     * Check if a message was set.
     */
    public boolean exists(String path) {
        return getYml().get(path) != null;
    }

    /**
     * Get a string list in player's language.
     */
    public static List<String> getList(Player p, String path) {
        return langByPlayer.getOrDefault(p, getDefaultLanguage()).l(path);
    }

    /**
     * Save a value to file if not exists.
     */
    public static void saveIfNotExists(String path, Object data) {
        for (Language l : languages) {
            if (l.getYml().get(path) == null) {
                l.set(path, data);
            }
        }
    }

    /**
     * Get a color translated message.
     */
    public String m(String path) {
        return ChatColor.translateAlternateColorCodes('&', getYml().getString(path).replace("{prefix}", prefix));
    }

    /**
     * Get a color translated list.
     */
    public List<String> l(String path) {
        return getYml().getStringList(path).stream().map(s -> s.replace("&", "§")).collect(Collectors.toList());
    }

    public static HashMap<Player, Language> getLangByPlayer() {
        return langByPlayer;
    }

    /**
     * Check if a language exists.
     */
    public static boolean isLanguageExist(String iso) {
        for (Language l : languages) {
            if (l.iso.equalsIgnoreCase(iso)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get language with given info.
     *
     * @return null if could not find.
     */
    public static Language getLang(String iso) {
        for (Language l : languages) {
            if (l.iso.equalsIgnoreCase(iso)) {
                return l;
            }
        }
        return getDefaultLanguage();
    }

    /**
     * Get language iso code.
     */
    public String getIso() {
        return iso;
    }

    /**
     * Get loaded languages list.
     */
    public static List<Language> getLanguages() {
        return languages;
    }

    /**
     * Save messages for unset stats items.
     */
    /*public static void setupCustomStatsMessages() {
        BedWars api = Bukkit.getServer().getServicesManager().getRegistration(BedWars.class).getProvider();
        for (Language l : getLanguages()) {
            if (l == null) continue;
            if (l.getYml() == null) continue;
            if (api.getConfigs().getMainConfig().getYml().get("ConfigPath.GENERAL_CONFIGURATION_STATS_PATH") == null) return;
            for (String item : api.getConfigs().getMainConfig().getYml().getConfigurationSection(ConfigPath.GENERAL_CONFIGURATION_STATS_PATH).getKeys(false)) {
                if (ConfigPath.GENERAL_CONFIGURATION_STATS_GUI_SIZE.contains(item)) continue;
                if (l.getYml().getDefaults() == null || !l.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + item + "-name")) l.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + item + "-name", "Name not set");
                if (l.getYml().getDefaults() == null || !l.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + item + "-lore")) l.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + item + "-lore", Collections.singletonList("lore not set"));
            }
            l.save();
        }
    }*/

    /*@SuppressWarnings("WeakerAccess")
    public void addDefaultStatsMsg(YamlConfiguration yml, String path, String name, String... lore) {
        if (yml.getDefaults() == null || !yml.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + path + "-name")) yml.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + path + "-name", name);
        if (yml.getDefaults() == null || !yml.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + path + "-lore")) yml.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + path + "-lore", lore);
    }*/

    /**
     * Change a player language and refresh
     * scoreboard and custom join items.
     */
    public static void setPlayerLanguage(Player p, String iso, boolean onLogin) {

        if (onLogin) {
            if (getDefaultLanguage().getIso().equalsIgnoreCase(iso)) return;
        }

        Language newLang = Language.getLang(iso);

        if (!onLogin) {
            Language oldLang = Language.getLangByPlayer().containsKey(p) ? Language.getPlayerLanguage(p) : Language.getLanguages().get(0);
            //PlayerLangChangeEvent e = new PlayerLangChangeEvent(p, oldLang.getIso(), newLang.getIso());
            //Bukkit.getPluginManager().callEvent(e);
            //if (e.isCancelled()) return;
            //todo call event
        }

        if (Language.getLangByPlayer().containsKey(p)) {
            Language.getLangByPlayer().replace(p, newLang);
        } else {
            Language.getLangByPlayer().put(p, newLang);
        }
    }

    /**
     * Change server default language.
     */
    public static void setDefaultLanguage(Language defaultLanguage) {
        Language.defaultLanguage = defaultLanguage;
    }

    /**
     * Get server default language.
     */
    public static Language getDefaultLanguage() {
        return defaultLanguage;
    }
}
