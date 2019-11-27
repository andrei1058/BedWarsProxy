package com.andrei1058.bedwars.proxy.configuration;

public class ConfigPath {

    public static final String GENERAL_CONFIGURATION_DISABLED_LANGUAGES = "disabled-languages";

    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH = "lobby-items";

    public static final String GENERAL_CONFIG_PLACEHOLDERS_REPLACEMENTS_SERVER_IP = "server-ip";

    // Replace %path% with name
    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_MATERIAL = GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH + ".%path%.material";
    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_DATA = GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH + ".%path%.data";
    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_SLOT = GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH + ".%path%.slot";
    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_ENCHANTED = GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH + ".%path%.enchanted";
    public static final String GENERAL_CONFIGURATION_LOBBY_ITEMS_COMMAND = GENERAL_CONFIGURATION_LOBBY_ITEMS_PATH + ".%path%.command";

    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH = "arena-gui";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SIZE = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.inv-size";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_SHOW_PLAYING = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.show-playing";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_SETTINGS_USE_SLOTS = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".settings.use-slots";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_MATERIAL = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.material";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_DATA = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.data";
    public static final String GENERAL_CONFIGURATION_ARENA_SELECTOR_STATUS_ENCHANTED = GENERAL_CONFIGURATION_ARENA_SELECTOR_PATH + ".%path%.enchanted";
}
