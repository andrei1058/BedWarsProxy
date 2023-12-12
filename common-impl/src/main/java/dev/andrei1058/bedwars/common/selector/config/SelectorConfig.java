package dev.andrei1058.bedwars.common.selector.config;

import dev.andrei1058.bedwars.common.selector.SelectorManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SelectorConfig {
    public static final String SELECTOR_GENERIC_PATTERN_PATH = "pattern";
    public static final String SELECTOR_GENERIC_REPLACE_PATH = "replacements";

    private YamlConfiguration yml;
    private File config;
    private boolean firstTime = false;

    /**
     * Create a new configuration file.
     *
     * @param plugin config owner.
     * @param name   config name. Do not include .yml in it.
     */
    public SelectorConfig(Plugin plugin, String name) {
        if (!SelectorManager.getINSTANCE().getSelectorDirectory().exists()) {
            if (!SelectorManager.getINSTANCE().getSelectorDirectory().mkdir()) {
                plugin.getLogger().log(Level.SEVERE, "Could not create " + SelectorManager.getINSTANCE().getSelectorDirectory().getPath());
                return;
            }
        }

        config = new File(SelectorManager.getINSTANCE().getSelectorDirectory(), name + ".yml");
        if (!config.exists()) {
            firstTime = true;
            plugin.getLogger().log(Level.INFO, "Creating " + config.getPath());
            try {
                if (!config.createNewFile()) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create " + config.getPath());
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        yml = YamlConfiguration.loadConfiguration(config);
        yml.options().copyDefaults(true);

        yml.options().header("Plugin by andrei1058.\n" +
                "Replacements example:\n" +
                "'x':\n" +
                "  type: ARENA (arena type keeps item-stack configuration in the arena file.\n" +
                "  filter-template: none or templateName,skeld,etc\n" +
                "  filter-status: loading,separated,by,comma (available: loading,waiting,starting,in_game,ending,none)\n" +
                "'*':\n" +
                "  type: NONE or AIR for air.\n" +
                "'=':\n" +
                "  type: COMMAND or CMD for items that execute commands when clicked.\n" +
                "  commands: \n" +
                "    as-player: myStoreLink'\n' bw open someOtherGUI, etc\n" +
                "    as-console: openDonations {player}\n" +
                "  item:\n" +
                "    material: DIAMOND\n" +
                "    data:0 (yes, data. I'm supporting 1.12.)\n" +
                "    enchanted: false\n" +
                "    amount: 1");

        yml.addDefault("main." + SELECTOR_GENERIC_PATTERN_PATH, Arrays.asList("#########", "#***#####", "#####***#", "##-z#####", "#########"));

        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "type", "ARENA");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "filter-template", "");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "filter-status", "starting,waiting");

        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "type", "CMD");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "commands.as-player", "au join");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "commands.as-console", "");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "item.material", "EMERALD");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "item.data", 0);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "item.enchanted", true);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".-." + "item.amount", 1);

        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "type", "CMD");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "commands.as-player", "au selector spectate");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "commands.as-console", "");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "item.material", "ELYTRA"
//                Objects.requireNonNull(CommonManager.getSingleton().getMaterialSupport().getForCurrent(
//                        "FLINT", "ELYTRA", "ELYTRA")
//                ).toString()
        );
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "item.data", 0);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "item.enchanted", true);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".z." + "item.amount", 1);

        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "type", "ITEM");
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.material", "GRAY_STAINED_GLASS_PANE"
//                Objects.requireNonNull(CommonManager.getSingleton().getMaterialSupport().getForCurrent(
//                        "STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"
//                )).toString()
        );
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.data", 7);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.enchanted", false);
        yml.addDefault("main." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.amount", 1);

        yml.addDefault("spectate." + SELECTOR_GENERIC_PATTERN_PATH, Arrays.asList("#########", "#*******#", "#*******#", "#*******#", "4########"));

        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "type", "ARENA");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "filter-template", "none");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".*." + "filter-status", "started");

        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "type", "ITEM");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.material", "GRAY_STAINED_GLASS_PANE"
//                Objects.requireNonNull(CommonManager.getSingleton().getMaterialSupport().getForCurrent(
//                        "STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"
//                )).toString()
        );
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.data", 7);
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.enchanted", false);
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".#." + "item.amount", 1);

        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "type", "CMD");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "commands.as-player", "au selector");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "commands.as-console", "");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "item.material", "CHEST");
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "item.data", 0);
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "item.enchanted", true);
        yml.addDefault("spectate." + SELECTOR_GENERIC_REPLACE_PATH + ".4." + "item.amount", 1);
        save();
    }

    /**
     * Reload configuration.
     */
    @SuppressWarnings("unused")
    public void reload() {
        yml = YamlConfiguration.loadConfiguration(config);
    }

    /**
     * Set data to config
     */
    public void set(String path, Object value) {
        yml.set(path, value);
        save();
    }

    /**
     * Get yml instance
     */
    public YamlConfiguration getYml() {
        return yml;
    }

    /**
     * Save config changes to file
     */
    public void save() {
        try {
            yml.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get list of strings at given path
     *
     * @return a list of string with colors translated
     */
    public List<String> getList(String path) {
        return yml.getStringList(path).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    /**
     * Get boolean at given path
     */
    public boolean getBoolean(String path) {
        return yml.getBoolean(path);
    }

    /**
     * Get Integer at given path
     */
    @SuppressWarnings("unused")
    public int getInt(String path) {
        return yml.getInt(path);
    }


    /**
     * Get string at given path
     */
    public String getString(String path) {
        return yml.getString(path);
    }

    /**
     * Check if the config file was created for the first time
     * Can be used to add default values
     */
    @SuppressWarnings("unused")
    public boolean isFirstTime() {
        return firstTime;
    }
}
