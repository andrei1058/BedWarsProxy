package com.andrei1058.bedwars.proxy.configuration;

import com.andrei1058.bedwars.proxy.language.English;
import com.andrei1058.bedwars.proxy.language.Language;
import com.andrei1058.bedwars.proxy.BedWarsProxy;
import com.andrei1058.bedwars.proxy.language.LanguageManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;

public class BedWarsConfig extends PluginConfig {

    public BedWarsConfig() {
        super(BedWarsProxy.getPlugin(), "config", BedWarsProxy.getPlugin().getDataFolder().getPath());

        YamlConfiguration yml = getYml();
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_PORT, 2019);
        yml.addDefault("language", "en");
        yml.addDefault(ConfigPath.GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP, "yourServer.com");
        yml.addDefault("storeLink", "https://www.spigotmc.org/resources/authors/39904/");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ALLOW_PARTIES, true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_RANDOMARENAS, true);
        yml.addDefault("database.enable", false);
        yml.addDefault("database.host", "localhost");
        yml.addDefault("database.database", "bedWars");
        yml.addDefault("database.user", "root");
        yml.addDefault("database.pass", "");
        yml.addDefault("database.port", 3306);
        yml.addDefault("database.ssl", false);
        yml.options().copyDefaults(true);

        yml.addDefault("levels-settings.default-name", "&7[{number}✩] ");
        yml.addDefault("levels-settings.progress-bar-symbol", "■");
        yml.addDefault("levels-settings.progress-bar-unlocked-color", "&b");
        yml.addDefault("levels-settings.progress-bar-locked-color", "&7");
        yml.addDefault("levels-settings.progress-bar-format", "&8 [{progress}&8]");
        yml.addDefault(ConfigPath.GENERAL_ENABLE_PARTY_CMD, true);

        //saveLobbyCommandItem("stats", "bw stats", false, String.valueOf(BedWarsProxy.getMaterialAdapter().getForCurrent("SKULL_ITEM", "SKULL_ITEM", "PLAYER_HEAD")), 3, 0);
        //saveLobbyCommandItem("arena-selector", "bw gui", true, "CHEST", 5, 4);
        //saveLobbyCommandItem("leave", "bw leave", false, String.valueOf(BedWarsProxy.getMaterialAdapter().getForCurrent("BED", "BED", "RED_BED")), 0, 8);

        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES, Collections.singletonList("your language iso here"));

        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE, 45);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SHOW_PLAYING, true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS, "10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "waiting"), "DIAMOND_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "waiting"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "waiting"), false);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "starting"), "GOLD_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "starting"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "starting"), true);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "playing"), "REDSTONE_BLOCK");
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "playing"), 0);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "playing"), false);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL.replace("%path%", "skipped-slot"), String.valueOf(BedWarsProxy.getMaterialAdapter().getForCurrent("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE")));
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA.replace("%path%", "skipped-slot"), 7);
        yml.addDefault(ConfigPath.GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED.replace("%path%", "skipped-slot"), false);

        save();

        //set default server language
        String whatLang = "en";
        new English();
        File[] langs = new File("plugins/" + BedWarsProxy.getPlugin().getDescription().getName() + "/Languages").listFiles();
        if (langs != null) {
            for (File f : langs) {
                if (f.isFile()) {
                    if (f.getName().startsWith("messages_") && f.getName().endsWith(".yml")) {
                        String lang = f.getName().replace("messages_", "").replace(".yml", "");
                        if (lang.equalsIgnoreCase(yml.getString("language"))) {
                            whatLang = f.getName().replace("messages_", "").replace(".yml", "");
                        }
                        if (LanguageManager.get().getLang(lang) == null) new Language(BedWarsProxy.getPlugin(), lang);
                    }
                }
            }
        }
        com.andrei1058.bedwars.proxy.api.Language def = LanguageManager.get().getLang(whatLang);

        if (def == null) throw new IllegalStateException("Could not found default language: " + whatLang);
        LanguageManager.get().setDefaultLanguage(def);

        //remove languages if disabled
        //server language can t be disabled
        for (String iso : yml.getStringList(ConfigPath.GENERAL_CONFIGURATION_DISABLED_LANGUAGES)) {
            com.andrei1058.bedwars.proxy.api.Language l = LanguageManager.get().getLang(iso);
            if (l != null) {
                if (l != def) LanguageManager.get().getLanguages().remove(l);
            }
        }
        //
    }

    /*
     * Add Lobby Command Item To
     * This won't create the item back if you delete it.
     */
    /*public void saveLobbyCommandItem(String name, String cmd, boolean enchanted, String material, int data, int slot) {
        if (isFirstTime()) {
            getYml().addDefault(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_COMMAND.replace("%path%", name), cmd);
            getYml().addDefault(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_MATERIAL.replace("%path%", name), material);
            getYml().addDefault(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_DATA.replace("%path%", name), data);
            getYml().addDefault(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_ENCHANTED.replace("%path%", name), enchanted);
            getYml().addDefault(ConfigPath.GENERAL_CONFIGURATION_LOBBY_ITEMS_SLOT.replace("%path%", name), slot);
            getYml().options().copyDefaults(true);
            save();
        }
    }*/

    /*public String getLobbyWorldName() {
        if (getYml().get("lobbyLoc") == null) return "";
        String d = getYml().getString("lobbyLoc");
        if (d == null) return "";
        String[] data = d.replace("[", "").replace("]", "").split(",");
        return data[data.length - 1];
    }*/
}
